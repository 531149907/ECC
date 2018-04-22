package com.ecc.web;

import com.ecc.service.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("ticket")
public class TicketApi {
    @Autowired
    TransactionService transactionService;

    @GetMapping("sign")
    public String sign(@RequestParam("fileId") String fileId,
                     @RequestParam("signFor") String signFor,
                     @RequestParam("permission") String permission) {
        return transactionService.signTicket(fileId, signFor, permission);
    }
}
