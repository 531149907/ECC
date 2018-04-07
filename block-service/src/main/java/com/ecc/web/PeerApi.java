package com.ecc.web;

import com.ecc.domain.peer.Peer;
import com.ecc.service.peer.PeerService;
import com.ecc.web.api.UserServiceApi;
import com.ecc.web.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PeerApi {
    @Autowired
    PeerService peerService;
    @Autowired
    UserServiceApi userServiceApi;

    @PostMapping("register")
    public Peer register(@RequestParam("email") String email,
                         @RequestParam("channel") String channel,
                         @RequestParam("level") String level,
                         @RequestParam("dir") String dir) throws UserException {
        return peerService.register(email, channel, level, dir);
    }

    @PostMapping("login")
    public Peer login(@RequestParam("email") String email,
                      @RequestParam("dir") String dir) throws UserException {
        return peerService.login(email, dir);
    }
}
