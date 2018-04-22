package com.ecc.web;

import com.ecc.domain.block.Block;
import com.ecc.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlockServiceApi {

    @Autowired
    BlockService blockService;

    @GetMapping("block")
    Block getBlock(@RequestParam("index") Integer index) {
        return blockService.getBlock(index);
    }

    @PutMapping("block")
    void addBlock(@RequestBody byte[] rawBlock) {
        blockService.addBlock(rawBlock);
    }

    @GetMapping("blocks")
    List<Block> getAllBlocks(){
        return blockService.getAllBlocks();
    }
}
