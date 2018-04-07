package com.ecc.service.contract;

import com.ecc.dao.contract.ContractMapper;
import com.ecc.domain.contract.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractService {
    @Autowired
    ContractMapper contractMapper;

    public void saveTempContract(Contract contract){
        contractMapper.addContract(contract);
    }

    public List<Contract> getTop10Contracts(){
        return contractMapper.getTop10Contracts();
    }

    public int getContractsCount(){
        return contractMapper.getContractsCount();
    }

    public void deleteContract(Contract contract){
        contractMapper.deleteContractById(contract.getId());
    }

    public void deleteContract(String contractId){
        contractMapper.deleteContractById(contractId);
    }
}
