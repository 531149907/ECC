package com.ecc.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ticket")
public class TicketApi {

    @PostMapping("sign")
    public void sign(@RequestParam("fileId") String fileId,
                     @RequestParam("signFor") String signFor,
                     @RequestParam("permissions") String permissions){

    }
}
