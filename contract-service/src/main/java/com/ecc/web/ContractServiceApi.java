package com.ecc.web;

import com.ecc.dao.ContractMapper;
import com.ecc.domain.contract.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ContractServiceApi {
    @Autowired
    ContractMapper contractMapper;

    @PutMapping("contract")
    public void addContract(@RequestBody Contract contract) {
        try {
            contractMapper.addContract(contract);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @GetMapping("contracts")
    public List<Contract> getTop10contracts(@RequestParam("limit") Integer limit) {
        try {
            return contractMapper.getTopContracts(limit);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("contract")
    public int getContractCount() {
        try {
            return contractMapper.getContractsCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @DeleteMapping("contract")
    public void deleteContract(@RequestParam("id") String id) {
        try {
            contractMapper.deleteContractById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
