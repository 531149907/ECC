package com.ecc.web.api;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.web.exceptions.FileException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Component
@FeignClient(value = "file-service")
public interface FileServiceApi {

    @RequestMapping(value = "transaction", method = RequestMethod.GET)
    FileTransaction getTransaction(@RequestParam("transactionId") String transactionId,
                                   @RequestParam("transactionType") String transactionType);


}
