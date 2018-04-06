package com.ecc.service.contract;

import com.ecc.domain.peer.Peer;
import com.ecc.service.block.BlockService;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transaction.TransactionService;
import com.ecc.service.transfer.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BlockService blockService;
    @Autowired
    TransferService transferService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PeerService peerService;
}
