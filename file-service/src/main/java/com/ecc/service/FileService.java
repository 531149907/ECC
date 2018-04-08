package com.ecc.service;

import com.ecc.dao.FileMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.web.api.UserServiceApi;
import com.google.gson.Gson;
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
        return fileMapper.getTransactionById(transactionId);
    }

    public void receiveFileAndTransaction(String transaction0, String fileName, MultipartFile file) throws Exception {
        Path tempFilePath = Paths.get("./file-service/src/main/temp/" + fileName);
        file.transferTo(tempFilePath.toFile());

        FileTransaction transaction = new Gson().fromJson(transaction0, FileTransaction.class);
        fileMapper.addFileTransaction(transaction);

        String holderEmail = transaction.getHolder();
        Peer peer = userServiceApi.getPeer(holderEmail, "");
        String holderIP = peer.getIp();
        Integer holderPort = peer.getPort();

        taskExecutor.execute(() -> new Thread(() -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put("fileName", fileName);
            params.put("file", new FileSystemResource(tempFilePath.toFile()));
            restTemplate.post(holderIP + ":" + holderPort + "/store", params, null);
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start());
    }

    public String getShardHashByShardFileName(String shardName) {
        return fileMapper.getShardHashByShardFileName(shardName);
    }

    public List<FileTransaction> getFileTransactions(String hashedFileName) {
        return fileMapper.getFileTransactions(hashedFileName);
    }
}
