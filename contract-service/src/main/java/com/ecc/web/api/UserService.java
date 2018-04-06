package com.ecc.web.api;

import com.ecc.domain.peer.Peer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-service")
public interface UserService {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    Peer getPeer(@RequestParam(value = "email", required = false) String email,
                 @RequestParam(value = "ip", required = false) String ip);
}
