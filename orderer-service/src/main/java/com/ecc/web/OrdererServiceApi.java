package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.service.OrdererService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrdererServiceApi {

    @Autowired
    OrdererService ordererService;

    @PostMapping("orderer/import")
    public void sendImportBlockBroadcast(@RequestParam("index") Integer index,
                                         @RequestParam("preHash") String preHash,
                                         @RequestBody List<Contract> contractList) {
        ordererService.sendImportBlockBroadcast(preHash, index, contractList);
    }
}
