package com.ecc.web.api;

import com.ecc.domain.contract.Contract;
import com.ecc.web.exceptions.ContractException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient("contract-service")
public interface ContractApi {

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    void receiveContract(@RequestBody Contract contract);

    @RequestMapping(value = "verify", method = RequestMethod.POST)
    void verifySignedContract(@RequestBody Contract contract) throws ContractException;
}
