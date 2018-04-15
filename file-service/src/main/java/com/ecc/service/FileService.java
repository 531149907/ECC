package com.ecc.service;

import com.ecc.dao.FileMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.web.api.UserServiceApi;
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
    FileMapper fileMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    UserServiceApi userServiceApi;
    @Autowired
    TaskExecutor taskExecutor;

    public FileTransaction getTransaction(String transactionId, String transactionType) {
        return fileMapper.getFileTransactionById(transactionId);
    }

    public void receiveFileAndTransaction(FileTransaction transaction, String fileName, MultipartFile file) throws Exception {
        Path tempFilePath = Paths.get("./file-service/src/main/temp/" + fileName);
        file.transferTo(tempFilePath.toFile());

        fileMapper.addFileTransaction(transaction);

        String holderEmail = transaction.getHolder();
        Peer peer = userServiceApi.getPeer(holderEmail, "");
        String holderIP = peer.getIp();
        Integer holderPort = peer.getPort();

        taskExecutor.execute(() -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put("fileName", fileName);
            params.put("file", new FileSystemResource(tempFilePath.toFile()));
            restTemplate.post(holderIP + ":" + holderPort + "/file/store", params, null);
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void addTicket(TicketTransaction transaction) {
        fileMapper.addTicketTransaction(transaction);
    }

    public void addTicketRevoke(String ticketId, String timestamp, String ip, Integer port) {
        fileMapper.addTicketRevoke(ticketId, timestamp, ip, port);
    }

    public List<FileTransaction> getFileTransactionsByFileId(String fileId) {
        return fileMapper.getFileTransactionsByFileId(fileId);
    }

    public List<String> getFileIdsByOwner(String owner) {
        return fileMapper.getOwnersFileIds(owner);
    }

    public String getShardHash(String hashedShardName) {
        return fileMapper.getShardHashByHashedShardName(hashedShardName);
    }

    public boolean getTicketRevokeInfo(String ticketId) {
        return fileMapper.getTicketRevokeInfo(ticketId) == null;
    }
}
