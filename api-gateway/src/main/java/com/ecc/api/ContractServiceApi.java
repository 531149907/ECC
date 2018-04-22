package com.ecc.api;

import com.ecc.domain.contract.Contract;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Component
@FeignClient("contract-service")
public interface ContractServiceApi {

    @RequestMapping(value = "contract", method = PUT)
    void addContract(@RequestBody Contract contract);

    @RequestMapping(value = "contracts", method = GET)
    List<Contract> getTop10contracts();

    @RequestMapping(value = "contract", method = GET)
    int getContractCount();

    @RequestMapping(value = "contract", method = DELETE)
    void deleteContract(@RequestParam("id") String id);
}
