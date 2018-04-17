package com.ecc.web;

import com.ecc.domain.peer.Peer;
import com.ecc.service.UserService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("user-service")
public class UserServiceController {
    @Autowired
    UserService userService;

    @GetMapping("peer")
    public Peer getPeer(@RequestParam(value = "email",required = false) String email,
                        @RequestParam(value = "ip",required = false) String ip){
        return userService.getPeer(email, ip);
    }

    @GetMapping("peers")
    public List<Peer> getPeers(){
        return userService.getUpPeers();
    }

    @PostMapping("register")
    public Peer register(@RequestBody Peer peer){
        return userService.register(peer);
    }

    @PostMapping("login")
    public void login(@RequestBody Peer peer){
        userService.login(peer);
    }

    @PostMapping("logout")
    public void logout(@RequestParam("email") String email){
        userService.logout(email);
    }

    @GetMapping("verify")
    public HashMap<String,String> getVerifyValue(@RequestParam("email") String email){
        return userService.getRandomValue(email);
    }

    @PostMapping("verify")
    public Peer returnVerifyValue(@RequestParam("email") String email,
                                  @RequestBody String verifyValue){
        return userService.returnVerifiedPeer(new Gson().fromJson(verifyValue,String.class),email);
    }
}
