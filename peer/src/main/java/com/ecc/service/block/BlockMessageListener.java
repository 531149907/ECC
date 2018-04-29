package com.ecc.service.block;

import com.ecc.domain.block.Block;
import com.ecc.domain.broadcast.BroadcastContent;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.handler.BlockHandler;
import com.ecc.service.RestTemplate;
import com.ecc.service.contract.ContractService;
import com.ecc.util.converter.BytesUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ecc.constants.ApplicationConstants.SERVER_URL;

@Component
public class BlockMessageListener {
    @Autowired
    ContractService contractService;
    @Autowired
    RestTemplate restTemplate;

    @RabbitListener(queues = "main")
    public void process(Message message) {
        HashMap<String, Object> rawMessage = (HashMap<String, Object>) BytesUtil.toObject(message.getBody());
        String index;
        for (Map.Entry<String, Object> entry : rawMessage.entrySet()) {
            if (entry.getKey().split(":")[0].equals(BroadcastContent.ACTION_BLOCK_RECEIVE)) {
                index = entry.getKey().split(":")[1];
                HashMap<String, List<Contract>> contracts = (HashMap<String, List<Contract>>) BytesUtil.toObject(message.getBody());
                List<Contract> verifiedContracts = new ArrayList<>();

                for (Map.Entry<String, List<Contract>> entry1 : contracts.entrySet()) {
                    for (Contract contract : entry1.getValue()) {
                        verifiedContracts.add(contractService.verifyContract(contract));
                    }
                }

                HashMap<String, String> params = new HashMap<>();
                params.put("key", Peer.getInstance().getEmail());
                params.put("index", index);
                restTemplate.post(SERVER_URL + "api/block-service/block/contract/verify", params, verifiedContracts, null);

            } else if (entry.getKey().split(":")[0].equals(BroadcastContent.ACTION_BLOCK_IMPORT)) {
                HashMap<String, Block> newBlockMap = (HashMap<String, Block>) BytesUtil.toObject(message.getBody());
                for (Map.Entry<String, Block> entry1 : newBlockMap.entrySet()) {
                    BlockHandler.verifyAndImportBlock(entry1.getValue());
                }
            }
        }
    }
}
