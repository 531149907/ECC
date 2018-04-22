package com.ecc.service.block;

import com.ecc.domain.contract.Contract;
import com.ecc.handler.BlockHandler;
import org.springframework.stereotype.Service;

@Service
public class BlockService {

    public void addContractToBlock(Contract contract) {
        BlockHandler.addContractToBlock(contract);
    }
}
