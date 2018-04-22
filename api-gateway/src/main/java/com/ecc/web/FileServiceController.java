package com.ecc.web;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("file-service")
public class FileServiceController {
    @Autowired
    FileService fileService;

    @GetMapping("transaction/file")
    public FileTransaction getFileTransaction(@RequestParam("id") String id) {
        return fileService.getFileTransaction(id);
    }

    @PostMapping("transaction/file")
    public void receiveFileTransaction(@RequestBody FileTransaction transaction) {
        fileService.receiveFileTransaction(transaction);
    }

    @PostMapping("file/upload")
    public void uploadFile(@RequestParam("fileName") String fileName,
                           @RequestParam("destination") String destination,
                           @RequestPart MultipartFile file) {
        fileService.transferFile(file, fileName, destination);
    }

    @PostMapping("transaction/ticket")
    public void addTicketTransaction(@RequestBody TicketTransaction transaction) {
        fileService.addTicketTransaction(transaction);
    }

    @PostMapping("transaction/ticket/revoke")
    public void addTicketRevokeTransaction(@RequestParam("ticketId") String ticketId,
                                           @RequestParam("timestamp") String timestamp,
                                           @RequestParam("ip") String ip,
                                           @RequestParam(value = "port",required = false) Integer port) {
        fileService.addTicketRevokeTransaction(ticketId, timestamp, ip, port);
    }

    @GetMapping("transaction/files/fileId")
    public List<FileTransaction> getFileTransactionByFileId(@RequestParam("fileId") String fileId) {
        return fileService.getFileTransactionsByFileId(fileId);
    }

    @GetMapping("transaction/files/owner")
    public List<String> getFileIdsByOwner(@RequestParam("owner") String owner) {
        return fileService.getFileIdsByOwner(owner);
    }

    @GetMapping("transaction/file/hash")
    public String getShardHash(@RequestParam("hashedShardName") String hashedShardName) {
        return fileService.getShardHash(hashedShardName);
    }

    @GetMapping("transaction/ticket/revoke")
    public boolean isTicketRevoked(@RequestParam("ticketId") String ticketId) {
        return fileService.getTicketRevokeInfo(ticketId);
    }
}
