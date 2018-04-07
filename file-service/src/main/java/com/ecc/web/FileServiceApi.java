package com.ecc.web;

import com.ecc.dao.transaction.TransactionMapper;
import com.ecc.dao.transfer.TransferMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.RestTemplate;
import com.ecc.web.api.UserServiceApi;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    UserServiceApi userServiceApi;
    @Autowired
    TaskExecutor taskExecutor;

    @GetMapping("transaction")
    public FileTransaction getTransaction(@RequestParam("transactionId") String transactionId,
                                          @RequestParam("transactionType") String transactionType) {
        return transactionMapper.getTransactionById(transactionId);
    }

    @RequestMapping(value = "upload", method = RequestMethod.POST, consumes = MULTIPART_FORM_DATA_VALUE)
    public void receiveFileAndTransaction(@RequestParam("transaction") String transaction0,
                                          @RequestParam("fileName") String fileName,
                                          @RequestPart("file") MultipartFile file) throws Exception {
        Path tempFilePath = Paths.get("./file-service/src/main/temp/"+ fileName);
        file.transferTo(tempFilePath.toFile());

        FileTransaction transaction = new Gson().fromJson(transaction0, FileTransaction.class);
        transferMapper.addFileTransaction(transaction);

        String holderEmail = transaction.getHolder();
        Peer peer = userServiceApi.getPeer(holderEmail, "");
        String holderIP = peer.getIp();
        Integer holderPort = peer.getPort();

        taskExecutor.execute(() -> {
            HashMap<String, Object> params = new HashMap<>();
            params.put("fileName",fileName);
            params.put("file", new FileSystemResource(tempFilePath.toFile()));
            restTemplate.post(holderIP + ":" + holderPort + "/store", params, null);
            try {
                Files.deleteIfExists(tempFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
