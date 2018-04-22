package com.ecc.api;

import com.ecc.domain.contract.Contract;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Component
@FeignClient("orderer-service")
public interface OrdererServiceApi {

    @RequestMapping(value = "orderer/import", method = RequestMethod.POST)
    public void sendImportBlockBroadcast(@RequestParam("index") Integer index,
                                         @RequestParam("preHash") String preHash,
                                         @RequestBody List<Contract> contractList);
}
