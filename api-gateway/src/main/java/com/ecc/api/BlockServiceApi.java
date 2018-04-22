package com.ecc.api;

import com.ecc.domain.block.Block;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient("block-service")
public interface BlockServiceApi {

    @RequestMapping(value = "block", method = RequestMethod.GET)
    Block getBlock(@RequestParam("index") Integer index);

    @RequestMapping(value = "block", method = RequestMethod.PUT)
    void addBlock(@RequestBody byte[] rawBlock);

    @RequestMapping(value = "blocks", method = RequestMethod.GET)
    List<Block> getAllBlocks();
}
