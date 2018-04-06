package com.ecc.service.peer;

import com.ecc.domain.peer.Peer;
import com.ecc.domain.security.KeyStorage;
import com.ecc.service.block.BlockService;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.service.contract.ContractService;
import com.ecc.service.transaction.TransactionService;
import com.ecc.service.transfer.TransferService;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.AesUtil;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.util.system.NetworkUtil;
import com.ecc.web.api.UserApi;
import com.ecc.web.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ecc.constants.PeerConstants.SERVER_PUBLIC_KEY;

@Service
public class PeerService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BlockService blockService;
    @Autowired
    ContractService contractService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    TransferService transferService;
    @Autowired
    DiscoveryClient discoveryClient;

    @Autowired
    UserApi userApi;

    public Peer register(String email, String channel, String level, String dir) throws UserException {
        RsaUtil.generateKeyPair(email);
        KeyStorage keyStorage = RsaUtil.loadKeyPair(email);

        Peer.getPeer().setEmail(email);
        Peer.getPeer().setChannel(channel);
        Peer.getPeer().setLevel(level);
        Peer.getPeer().setIp(NetworkUtil.getLocalAddress());
        Peer.getPeer().setPort(29606);
        Peer.getPeer().setDir(dir);
        Peer.getPeer().setPublicKey(RsaUtil.getKeyInString(keyStorage.getPublicKey()));
        Peer.getPeer().setRegDate(DateUtil.getDate());

        Peer peer = userApi.register(Peer.getPeer());
        if (peer != null) {
            Peer.setPeer(peer);
            return peer;
        }
        throw new UserException("Email already been registered!");
    }

    public Peer login(String email, String dir) throws UserException {
        if (RsaUtil.loadKeyPair(email).getPrivateKey() != null) {

            //todo: get value from server
            HashMap<String, String> params = userApi.getRandomValue(email);
            String aesKey = RsaUtil.decrypt(params.get("encryptedAesKey"), RsaUtil.loadKeyPair(email).getPrivateKey());
            String aesDecryptValue = AesUtil.decrypt(aesKey, params.get("encryptedData"));
            String randomValue = aesDecryptValue.split("@")[0];
            String signedValue = aesDecryptValue.split("@")[1];

            if (RsaUtil.verify(SERVER_PUBLIC_KEY, randomValue, signedValue)) {
                String verifyValue = randomValue + "@" + RsaUtil.sign(randomValue, RsaUtil.loadKeyPair(email).getPrivateKey());
                verifyValue = AesUtil.encrypt(aesKey, verifyValue);

                //todo: SENT TO SERVER to receive feedback
                Peer peer = userApi.returnVerifiedPeer(verifyValue, email);
                Peer.getPeer().setIp(NetworkUtil.getLocalAddress());
                Peer.getPeer().setPort(29626);
                Peer.getPeer().setDir(dir);
                Peer.setPeer(peer);

                //todo: update peer current statues
                userApi.login(Peer.getPeer());
                return peer;
            }
            throw new UserException("Random value verify failed!");
        }
        throw new UserException("Cannot locate privateKey file!");
    }

    public List<String> getPeerList(int maxPeers) {
        List<ServiceInstance> instances = discoveryClient.getInstances("peer".toUpperCase());
        List<String> tempList = new ArrayList<>();
        List<String> peerList = new ArrayList<>();

        for (ServiceInstance instance : instances) {
            String email = userApi.getPeer("", instance.getHost()).getEmail();
            tempList.add(email);
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
