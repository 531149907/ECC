package com.ecc.service;

import com.ecc.dao.ContractMapper;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.api.BlockServiceApi;
import com.ecc.web.api.FileServiceApi;
import com.ecc.web.api.UserServiceApi;
import com.ecc.web.exceptions.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    public void verifyReceiverSignedContract(Contract contract) throws ContractException {
        FileTransaction transaction = fileServiceApi.getTransaction(contract.getTransactionId(),
                contract.getTransactionType());
        String holder = transaction.getHolder();
        String publicKey = userServiceApi.getPeer(holder, "").getPublicKey();
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        if (contractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract,
                RsaUtil.getPublicKeyFromString(publicKey))) {
            contractMapper.addContract(contract);
            System.out.println("Contract verified! Waiting for 10 contracts and send to Block Service!");
            blockServiceApi.sendToBlockService(contract);
            return;
        }
        throw new ContractException("Contract verify failed!", 500);
    }

    public void receiveSenderSignedContract(Contract contract) {
        FileTransaction transaction = fileServiceApi.getTransaction(contract.getTransactionId(),
                contract.getTransactionType());
        String holder = transaction.getHolder();
        String host = userServiceApi.getPeer(holder, "").getIp();
        Integer port = userServiceApi.getPeer(holder, "").getPort();

        HashMap<String, Object> params = new HashMap<>();
        params.put("contract", contract);
        restTemplate.post(host + ":" + port + "/contract", params, null);
    }
}
