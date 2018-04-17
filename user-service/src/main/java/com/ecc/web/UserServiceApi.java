package com.ecc.web;

import com.ecc.dao.PeerMapper;
import com.ecc.dao.VerificationMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.peer.Verification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserServiceApi {

    @Autowired
    PeerMapper peerMapper;
    @Autowired
    VerificationMapper verificationMapper;

    @PutMapping("peer")
    public void addPeer(@RequestBody Peer peer) {
        peerMapper.addPeer(peer);
    }

    @GetMapping("peer/email")
    public Peer getPeerByEmail(@RequestParam("email") String email) {
        return peerMapper.getPeerByEmail(email);
    }

    @GetMapping("peer/ip")
    public Peer getPeerByIp(@RequestParam("ip") String ip) {
        return peerMapper.getPeerByIp(ip);
    }

    @GetMapping("peer")
    public List<Peer> getUpPeers() {
        return peerMapper.getUpPeers();
    }

    @GetMapping("token")
    public boolean checkIfTokenExists(@RequestParam("token") String token) {
        return peerMapper.getTokenExists(token) == 1;
    }

    @PostMapping("token")
    public void updateToken(@RequestParam("email") String email,
                            @RequestParam("token") String token) {
        peerMapper.updateToken(email, token);
    }

    @PostMapping("peer")
    public void updatePeer(@RequestBody Peer peer) {
        peerMapper.updatePeer(peer);
    }

    @PutMapping("verify")
    public void addVerification(@RequestBody Verification verification) {
        verificationMapper.addVerification(verification);
    }

    @GetMapping("verify")
    public Verification getVerification(@RequestParam("email") String email) {
        return verificationMapper.getVerification(email);
    }

    @DeleteMapping("verify")
    public void deleteVerification(@RequestParam("email") String email) {
        verificationMapper.deleteVerification(email);
    }

    @PostMapping("verify")
    public void updateVerification(@RequestBody Verification verification) {
        verificationMapper.updateVerification(verification);
    }
}
