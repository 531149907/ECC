package com.ecc.service.peer;

import com.ecc.domain.peer.Peer;
import com.ecc.domain.security.KeyStorage;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.service.RestTemplate;
import com.ecc.service.block.BlockService;
import com.ecc.service.contract.ContractService;
import com.ecc.service.transaction.TransactionService;
import com.ecc.service.transfer.TransferService;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.AesUtil;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.util.system.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ecc.constants.ApplicationConstants.SERVER_PUBLIC_KEY;
import static com.ecc.constants.ApplicationConstants.SERVER_URL;

@Service
public class PeerService {
    @Autowired
    BlockService blockService;
    @Autowired
    ContractService contractService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    TransferService transferService;
    @Autowired
    RestTemplate restTemplate;

    public Peer register(String email, String channel, String level, String dir) throws Exception {
        RsaUtil.generateKeyPair(email);
        KeyStorage keyStorage = RsaUtil.loadKeyPair(email);

        Peer.getInstance().setEmail(email);
        Peer.getInstance().setChannel(channel);
        Peer.getInstance().setLevel(level);
        Peer.getInstance().setIp(NetworkUtil.getLocalAddress());
        Peer.getInstance().setPort(29626);
        Peer.getInstance().setDir(dir);
        Peer.getInstance().setPublicKey(RsaUtil.getKeyInString(keyStorage.getPublicKey()));
        Peer.getInstance().setRegDate(DateUtil.getDate());

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        if (restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class) != null) {
            throw new CustomException(ExceptionCollection.USER_EMAIL_ALREADY_REGISTERED);
        }

        Peer peer = restTemplate.post(SERVER_URL + "api/user-service/register", null, Peer.getInstance(), Peer.class);
        Peer.getInstance().setId(peer.getId());
        Peer.getInstance().setEmail(peer.getEmail());
        Peer.getInstance().setChannel(peer.getChannel());
        Peer.getInstance().setLevel(peer.getLevel());
        Peer.getInstance().setIp(NetworkUtil.getLocalAddress());
        Peer.getInstance().setPort(29626);
        Peer.getInstance().setDir(dir);
        Peer.getInstance().setPublicKey(peer.getPublicKey());
        Peer.getInstance().setRegDate(peer.getRegDate());

        return Peer.getInstance();
    }

    public Peer login(String email, String dir) throws Exception {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        if(restTemplate.get(SERVER_URL+"api/user-service/peer",params,Peer.class)==null){
            throw new CustomException(ExceptionCollection.USER_EMAIL_NOT_REGISTERED);
        }

        if (RsaUtil.loadKeyPair(email).getPrivateKey() != null) {
            params = restTemplate.get(SERVER_URL + "api/user-service/verify", params, HashMap.class);

            if(RsaUtil.loadKeyPair(email).getPrivateKey() == null){
                throw new CustomException(ExceptionCollection.KEY_PRIVATE_KEY_NOT_EXISTS);
            }

            String aesKey = RsaUtil.decrypt(params.get("encryptedAesKey"), RsaUtil.loadKeyPair(email).getPrivateKey());
            String aesDecryptValue = AesUtil.decrypt(aesKey, params.get("encryptedData"));
            String randomValue = aesDecryptValue.split("@")[0];
            String signedValue = aesDecryptValue.split("@")[1];

            if (RsaUtil.verify(SERVER_PUBLIC_KEY, randomValue, signedValue)) {
                String verifyValue = randomValue + "@" + RsaUtil.sign(randomValue, RsaUtil.loadKeyPair(email).getPrivateKey());
                verifyValue = AesUtil.encrypt(aesKey, verifyValue);

                //todo: SENT TO SERVER to receive feedback
                params = new HashMap<>();
                params.put("email", email);
                Peer newPeer = restTemplate.post(SERVER_URL + "api/user-service/verify", params, verifyValue, Peer.class);

                Peer.getInstance().setId(newPeer.getId());
                Peer.getInstance().setEmail(newPeer.getEmail());
                Peer.getInstance().setChannel(newPeer.getChannel());
                Peer.getInstance().setLevel(newPeer.getLevel());
                Peer.getInstance().setIp(NetworkUtil.getLocalAddress());
                Peer.getInstance().setPort(29626);
                Peer.getInstance().setDir(dir);
                Peer.getInstance().setPublicKey(newPeer.getPublicKey());
                Peer.getInstance().setRegDate(newPeer.getRegDate());
                Peer.getInstance().setSecretKey(aesKey);
                Peer.getInstance().setStatus("up");
                Peer.getInstance().setToken(newPeer.getToken());

                //todo: update peer current statues
                restTemplate.post(SERVER_URL + "api/user-service/login", null, Peer.getInstance(), null);
                return Peer.getInstance();
            }
            throw new CustomException(ExceptionCollection.USER_VERIFICATION_ERROR);
        }
        throw new CustomException(ExceptionCollection.KEY_PRIVATE_KEY_NOT_EXISTS);
    }

    public List<String> getPeerList(int maxPeers) {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", Peer.getInstance().getToken());
        List<Peer> instances = restTemplate.get(SERVER_URL + "user-service/peers", params, List.class);
        List<String> tempList = new ArrayList<>();
        List<String> peerList = new ArrayList<>();

        for (Peer peer : instances) {
            tempList.add(peer.getEmail());
        }

        if (maxPeers > tempList.size()) {
            peerList.addAll(tempList);
            for (int i = 0; i < maxPeers - tempList.size(); i++) {
                peerList.add(tempList.get(0));
            }
        } else {
            peerList.addAll(tempList);
        }

        return peerList;
    }
}
