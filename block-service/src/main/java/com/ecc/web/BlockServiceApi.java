package com.ecc.web;

import com.ecc.domain.contract.Contract;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockServiceApi {

    @PostMapping("upload")
    void receiveVerifiedContract(@RequestBody Contract contracts) {

    }
}
