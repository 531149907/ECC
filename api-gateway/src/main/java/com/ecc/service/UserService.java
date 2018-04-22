package com.ecc.service;

import com.ecc.api.UserServiceApi;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.peer.Verification;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.util.crypto.AesUtil;
import com.ecc.util.crypto.RsaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.ecc.constants.ApplicationConstants.SERVER_PRIVATE_KEY;
import static com.ecc.constants.ApplicationConstants.SERVER_PUBLIC_KEY;

@Service
public class UserService {
    @Autowired
    UserServiceApi userService;

    public Peer getPeer(String email, String ip) {
        if (!email.equals("")) {
            return userService.getPeerByEmail(email);
        }
        if (!ip.equals("")) {
            return userService.getPeerByIp(ip);
        }
        throw new CustomException(ExceptionCollection.USER_NOT_EXISTS);
    }

    public List<Peer> getUpPeers() {
        List<Peer> temp = userService.getUpPeers();
        return temp;
    }

    public String getToken(String email) {
        return userService.getPeerByEmail(email).getToken();
    }

    public void logout(String email) {
        Peer peer = Peer.builder()
                .email(email)
                .token("")
                .status("down")
                .build();
        userService.updatePeer(peer);
    }

    public Peer register(Peer peer) throws CustomException {
        userService.addPeer(peer);
        return userService.getPeerByEmail(peer.getEmail());
    }

    public void login(Peer peer) {
        userService.updatePeer(peer);
    }

    public HashMap<String, String> getRandomValue(String email) {
        HashMap<String, String> params = new HashMap<>();

        String randomValue = UUID.randomUUID().toString().replace("-", "");
        String aesKey = UUID.randomUUID().toString().replace("-", "");
        String aesKeyPeerPublicKeyEncrypted = RsaUtil.encrypt(aesKey, userService.getPeerByEmail(email).getPublicKey());
        String aesKeyServerPublicKeyEncrypted = RsaUtil.encrypt(aesKey, SERVER_PUBLIC_KEY);
        String signedValue = RsaUtil.sign(randomValue, SERVER_PRIVATE_KEY);
        String resultValue = randomValue + "@" + signedValue;
        String aesEncryptedValue = AesUtil.encrypt(aesKey, resultValue);
        Verification verification = Verification.builder()
                .email(email)
                .code(randomValue)
                .password(aesKeyServerPublicKeyEncrypted)
                .build();
        if (userService.getVerification(email) != null) {
            userService.deleteVerification(email);
        }
        userService.addVerification(verification);

        params.put("encryptedAesKey", aesKeyPeerPublicKeyEncrypted);
        params.put("encryptedData", aesEncryptedValue);

        return params;
    }

    public Peer returnVerifiedPeer(String verifyValue, String email) {
        String aesKey = RsaUtil.decrypt(userService.getVerification(email).getPassword(), SERVER_PRIVATE_KEY);
        String decryptedValue = AesUtil.decrypt(aesKey, verifyValue);
        String randomValueToVerify = decryptedValue.split("@")[0];
        String signedValueToVerify = decryptedValue.split("@")[1];

        boolean b1 = randomValueToVerify.equals(userService.getVerification(email).getCode());
        boolean b2 = RsaUtil.verify(userService.getPeerByEmail(email).getPublicKey(), randomValueToVerify, signedValueToVerify);

        if (b1 && b2) {
            userService.updateToken(email, UUID.randomUUID().toString());
        } else {
            throw new CustomException(ExceptionCollection.USER_VERIFICATION_ERROR);
        }
        userService.deleteVerification(email);
        return userService.getPeerByEmail(email);
    }

    public boolean verifyAccess(String token) {
        return userService.checkIfTokenExists(token);
    }
}
