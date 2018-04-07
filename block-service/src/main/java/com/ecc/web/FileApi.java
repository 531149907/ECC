package com.ecc.web;

import com.ecc.service.transfer.TransferService;
import com.ecc.web.exceptions.CryptoExcetion;
import com.ecc.web.exceptions.FileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

import static com.ecc.constants.PeerConstants.PATH_TEMP;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
public class FileApi {
    @Autowired
    TransferService transferService;

    @PostMapping("upload")
    public void upload(@RequestParam("file") String file,
                       @RequestParam("password") String password) throws Exception {
        try {
            transferService.encryptFile(file, password);
        } catch (Exception e) {
            throw new CryptoExcetion("File encrypt failed!");
        }
        String filePath = PATH_TEMP + Paths.get(file).getFileName();
        transferService.uploadFile(filePath);
    }

    @RequestMapping(value = "store", method = RequestMethod.POST, consumes = MULTIPART_FORM_DATA_VALUE)
    public void store(@RequestParam("fileName") String fileName,
                      @RequestPart("file") MultipartFile file) throws FileException {
        transferService.storeFile(fileName, file);
    }
}
