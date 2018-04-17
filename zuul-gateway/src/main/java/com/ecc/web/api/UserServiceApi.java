package com.ecc.web.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Component
@FeignClient("user-service")
public interface UserServiceApi {
    @RequestMapping(value = "token", method = GET)
    boolean checkIfTokenExists(@RequestParam("token") String token);
}
