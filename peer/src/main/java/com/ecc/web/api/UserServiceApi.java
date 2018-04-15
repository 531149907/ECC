package com.ecc.web.api;

import com.ecc.domain.peer.Peer;
import com.ecc.web.exceptions.UserException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Component
@FeignClient("USER-SERVICE")
public interface UserServiceApi {
    @RequestMapping(value = "register", method = RequestMethod.POST)
    Peer register(@RequestBody Peer peer) throws UserException;

    @RequestMapping(value = "verify", method = RequestMethod.GET)
    HashMap<String, String> getRandomValue(@RequestParam("email") String email) throws UserException;

    @RequestMapping(value = "verify", method = RequestMethod.POST)
    Peer returnVerifiedPeer(@RequestParam("verifyValue") String verifyValue,
                            @RequestParam("email") String email) throws UserException;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    void login(@RequestBody Peer peer);

    @RequestMapping(value = "peer", method = RequestMethod.GET)
    Peer getPeer(@RequestParam(value = "email") String email,
                 @RequestParam(value = "ip") String ip);
}
