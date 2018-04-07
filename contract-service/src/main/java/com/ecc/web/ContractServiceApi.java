package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.RestTemplate;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.ContractService;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.api.BlockServiceApi;
import com.ecc.web.api.FileServiceApi;
import com.ecc.web.api.UserServiceApi;
import com.ecc.web.exceptions.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class ContractServiceApi {

    @Autowired
    ContractService contractService;
    @Autowired
    UserServiceApi userServiceApi;
    @Autowired
    FileServiceApi fileServiceApi;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BlockServiceApi blockServiceApi;

    @PostMapping("verify")
    public void verifyReceiverSignedContract(@RequestBody Contract contract) throws ContractException {
        FileTransaction transaction = fileServiceApi.getTransaction(contract.getTransactionId(),
                contract.getTransactionType());
        String holder = transaction.getHolder();
        String publicKey = userServiceApi.getPeer(holder, "").getPublicKey();
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        if (contractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract,
                RsaUtil.getPublicKeyFromString(publicKey))) {
            contractService.saveTempContract(contract);
            System.out.println("Contract verified! Waiting for 10 contracts and send to Block Service!");
            blockServiceApi.sendToBlockService(contract);
            return;
        }
        throw new ContractException("Contract verify failed!", 500);
    }

    @PostMapping("upload")
    public void receiveSenderSignedContract(@RequestBody Contract contract) {
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
