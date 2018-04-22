package com.ecc.web;

import com.ecc.api.BlockServiceApi;
import com.ecc.domain.block.Block;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("block-service")
public class BlockServiceController {

    @Autowired
    BlockServiceApi blockService;

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
}
