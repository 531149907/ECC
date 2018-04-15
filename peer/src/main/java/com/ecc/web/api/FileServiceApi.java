package com.ecc.web.api;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Component
@FeignClient(value = "file-service")
public interface FileServiceApi {

    @GetMapping("transaction")
    FileTransaction getTransaction(@RequestParam("transactionId") String transactionId,
                                   @RequestParam("transactionType") String transactionType);

    @GetMapping("shard_hash")
    String getShardHash(@RequestParam("hashedShardName") String hashedFileName);

    @GetMapping("transactions")
    List<FileTransaction> getFileTransactions(@RequestParam("fileId") String fileId);

    @GetMapping("transactions/{owner}")
    List<String> getFileIds(@PathVariable("owner") String owner);

    @PostMapping("ticket/revoke")
    void revokeTicket(@RequestParam("ticketId") String ticketId,
                      @RequestParam("timestamp") String timestamp,
                      @RequestParam("ip") String ip,
                      @RequestParam("port") Integer port);

    @PostMapping("ticket/create")
    void createTicket(@RequestBody TicketTransaction transaction);

    @GetMapping("ticket/revoke")
    String isTicketRevoked(@RequestParam("ticketId") String ticketId);
}
