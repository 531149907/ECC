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
        try {
            peerMapper.addPeer(peer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("peer/email")
    public Peer getPeerByEmail(@RequestParam("email") String email) {
        try {
            return peerMapper.getPeerByEmail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("peer/ip")
    public Peer getPeerByIp(@RequestParam("ip") String ip) {
        try {
            return peerMapper.getPeerByIp(ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("peer")
    public List<Peer> getUpPeers() {
        try {
            return peerMapper.getUpPeers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("token")
    public boolean checkIfTokenExists(@RequestParam("token") String token) {
        try {
            return peerMapper.getTokenExists(token) == 1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("token")
    public void updateToken(@RequestParam("email") String email,
                            @RequestParam("token") String token) {
        try {
            peerMapper.updateToken(email, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("peer")
    public void updatePeer(@RequestBody Peer peer) {
        try {
            peerMapper.updatePeer(peer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PutMapping("verify")
    public void addVerification(@RequestBody Verification verification) {
        try {
            verificationMapper.addVerification(verification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("verify")
    public Verification getVerification(@RequestParam("email") String email) {
        try {
            return verificationMapper.getVerification(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @DeleteMapping("verify")
    public void deleteVerification(@RequestParam("email") String email) {
        try {
            verificationMapper.deleteVerification(email);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("verify")
    public void updateVerification(@RequestBody Verification verification) {
        try {
            verificationMapper.updateVerification(verification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
