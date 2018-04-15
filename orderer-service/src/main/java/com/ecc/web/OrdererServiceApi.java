package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.service.OrdererService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrdererServiceApi {

    @Autowired
    OrdererService ordererService;

    @PostMapping("upload")
    void receiveFailReport(@RequestBody Contract contracts) {

    }

    @PostMapping("message")
    void sendMessage(){
        ordererService.send("hello!?>?<!");
    }
}
