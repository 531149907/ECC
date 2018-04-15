package com.ecc.service;

import com.ecc.dao.ContractMapper;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.api.BlockServiceApi;
import com.ecc.web.api.FileServiceApi;
import com.ecc.web.api.PeerApi;
import com.ecc.web.api.UserServiceApi;
import com.ecc.web.exceptions.ContractException;
import com.google.gson.Gson;
import feign.Feign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ContractService {
    @Autowired
    ContractMapper contractMapper;
    @Autowired
    UserServiceApi userServiceApi;
    @Autowired
    FileServiceApi fileServiceApi;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BlockServiceApi blockServiceApi;
    @Autowired
    TaskExecutor taskExecutor;

    public void verifyReceiverSignedContract(Contract contract) throws ContractException {
        FileTransaction transaction = fileServiceApi.getTransaction(contract.getTransactionId(),
                contract.getTransactionType());
        String holder = transaction.getHolder();
        String publicKey = userServiceApi.getPeer(holder, "").getPublicKey();
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        if (contractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract,
                RsaUtil.getPublicKeyFromString(publicKey))) {
            contractMapper.addContract(contract);
            System.out.println("Contract verified! Sending to Block Service!");
            blockServiceApi.sendToBlockService(contract);
            return;
        }
        throw new ContractException("Contract verify failed!", 500);
    }

    public void receiveSenderSignedContract(Contract contract) {
        if (contract.getTransactionType().equals(TransactionType.TICKET)) {
            contractMapper.addContract(contract);
            System.out.println("Contract verified! Sending to Block Service!");
            blockServiceApi.sendToBlockService(contract);
            return;
        }
        taskExecutor.execute(() -> {
            FileTransaction transaction = fileServiceApi.getTransaction(contract.getTransactionId(),
                    contract.getTransactionType());
            String holder = transaction.getHolder();
            String host = userServiceApi.getPeer(holder, "").getIp();
            Integer port = userServiceApi.getPeer(holder, "").getPort();

            HashMap<String, Object> params = new HashMap<>();
            params.put("contract", contract);
            System.out.println(">>>>> "+host + ":" + port + "/contract/sign");
            PeerApi peerApi = Feign.builder()
                    .target(PeerApi.class,"http://"+host + ":" + port);
            try {
                peerApi.receiverSign(new Gson().toJson(contract));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //restTemplate.post(host + ":" + port + "/contract/sign", params, null,true);
        });
    }
}
