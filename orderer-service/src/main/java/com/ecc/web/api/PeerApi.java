package com.ecc.web.api;

import com.ecc.domain.contract.Contract;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
public interface PeerApi {

    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    void sendAndVerify(@RequestBody Contract contract);

}
