package com.ecc.web;

import com.ecc.dao.transaction.TransactionMapper;
import com.ecc.dao.transfer.TransferMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.web.api.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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
    @Autowired
    TaskExecutor taskExecutor;

    @GetMapping("transaction")
    public FileTransaction getTransaction(@RequestParam("transactionId") String transactionId,
                                          @RequestParam("transactionType") String transactionType) {
        return transactionMapper.getTransactionById(transactionId);
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, consumes = MULTIPART_FORM_DATA_VALUE)
    public void receiveFileAndTransaction(@RequestParam("transaction") String transaction0,
                                          @RequestPart("file") MultipartFile file) throws Exception {

        Files.createFile(Paths.get("/Users/zhouzhixuan/Desktop/100.PNG"));
        File file1 = new File("/Users/zhouzhixuan/Desktop/100.PNG");
        file.transferTo(file1);

        FileTransaction transaction = new Gson().fromJson(transaction0, FileTransaction.class);
        transferMapper.addFileTransaction(transaction);

        String holderEmail = transaction.getHolder();
        Peer peer = userService.getPeer(holderEmail, "");
        String holderIP = peer.getIp();
        Integer holderPort = peer.getPort();
/*
        Path tempFilePath = Paths.get("./file-service/src/main/temp/" + file1.getName());
        Path tempFileDir = Paths.get("./file-service/src/main/temp/");

        if (!Files.exists(tempFileDir)) {
            Files.createDirectories(tempFileDir);
            Files.deleteIfExists(tempFilePath);
            Files.createFile(tempFilePath);
        }*/

        //File tempFile = tempFilePath.toFile();
        //file.transferTo(tempFile);

        taskExecutor.execute(() -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put("file", new FileSystemResource(file1));
            restTemplate.post(holderIP + ":" + holderPort + "/store", params, null);
            System.out.println("sent to " + holderIP + ":" + holderPort);
        });

        System.out.println(">>> 6");
    }
}
