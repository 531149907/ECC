package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.service.contract.ContractService;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transfer.TransferService;
import com.ecc.web.api.ContractServiceApi;
import com.ecc.web.api.FileServiceApi;
import com.ecc.web.exceptions.ContractException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.KeyException;

@RestController
@RequestMapping("contract")
public class ContractApi {
    @Autowired
    PeerService peerService;
    @Autowired
    TransferService transferService;
    @Autowired
    ContractService contractService;

    @Autowired
    FileServiceApi fileServiceApi;
    @Autowired
    ContractServiceApi contractServiceApi;

    @PostMapping("sign")
    public void receiverSign(@RequestParam("contract") Contract contract) throws ContractException, KeyException {
        System.out.println("init");
        contract.show();
        contractService.receiverSignContract(contract);
    }

    @PostMapping("verify")
    public void verifyContract(@RequestParam("contract") Contract contract) throws KeyException, ContractException {
        System.out.println("init");
        contract.show();
        contractService.verifyContract(contract);
    }
}
