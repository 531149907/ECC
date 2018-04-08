package com.ecc.web.api;

import com.ecc.domain.transaction.impl.FileTransaction;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient(value = "file-service")
public interface FileServiceApi {

    @RequestMapping(value = "transaction", method = RequestMethod.GET)
    FileTransaction getTransaction(@RequestParam("transactionId") String transactionId,
                                   @RequestParam("transactionType") String transactionType);


    @RequestMapping(value = "shardhash", method = RequestMethod.GET)
    String getShardHash(@RequestParam("shardFileName") String shardFileName);

    @RequestMapping(value = "transactionlist", method = RequestMethod.GET)
    List<FileTransaction> getFileTransactions(@RequestParam("hashedFileName") String hashedFileName);
}
