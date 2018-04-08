package com.ecc.web.api;

import com.ecc.domain.contract.Contract;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient("peer")
public interface PeerApi {

    @RequestMapping(value = "/verify",method = RequestMethod.POST)
    void sendAndVerify(@RequestBody Contract contract);

}
