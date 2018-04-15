package com.ecc.web;

import com.ecc.domain.peer.Peer;
import com.ecc.service.peer.PeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class PeerApi {
    @Autowired
    PeerService peerService;

    @PostMapping("register")
    public Peer register(@RequestParam("email") String email,
                         @RequestParam("channel") String channel,
                         @RequestParam("level") String level,
                         @RequestParam("dir") String dir) throws Exception {
        return peerService.register(email, channel, level, dir);
    }

    @PostMapping("login")
    public Peer login(@RequestParam("email") String email,
                      @RequestParam("dir") String dir) throws Exception {
        return peerService.login(email, dir);
    }
}
