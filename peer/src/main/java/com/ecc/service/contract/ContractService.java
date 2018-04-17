package com.ecc.service.contract;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.RestTemplate;
import com.ecc.service.block.BlockService;
import com.ecc.service.contract.impl.ContractHandlerImpl;
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

    public void receiverSignContract(Contract contract) throws Exception {
        /*ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        HashMap<String, Object> params = new HashMap<>();
        params.put("transactionId", contract.getTransactionId());
        params.put("transactionType", contract.getTransactionType());
        params.put("token", Peer.getInstance().getToken());
        FileTransaction transaction = restTemplate.get(SERVER_URL + "file-service/transaction", params, FileTransaction.class);

        if (!transaction.hash().equals(contract.getTransactionHash())) {
            throw new Exception("Transaction.hash != contract.transaction.hash");
        }

        params = new HashMap<>();
        params.put("email", transaction.getOwner());
        params.put("token", Peer.getInstance().getToken());

        String senderPublicKey = restTemplate.get(SERVER_URL + "user-service/peer", params, Peer.class).getPublicKey();
        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getInstance().getEmail()).getPrivateKey();

        if (contractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract,
                RsaUtil.getPublicKeyFromString(senderPublicKey))) {
            contractHandler.sign(Contract.RECEIVER_SIGN, contract, peerPrivateKey);
            taskExecutor.execute(() -> {
                HashMap<String, Object> params1 = new HashMap<>();
                params1.put("contract", contract);
                params1.put("token", Peer.getInstance().getToken());
                restTemplate.post(SERVER_URL + "contract-service/verify", params1, null);
            });
            taskExecutor.execute(() -> {
                //todo: add to block-service
                //blockService.addContractToBlock(contract);
            });
            return;
        }

        throw new Exception("Contract verify failed!");*/
    }

    public void verifyContract(Contract contract) throws Exception {
        /*ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        HashMap<String, Object> params = new HashMap<>();
        params.put("transactionId", contract.getTransactionId());
        params.put("transactionType", contract.getTransactionType());
        params.put("token", Peer.getInstance().getToken());
        FileTransaction transaction = restTemplate.get(SERVER_URL + "file-service/transaction", params, FileTransaction.class);

        if (!transaction.hash().equals(contract.getTransactionHash())) {
            throw new Exception("Transaction.hash != contract.transaction.hash");
        }

        params = new HashMap<>();
        params.put("email", transaction.getOwner());
        params.put("token", Peer.getInstance().getToken());
        String senderPublicKey = restTemplate.get(SERVER_URL + "user-service/peer", params, Peer.class).getPublicKey();

        params.put("email",transaction.getHolder());
        String receiverPublicKey = restTemplate.get(SERVER_URL + "user-service/peer", params, Peer.class).getPublicKey();

        if (contractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract, RsaUtil.getPublicKeyFromString(senderPublicKey))
                && contractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract, RsaUtil.getPublicKeyFromString(receiverPublicKey))) {

            contract.setVerifyResult("Y");
            //todo: 成功 加入block中
            //blockService.addContractToBlock(contract);
        } else {
            contract.setVerifyResult("N");
        }
        contract.setVerifier(Peer.getInstance().getEmail());
        String message = contract.getRawMessage() + contract.getSenderSign() + contract.getReceiverSign();
        contract.setVerifierSign(RsaUtil.sign(message, RsaUtil.loadKeyPair(Peer.getInstance().getEmail()).getPrivateKey()));

        taskExecutor.execute(() -> {
            //todo: 发回给block-service
            //blockServiceApi.receiveVerifiedContract(contract);
        });*/
    }
}
