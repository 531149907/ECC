package com.ecc.service;

import com.ecc.domain.block.Block;
import com.ecc.domain.broadcast.BroadcastContent;
import com.ecc.domain.contract.Contract;
import com.ecc.handler.MerkleTreeHandler;
import com.ecc.util.converter.DateUtil;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class OrdererService {

    @Autowired
    AmqpTemplate rabbitTemplate;

    public void sendImportBlockBroadcast(String preHash, Integer index, List<Contract> contractList) {
        Block newBlock = Block.builder()
                .index(index)
                .contracts(contractList)
                .prevHash(preHash)
                .timestamp(DateUtil.getDate())
                .build();
        newBlock.setMerkleTreeRoot(MerkleTreeHandler.buildTree(newBlock));

        HashMap<String, Object> params = new HashMap<>();
        params.put(BroadcastContent.ACTION_BLOCK_IMPORT + ":" + index, newBlock);
        rabbitTemplate.convertAndSend("main", params);
    }
}
