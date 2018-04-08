package com.ecc.service.contract;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.block.BlockService;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.BlockApi;
import com.ecc.web.api.BlockServiceApi;
import com.ecc.web.api.ContractServiceApi;
import com.ecc.web.api.FileServiceApi;
import com.ecc.web.api.UserServiceApi;
import com.ecc.web.exceptions.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyException;
import java.security.PrivateKey;

@Service
public class ContractService {
    @Autowired
    BlockService blockService;
    @Autowired
    FileServiceApi fileServiceApi;
    @Autowired
    ContractServiceApi contractServiceApi;
    @Autowired
    UserServiceApi userServiceApi;
    @Autowired
    BlockServiceApi blockServiceApi;

    public void receiverSignContract(Contract contract) throws ContractException, KeyException {
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        FileTransaction transaction = fileServiceApi.getTransaction(contract.getTransactionId(), contract.getTransactionType());

        if (!transaction.hash().equals(contract.getTransactionHash())) {
            throw new ContractException("Transaction.hash != contract.transaction.hash");
        }

        String publicKey = userServiceApi.getPeer(contract.getSenderSign(), "").getPublicKey();
        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getPeer().getEmail()).getPrivateKey();

        if (contractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract,
                RsaUtil.getPublicKeyFromString(publicKey))) {
            contractHandler.sign(Contract.RECEIVER_SIGN, contract, peerPrivateKey);
            contractServiceApi.uploadReceiverSignedContract(contract);
            blockService.addContractToBlock(contract);
            return;
        }

        throw new ContractException("Contract verify failed!");
    }

    public void verifyContract(Contract contract) throws ContractException, KeyException {
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        FileTransaction transaction = fileServiceApi.getTransaction(contract.getTransactionId(), contract.getTransactionType());

        if (!transaction.hash().equals(contract.getTransactionHash())) {
            throw new ContractException("Transaction.hash != contract.transaction.hash");
        }

        String senderPublicKey = userServiceApi.getPeer(transaction.getOwner(), "").getPublicKey();
        String receiverPublicKey = userServiceApi.getPeer(transaction.getHolder(), "").getPublicKey();

        if (contractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract, RsaUtil.getPublicKeyFromString(senderPublicKey))
                && contractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract, RsaUtil.getPublicKeyFromString(receiverPublicKey))) {

            contract.setVerifyResult("Y");
            //成功 加入block中
            blockService.addContractToBlock(contract);
        } else {
            contract.setVerifyResult("N");
        }
        contract.setVerifier(Peer.getPeer().getEmail());
        String message = contract.getRawMessage() + contract.getSenderSign() + contract.getReceiverSign();
        contract.setVerifierSign(RsaUtil.sign(message, RsaUtil.loadKeyPair(Peer.getPeer().getEmail()).getPrivateKey()));

        //todo: 发回给block-service
        blockServiceApi.receiveVerifiedContract(contract);
    }
}
