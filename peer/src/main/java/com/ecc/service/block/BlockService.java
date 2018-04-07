package com.ecc.service.block;

import com.ecc.domain.contract.Contract;
import com.ecc.service.block.impl.BlockHandlerImpl;
import org.springframework.stereotype.Service;

@Service
public class BlockService {

    public void addContractToBlock(Contract contract) {
        BlockHandler blockHandler = BlockHandlerImpl.getHandler();
        blockHandler.addContractToBlock(contract);
    }
}
