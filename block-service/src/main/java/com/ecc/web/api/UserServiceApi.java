package com.ecc.web.api;

import com.ecc.domain.peer.Peer;
import com.ecc.web.exceptions.UserException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

@Component
@FeignClient("USER-SERVICE")
public interface UserServiceApi {
    @HystrixCommand(ignoreExceptions = {Exception.class})
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    Peer register(@RequestBody Peer peer) throws UserException;

    @HystrixCommand(ignoreExceptions = {Exception.class})
    @RequestMapping(value = "/verify", method = RequestMethod.GET)
    HashMap<String, String> getRandomValue(@RequestParam("email") String email) throws UserException;

    @HystrixCommand(ignoreExceptions = {Exception.class})
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    Peer returnVerifiedPeer(@RequestParam("verifyValue") String verifyValue,
                            @RequestParam("email") String email) throws UserException;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    void login(@RequestBody Peer peer);

    @RequestMapping(value = "/", method = RequestMethod.GET)
    Peer getPeer(@RequestParam(value = "email", required = false) String email,
                 @RequestParam(value = "ip", required = false) String ip);
}
