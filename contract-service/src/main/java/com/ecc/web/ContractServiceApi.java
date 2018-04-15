package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.ContractService;
import com.ecc.service.RestTemplate;
import com.ecc.service.contract.ContractHandler;
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

    @PostMapping("verify")
    public void verifyReceiverSignedContract(@RequestBody Contract contract) throws ContractException {
        contractService.verifyReceiverSignedContract(contract);
    }

    @PostMapping("upload")
    public void receiveSenderSignedContract(@RequestBody Contract contract) {
        contractService.receiveSenderSignedContract(contract);
    }
}
