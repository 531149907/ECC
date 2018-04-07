package com.ecc.service;

import com.ecc.dao.PeerMapper;
import com.ecc.domain.peer.Peer;
import com.ecc.util.crypto.AesUtil;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

import static com.ecc.constants.ApplicationConstants.SERVER_PRIVATE_KEY;
import static com.ecc.constants.ApplicationConstants.SERVER_PUBLIC_KEY;

@Service
public class UserService {
    @Autowired
    PeerMapper peerMapper;

    public Peer getPeer(String email, String ip) {
        if (email == null || email.equals("")) {
            return peerMapper.getPeerByIp(ip);
        } else if (ip == null || ip.equals("")) {
            return peerMapper.getPeerByEmail(email);
        }
        return null;
    }

    public Peer register(Peer peer) throws UserException {
        if (peerMapper.getPeerByEmail(peer.getEmail()) == null) {
            peerMapper.addPeer(peer);
            return peer;
        }
        throw new UserException("Email already been registered!", 500);
    }

    public void login(Peer peer) {
        peerMapper.updatePeer(peer);
    }

    public HashMap<String, String> getRandomValue(String email) throws UserException {
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

    public Peer returnVerifiedPeer(String verifyValue, String email) throws UserException {
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
