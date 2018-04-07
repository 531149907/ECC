package com.ecc.service.block;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.service.block.impl.BlockHandlerImpl;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.service.contract.ContractService;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transaction.TransactionService;
import com.ecc.service.transfer.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlockService {

    public void addContractToBlock(Contract contract){
        BlockHandler blockHandler = BlockHandlerImpl.getHandler();
        blockHandler.addContractToBlock(contract);
    }
}
