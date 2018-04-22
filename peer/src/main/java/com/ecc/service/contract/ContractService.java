package com.ecc.service.contract;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.handler.ContractHandler;
import com.ecc.service.RestTemplate;
import com.ecc.service.block.BlockService;
import com.ecc.util.crypto.RsaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.HashMap;

import static com.ecc.constants.ApplicationConstants.SERVER_URL;

@Service
public class ContractService {
    @Autowired
    BlockService blockService;
    @Autowired
    TaskExecutor taskExecutor;
    @Autowired
    RestTemplate restTemplate;

    public void receiverSignContract(Contract contract) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", contract.getTransactionId());
        params.put("token", Peer.getInstance().getToken());
        FileTransaction transaction = restTemplate.get(SERVER_URL + "api/file-service/transaction/file", params, FileTransaction.class);
/*
        String contractTransactionHash = contract.getTransactionHash();
        String transactionHash = transaction.hash();
        if (!transactionHash.equals(contractTransactionHash)) {
            throw new CustomException(ExceptionCollection.CONTRACT_VERIFY_FAILED);
        }
*/
        params = new HashMap<>();
        params.put("email", transaction.getOwner());
        params.put("token", Peer.getInstance().getToken());
        String senderPublicKey = restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class).getPublicKey();
        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getInstance().getEmail()).getPrivateKey();

        if (ContractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract, RsaUtil.getPublicKeyFromString(senderPublicKey))) {
            ContractHandler.sign(Contract.RECEIVER_SIGN, contract, peerPrivateKey);
            taskExecutor.execute(() -> {
                HashMap<String, String> params1 = new HashMap<>();
                params1.put("token", Peer.getInstance().getToken());
                restTemplate.post(SERVER_URL + "api/contract-service/verify/receiver", params1, contract, null);
            });
            //todo: add contract to local block
            blockService.addContractToBlock(contract);
            return;
        }

        throw new CustomException(ExceptionCollection.CONTRACT_VERIFY_FAILED);
    }

    public void verifyContract(Contract contract) {
        HashMap<String, String> params = new HashMap<>();
        params.put("id", contract.getTransactionId());
        params.put("token", Peer.getInstance().getToken());
        FileTransaction transaction = restTemplate.get(SERVER_URL + "api/file-service/transaction/file/id", params, FileTransaction.class);

        if (!transaction.hash().equals(contract.getTransactionHash())) {
            throw new CustomException(ExceptionCollection.CONTRACT_VERIFY_FAILED);
        }

        params = new HashMap<>();
        params.put("email", transaction.getOwner());
        params.put("token", Peer.getInstance().getToken());
        String senderPublicKey = restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class).getPublicKey();

        params.put("email", transaction.getHolder());
        params.put("token", Peer.getInstance().getToken());
        String receiverPublicKey = restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class).getPublicKey();

        if (ContractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract, RsaUtil.getPublicKeyFromString(senderPublicKey))
                && ContractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract, RsaUtil.getPublicKeyFromString(receiverPublicKey))) {

            contract.setVerifyResult("Y");
            //todo: add contract to local block
            blockService.addContractToBlock(contract);
        } else {
            contract.setVerifyResult("N");
        }
        contract.setVerifier(Peer.getInstance().getEmail());
        String message = contract.getRawMessage() + contract.getSenderSign() + contract.getReceiverSign();
        contract.setVerifierSign(RsaUtil.sign(message, RsaUtil.loadKeyPair(Peer.getInstance().getEmail()).getPrivateKey()));

        taskExecutor.execute(() -> {
            //todo: 发回给contract-service
            //blockServiceApi.receiveVerifiedContract(contract);
        });
    }
}
