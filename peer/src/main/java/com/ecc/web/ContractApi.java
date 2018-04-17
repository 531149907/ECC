package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.service.contract.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("contract")
public class ContractApi {
    @Autowired
    ContractService contractService;

    @PostMapping("sign")
    public void receiverSign(@RequestParam("contract") Contract contract) throws Exception {
        System.out.println("init");
        contract.show();
        contractService.receiverSignContract(contract);
    }

    @PostMapping("verify")
    public void verifyContract(@RequestParam("contract") Contract contract) throws Exception {
        System.out.println("init");
        contract.show();
        contractService.verifyContract(contract);
    }
}
