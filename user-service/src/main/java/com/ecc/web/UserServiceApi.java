package com.ecc.web;

import com.ecc.domain.peer.Peer;
import com.ecc.service.UserService;
import com.ecc.web.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserServiceApi {
    @Autowired
    UserService userService;

    @RequestMapping(value = "peer", method = RequestMethod.GET)
    public Peer getPeer(@RequestParam(value = "email") String email,
                 @RequestParam(value = "ip") String ip){
        return userService.getPeer(email, ip);
    }

    @PostMapping("register")
    public Peer register(@RequestBody Peer peer) throws UserException {
        return userService.register(peer);
    }

    @PostMapping("login")
    public void login(@RequestBody Peer peer) {
        userService.login(peer);
    }

    @GetMapping("verify")
    public HashMap<String, String> getRandomValue(@RequestParam("email") String email) throws UserException {
        return userService.getRandomValue(email);
    }

    @PostMapping("verify")
    public Peer returnVerifiedPeer(@RequestParam("verifyValue") String verifyValue,
                                   @RequestParam("email") String email) throws UserException {
        return userService.returnVerifiedPeer(verifyValue, email);
    }
}
