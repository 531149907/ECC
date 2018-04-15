package com.ecc.web;

import com.ecc.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ticket")
public class TicketApi {
    @Autowired
    TransactionService transactionService;

    @PostMapping("sign")
    public void sign(@RequestParam("fileId") String fileId,
                     @RequestParam("signFor") String signFor,
                     @RequestParam("permissions") String permissions) throws Exception {
        transactionService.signTicket(fileId, signFor, permissions);
    }
}
