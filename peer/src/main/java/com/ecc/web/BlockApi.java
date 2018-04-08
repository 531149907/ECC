package com.ecc.web;

import com.ecc.domain.block.Block;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("block")
public class BlockApi {

    @PostMapping("sync")
    public void blockSync(@RequestBody Block block){

    }
}
