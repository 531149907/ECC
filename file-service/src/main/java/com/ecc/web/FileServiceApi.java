package com.ecc.web;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.service.FileService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
public class FileServiceApi {

    @Autowired
    FileService fileService;

    @GetMapping("transaction")
    public FileTransaction getTransaction(@RequestParam("transactionId") String transactionId,
                                          @RequestParam("transactionType") String transactionType) {
        return fileService.getTransaction(transactionId, transactionType);
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, consumes = MULTIPART_FORM_DATA_VALUE)
    public void receiveFileAndTransaction(@RequestParam("transaction") String transaction,
                                          @RequestParam("fileName") String fileName,
                                          @RequestPart("file") MultipartFile file) throws Exception {
        FileTransaction transaction0 = new Gson().fromJson(transaction, FileTransaction.class);
        fileService.receiveFileAndTransaction(transaction0, fileName, file);
    }

    @GetMapping("shard_hash")
    public String getShardHash(@RequestParam("hashedShardName") String hashedFileName) {
        return fileService.getShardHash(hashedFileName);
    }

    @GetMapping("transactions")
    public List<FileTransaction> getFileTransactions(@RequestParam("fileId") String fileId) {
        return fileService.getFileTransactionsByFileId(fileId);
    }

    @GetMapping("transactions/{owner}")
    public List<String> getFileIds(@PathVariable("owner") String owner) {
        return fileService.getFileIdsByOwner(owner);
    }

    @PostMapping("ticket/revoke")
    public void revokeTicket(@RequestParam("ticketId") String ticketId,
                             @RequestParam("timestamp") String timestamp,
                             @RequestParam("ip") String ip,
                             @RequestParam("port") Integer port) {
        fileService.addTicketRevoke(ticketId, timestamp, ip, port);
    }

    @PostMapping("ticket/create")
    public void createTicket(@RequestBody TicketTransaction transaction) {
        fileService.addTicket(transaction);
    }

    @GetMapping("ticket/revoke")
    public String isTicketRevoked(@RequestParam("ticketId") String ticketId) {
        if (fileService.getTicketRevokeInfo(ticketId)) {
            return "false";
        }
        return "true";
    }
}
