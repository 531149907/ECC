package com.ecc.service;

import com.ecc.api.ContractServiceApi;
import com.ecc.api.FileServiceApi;
import com.ecc.api.UserServiceApi;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.handler.BaseContractHandler;
import com.ecc.util.crypto.RsaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ContractService {
    @Autowired
    ContractServiceApi contractService;
    @Autowired
    FileServiceApi fileService;
    @Autowired
    UserServiceApi userService;
    @Autowired
    TaskExecutor taskExecutor;
    @Autowired
    RestTemplate restTemplate;

    public void verifyReceiverSignedContract(Contract contract) {
        FileTransaction transaction = fileService.getFileTransaction(contract.getTransactionId());
        String holder = transaction.getHolder();
        String publicKey = userService.getPeerByEmail(holder).getPublicKey();
        try {
            if (BaseContractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract,
                    RsaUtil.getPublicKeyFromString(publicKey))) {
                contractService.addContract(contract);
                //todo: send contract to block-service
                //blockServiceApi.sendToBlockService(contract);
            }
        } catch (Exception e) {
            throw new CustomException(ExceptionCollection.CONTRACT_VERIFY_FAILED);
        }
    }

    public void receiveSenderSignedContract(Contract contract) {
        if (contract.getTransactionType().equals(TransactionType.TICKET)) {
            //ticket只需要sender签名
            contractService.addContract(contract);
            //todo: send contract to block-service
            //blockServiceApi.sendToBlockService(contract);
            return;
        }
        taskExecutor.execute(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            FileTransaction transaction = fileService.getFileTransaction(contract.getTransactionId());
            String holder = transaction.getHolder();
            Peer peer = userService.getPeerByEmail(holder);
            restTemplate.post(peer.getIp() + ":" + peer.getPort() + "/contract/sign", null, contract, null);
        });
    }
}
