package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.service.contract.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("contract")
public class ContractApi {
    @Autowired
    ContractService contractService;

    @PostMapping("sign")
    public void receiverSign(@RequestBody Contract contract) throws Exception {
        contractService.receiverSignContract(contract);
    }

    @PostMapping("verify")
    public void verifyContract(@RequestBody Contract contract) throws Exception {
        contractService.verifyContract(contract);
    }
}
