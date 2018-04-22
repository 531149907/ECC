package com.ecc.web;

import com.ecc.api.BlockServiceApi;
import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;
import com.ecc.service.runner.ContractCollectorRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("block-service")
public class BlockServiceController {

    @Autowired
    BlockServiceApi blockService;
    @Autowired
    ContractCollectorRunner contractCollectorRunner;

    @GetMapping("blocks")
    List<Block> getBlocks(@RequestParam(value = "index", required = false) String index) {
        List<Block> blocks = new ArrayList<>();

        if (index == null || index.equals("")) {
            blocks = blockService.getAllBlocks();
        } else {
            String[] indexs = index.split(",");
            for (String var0 : indexs) {
                blocks.add(blocks.get(Integer.valueOf(var0)));
            }
        }

        return blocks;
    }

    @PostMapping("block/contract/verify")
    void receiveContracts(@RequestParam("key") String key,
                          @RequestParam("index") Integer index,
                          @RequestBody List<Contract> contracts){
        contractCollectorRunner.addToSendBackContracts(index, key, contracts);
    }

}
