package com.ecc.web.api;

import feign.RequestLine;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

@Component
public interface PeerApi {

    @RequestLine("POST /contract/sign")
    void receiverSign(@RequestParam("contract") String contract) throws Exception;

    @RequestLine("POST /contract/verify")
    void verifyContract(@RequestParam("contract") String contract) throws Exception;
}
