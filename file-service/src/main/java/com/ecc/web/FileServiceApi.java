package com.ecc.web;

import com.ecc.dao.transaction.TransactionMapper;
import com.ecc.dao.transfer.TransferMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.web.api.UserService;
import com.ecc.web.exceptions.FileException;
import com.ecc.web.exceptions.UserException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@RestController
public class FileServiceApi {

    @Autowired
    TransferMapper transferMapper;
    @Autowired
    TransactionMapper transactionMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    UserService userService;

    @GetMapping("transaction")
    public FileTransaction getTransaction(@RequestParam("transactionId") String transactionId,
                                          @RequestParam("transactionType") String transactionType) {
        return transactionMapper.getTransactionById(transactionId);
    }

    @PostMapping("upload")
    public void receiveFileAndTransaction(@RequestParam("transaction") String transaction0,
                                          @RequestPart("multipart") MultipartFile multipart) throws FileException {
        try {
            FileTransaction transaction = new Gson().fromJson(transaction0,FileTransaction.class);
            transferMapper.addFileTransaction(transaction);

            String holderEmail = transaction.getHolder();
            Peer peer = userService.getPeer(holderEmail, "");
            if (peer == null) {
                throw new UserException("User not exist!", 500);
            }

            String holderIP = peer.getIp();
            Integer holderPort = peer.getPort();

            Path tempFilePath = Paths.get("./src/main/temp/" + multipart.getOriginalFilename());
            Path tempFileDir = Paths.get("./src/main/temp/");
            if (!Files.exists(tempFileDir)) {
                Files.createDirectories(tempFileDir);
                Files.deleteIfExists(tempFilePath);
                Files.createFile(tempFilePath);
            }
            File tempFile = tempFilePath.toFile();
            multipart.transferTo(tempFile);

            HashMap<String, Object> params = new HashMap<>();
            params.put("multipart", new FileSystemResource(tempFile));
            restTemplate.post(holderIP + ":" + holderPort + "/store", params, null);
        } catch (Exception e) {
            throw new FileException("Process file and transaction error!",500);
        }
    }
}
