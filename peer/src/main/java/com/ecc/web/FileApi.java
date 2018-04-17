package com.ecc.web;

import com.ecc.service.transfer.TransferService;
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
                       @RequestParam("password") String password) throws Exception {
        transferService.encryptFile(file, password);
        String filePath = PATH_TEMP + Paths.get(file).getFileName();
        transferService.uploadFile(filePath);
    }

    @RequestMapping(value = "store", method = RequestMethod.POST, consumes = MULTIPART_FORM_DATA_VALUE)
    public void store(@RequestParam("fileName") String fileName,
                      @RequestPart("file") MultipartFile file) throws Exception {
        transferService.storeFile(fileName, file);
    }

    @PostMapping("push")
    public byte[] push(@RequestParam("fileName") String fileName) throws Exception {
        return transferService.pushFile(fileName);
    }

    @PostMapping("download")
    public void download(@RequestBody String ticketCode) throws Exception {
        transferService.download(ticketCode);
    }
}
