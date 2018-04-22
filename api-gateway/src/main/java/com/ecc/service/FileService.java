package com.ecc.service;

import com.ecc.api.FileServiceApi;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

@Service
public class FileService {
    @Autowired
    FileServiceApi fileService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    TaskExecutor taskExecutor;

    public FileTransaction getFileTransaction(String id) {
        return fileService.getFileTransaction(id);
    }

    public void receiveFileTransaction(FileTransaction fileTransaction) {
        fileService.addFileTransaction(fileTransaction);
    }

    public void transferFile(MultipartFile file, String fileName, String destination) {
        Path tempFilePath = Paths.get("/Users/zhouzhixuan/Desktop/temp" + fileName);
        try {
            file.transferTo(tempFilePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        taskExecutor.execute(() -> {
            HashMap<String, String> params = new HashMap<>();
            params.put("fileName", fileName);
            FileSystemResource resource = new FileSystemResource(tempFilePath.toFile());
            restTemplate.postForUpload(destination + "/file/store", params, resource, null);
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addTicketTransaction(TicketTransaction transaction) {
        fileService.addTicketTransaction(transaction);
    }

    public void addTicketRevokeTransaction(String ticketId, String timestamp, String ip, Integer port) {
        fileService.addTicketRevoke(ticketId, timestamp, ip, port);
    }

    public List<FileTransaction> getFileTransactionsByFileId(String fileId) {
        return fileService.getFileTransactionsByFileId(fileId);
    }

    public List<String> getFileIdsByOwner(String owner) {
        return fileService.getOwnerFileIds(owner);
    }

    public String getShardHash(String hashedShardName) {
        return fileService.getShardHashByHashedShardName(hashedShardName);
    }

    public boolean getTicketRevokeInfo(String ticketId) {
        return fileService.isTicketRevoked(ticketId);
    }
}
