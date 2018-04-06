package com.ecc.web;

import com.ecc.dao.peer.PeerMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.util.crypto.AesUtil;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

import static com.ecc.constants.PeerConstants.SERVER_PRIVATE_KEY;
import static com.ecc.constants.PeerConstants.SERVER_PUBLIC_KEY;

@RestController
public class UserServiceApi {
    @Autowired
    PeerMapper peerMapper;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Peer getPeer(@RequestParam("email") String email,
                        @RequestParam("ip") String ip) {
        if (email == null || email.equals("")) {
            return peerMapper.getPeerByIp(ip);
        } else if (ip == null || ip.equals("")) {
            return peerMapper.getPeerByEmail(email);
        }
        return null;
    }

    @PostMapping("register")
    public Peer register(@RequestBody Peer peer) throws UserException {
        if (peerMapper.getPeerByEmail(peer.getEmail()) == null) {
            peerMapper.addPeer(peer);
            return peer;
        }
        throw new UserException("Email already been registered!", 500);
    }

    @PostMapping("login")
    public void login(@RequestBody Peer peer) {
        peerMapper.updatePeer(peer);
    }

    @GetMapping("verify")
    public HashMap<String, String> getRandomValue(@RequestParam("email") String email) throws UserException {
        if (peerMapper.getPeerByEmail(email) != null) {
            HashMap<String, String> params = new HashMap<>();

            String randomValue = UUID.randomUUID().toString();
            String aesKey = UUID.randomUUID().toString();
            String aesKeyPeerPublicKeyEncrypted = RsaUtil.encrypt(aesKey, peerMapper.getPeerByEmail(email).getPublicKey());
            String aesKeyServerPublicKeyEncrypted = RsaUtil.encrypt(aesKey, SERVER_PUBLIC_KEY);
            String signedValue = RsaUtil.sign(randomValue, SERVER_PRIVATE_KEY);
            String resultValue = randomValue + "@" + signedValue;
            String aesEncryptedValue = AesUtil.encrypt(aesKey, resultValue);
            peerMapper.addVerification(email, randomValue, aesKeyServerPublicKeyEncrypted);

            params.put("encryptedAesKey", aesKeyPeerPublicKeyEncrypted);
            params.put("encryptedData", aesEncryptedValue);

            return params;
        }
        throw new UserException("Email not registered!", 500);
    }

    @PostMapping("verify")
    public Peer returnVerifiedPeer(@RequestParam("verifyValue") String verifyValue,
                                   @RequestParam("email") String email) throws UserException {
        String aesKey = RsaUtil.decrypt(peerMapper.getVerificationPassword(email), SERVER_PRIVATE_KEY);
        String decryptedValue = AesUtil.decrypt(aesKey, verifyValue);
        String randomValueToVerify = decryptedValue.split("@")[0];
        String signedValueToVerify = decryptedValue.split("@")[1];

        if (randomValueToVerify.equals(peerMapper.getVerificationCode(email))
                && RsaUtil.verify(peerMapper.getPeerByEmail(email).getPublicKey(), randomValueToVerify, signedValueToVerify)) {
            peerMapper.deleteVerification(email);
            return peerMapper.getPeerByEmail(email);
        }

        peerMapper.deleteVerification(email);
        throw new UserException("Random value verify failed!", 500);
    }
}
