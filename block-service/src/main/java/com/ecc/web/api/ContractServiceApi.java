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
public interface ContractServiceApi {

    @RequestMapping(value = "upload", method = RequestMethod.POST)
    void uploadSenderSignedContract(@RequestBody Contract contract);

    @RequestMapping(value = "verify", method = RequestMethod.POST)
    void uploadReceiverSignedContract(@RequestBody Contract contract) throws ContractException;
}
