package com.ecc.web.api;

import com.ecc.domain.contract.Contract;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient("block-service")
public interface BlockServiceApi {
    @RequestMapping(value = "upload", method = RequestMethod.POST)
    void receiveVerifiedContract(@RequestBody Contract contracts);
}
