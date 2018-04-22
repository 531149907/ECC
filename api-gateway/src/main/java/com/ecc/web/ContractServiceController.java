package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.service.ContractService;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("contract-service")
public class ContractServiceController {
    @Autowired
    ContractService contractService;

    @PostMapping("verify/receiver")
    public void verifyReceiverSignedContract(@RequestBody Contract contract) {
        contractService.verifyReceiverSignedContract(contract);
    }

    @PostMapping("verify/sender")
    public void receiveSenderSignedContract(@RequestBody Contract contract) {
        contractService.receiveSenderSignedContract(contract);
    }

}
