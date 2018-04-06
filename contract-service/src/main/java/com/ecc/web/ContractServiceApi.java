package com.ecc.web;

import com.ecc.dao.contract.ContractMapper;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.api.FileService;
import com.ecc.web.api.UserService;
import com.ecc.web.exceptions.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class ContractServiceApi {

    @Autowired
    ContractMapper contractMapper;
    @Autowired
    UserService userService;
    @Autowired
    FileService fileService;
    @Autowired
    RestTemplate restTemplate;

    @PostMapping("verify")
    public void verifySignedContract(@RequestBody Contract contract) throws ContractException {
        FileTransaction transaction = fileService.getTransaction(contract.getTransactionId(),
                contract.getTransactionType());
        String holder = transaction.getHolder();
        String publicKey = userService.getPeer(holder, "").getPublicKey();
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        if (contractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract,
                RsaUtil.getPublicKeyFromString(publicKey))) {
            //todo: sent to block service
            System.out.println("Finished! contract verified!");
            return;
        }
        throw new ContractException("Contract verify failed!", 500);
    }

    @PostMapping("upload")
    public void receiveContract(@RequestBody Contract contract) {
        FileTransaction transaction = fileService.getTransaction(contract.getTransactionId(),
                contract.getTransactionType());
        String holder = transaction.getHolder();
        String host = userService.getPeer(holder, "").getIp();
        Integer port = userService.getPeer(holder, "").getPort();

        HashMap<String, Object> params = new HashMap<>();
        params.put("contract", contract);
        restTemplate.post(host + ":" + port + "/contract", params, null);
    }
}
