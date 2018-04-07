package com.ecc.web.api;

import com.ecc.web.exceptions.FileException;
import feign.Headers;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Component
@FeignClient(value = "file-service", configuration = FileServiceUploadApi.MultipartSupportConfig.class)
public interface FileServiceUploadApi {


    @RequestMapping(value = "upload", method = RequestMethod.POST,
            produces = APPLICATION_JSON_UTF8_VALUE,
            consumes = MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    void receiveFileAndTransaction(@RequestParam("transaction") String transaction,
                                   @RequestParam("fileName") String fileName,
                                   @RequestPart("file") MultipartFile file) throws FileException;

    class MultipartSupportConfig {
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }
}
