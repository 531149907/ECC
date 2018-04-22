package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.handler.BlockHandler;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("block")
public class BlockApi {

    @GetMapping("contracts")
    public List<Contract> getTempContract(String contractIds) {
        String[] ids = contractIds.split(",");
        List<Contract> contracts = new ArrayList<>();
        for (String id : ids) {
            Contract contract = BlockHandler.getContractFromTempBlock(id);
            if (contract != null) {
                contracts.add(contract);
            }
        }
        return contracts;
    }

}
