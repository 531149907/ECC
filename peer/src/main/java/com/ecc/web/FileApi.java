package com.ecc.web;

import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.service.transfer.TransferService;
import com.ecc.web.exceptions.CryptoExcetion;
import com.ecc.web.exceptions.FileException;
import com.ecc.web.exceptions.TicketException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

import static com.ecc.constants.ApplicationConstants.PATH_TEMP;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("file")
public class FileApi {
    @Autowired
    TransferService transferService;

    @PostMapping("upload")
    public void upload(@RequestParam("file") String file,
                       @RequestParam("password") String password) throws Exception,CryptoExcetion {
        transferService.encryptFile(file, password);
        String filePath = PATH_TEMP + Paths.get(file).getFileName();
        transferService.uploadFile(filePath);
    }

    @RequestMapping(value = "store", method = RequestMethod.POST, consumes = MULTIPART_FORM_DATA_VALUE)
    public void store(@RequestParam("fileName") String fileName,
                      @RequestPart("file") MultipartFile file) throws FileException {
        transferService.storeFile(fileName, file);
    }

    @PostMapping("push")
    public byte[] push(@RequestParam("fileName") String fileName) throws FileException {
        return transferService.pushFile(fileName);
    }

    @PostMapping("download")
    public void download(@RequestBody String ticketCode) throws TicketException{
        transferService.download(ticketCode);
    }
}
