package com.ecc.api;

import com.ecc.domain.peer.Peer;
import com.ecc.domain.peer.Verification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@Component
@FeignClient("user-service")
public interface UserServiceApi {
    @RequestMapping(value = "peer", method = PUT)
    void addPeer(@RequestBody Peer peer);

    @RequestMapping(value = "peer/email", method = GET)
    Peer getPeerByEmail(@RequestParam("email") String email);

    @RequestMapping(value = "peer/ip", method = GET)
    Peer getPeerByIp(@RequestParam("ip") String ip);

    @RequestMapping(value = "peer", method = GET)
    List<Peer> getUpPeers();

    @RequestMapping(value = "token", method = GET)
    boolean checkIfTokenExists(@RequestParam("token") String token);

    @RequestMapping(value = "token", method = POST)
    void updateToken(@RequestParam("email") String email,
                     @RequestParam("token") String token);

    @RequestMapping(value = "peer", method = POST)
    void updatePeer(@RequestBody Peer peer);

    @RequestMapping(value = "verify", method = PUT)
    void addVerification(@RequestBody Verification verification);

    @RequestMapping(value = "verify", method = GET)
    Verification getVerification(@RequestParam("email") String email);

    @RequestMapping(value = "verify", method = DELETE)
    void deleteVerification(@RequestParam("email") String email);

    @RequestMapping(value = "verify", method = POST)
    void updateVerification(@RequestBody Verification verification);
}
