package com.ecc.web;

import com.ecc.dao.FileTransactionMapper;
import com.ecc.dao.TicketTransactionMapper;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FileServiceApi {
    @Autowired
    TicketTransactionMapper ticketTransactionMapper;
    @Autowired
    FileTransactionMapper fileTransactionMapper;

    @PutMapping("transaction/ticket")
    public void addTicketTransaction(@RequestBody TicketTransaction transaction) {
        try {
            ticketTransactionMapper.addTicketTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PutMapping("transaction/ticket/revoke")
    public void addTicketRevoke(@RequestParam("ticketId") String ticketId,
                                @RequestParam("timestamp") String timestamp,
                                @RequestParam("ip") String ip,
                                @RequestParam("port") Integer port) {
        try {
            ticketTransactionMapper.addTicketRevoke(ticketId, timestamp, ip, port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @GetMapping("transaction/ticket/revoke")
    public boolean isTicketRevoked(@RequestParam("ticketId") String ticketId) {
        try {
            return ticketTransactionMapper.getTicketRevokeInfo(ticketId) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @PutMapping("transaction/file")
    public void addFileTransaction(@RequestBody FileTransaction transaction) {
        try {
            fileTransactionMapper.addFileTransaction(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("transaction/file/id")
    public FileTransaction getFileTransaction(@RequestParam("id") String id) {
        try {
            FileTransaction fileTransaction = fileTransactionMapper.getFileTransactionById(id);
            return fileTransaction;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("transaction/file/hash")
    public String getShardHashByHashedShardName(@RequestParam("hashedShareName") String hashedShardName) {
        try {
            return "\""+fileTransactionMapper.getShardHashByHashedShardName(hashedShardName)+"\"";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("transaction/files/fileId")
    public List<FileTransaction> getFileTransactionsByFileId(@RequestParam("fileId") String fileId) {
        try {
            return fileTransactionMapper.getFileTransactionsByFileId(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("transaction/files/owner")
    public List<String> getOwnerFileIds(@RequestParam("owner") String owner) {
        try {
            return fileTransactionMapper.getOwnersFileIds(owner);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
