package com.ecc.web;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.FileService;
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
        fileService.receiveFileAndTransaction(transaction, fileName, file);
    }

    @GetMapping("shardhash")
    public String getShardHash(@RequestParam("shardFileName") String shardFileName) {
        return fileService.getShardHashByShardFileName(shardFileName);
    }

    @GetMapping("transactionlist")
    public List<FileTransaction> getFileTransactions(@RequestParam("hashedFileName") String hashedFileName){
        return fileService.getFileTransactions(hashedFileName);
    }
}
