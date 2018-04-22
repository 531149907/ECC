package com.ecc.service;

import com.ecc.domain.broadcast.BroadcastContent;
import com.ecc.domain.contract.Contract;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class BlockService {

    @Autowired
    AmqpTemplate rabbitTemplate;

    public void broadcastContracts(List<Contract> contracts, int index) {
        HashMap<String, List<Contract>> params = new HashMap<>();
        params.put(BroadcastContent.ACTION_BLOCK_RECEIVE + ":" + index, contracts);
        rabbitTemplate.convertAndSend("main", params);
    }
}
