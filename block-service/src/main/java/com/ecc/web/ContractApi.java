package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.service.contract.ContractService;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transfer.TransferService;
import com.ecc.web.api.ContractServiceApi;
import com.ecc.web.api.FileServiceApi;
import com.ecc.web.exceptions.ContractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
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

    @PostMapping("contract")
    public void receiverSign(@RequestBody Contract contract) throws ContractException {
        contractService.receiverSignContract(contract);
    }
}
