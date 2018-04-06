package com.ecc.web.api;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface PeerUploadApi {
    @RequestLine("POST /store")
    @Headers("Content-Type: multipart/form-data")
    void storeFile(@Param("file") File file);
}
