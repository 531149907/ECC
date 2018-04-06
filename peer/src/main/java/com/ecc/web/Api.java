package com.ecc.web;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.ContractService;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transfer.TransferService;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.api.ContractApi;
import com.ecc.web.api.FileApi;
import com.ecc.web.api.UserApi;
import com.ecc.web.exceptions.ContractException;
import com.ecc.web.exceptions.CryptoExcetion;
import com.ecc.web.exceptions.FileException;
import com.ecc.web.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;
import java.security.PrivateKey;

import static com.ecc.constants.PeerConstants.PATH_TEMP;

@RestController
public class Api {
    @Autowired
    PeerService peerService;
    @Autowired
    TransferService transferService;
    @Autowired
    ContractService contractService;
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FileApi fileApi;
    @Autowired
    ContractApi contractApi;
    @Autowired
    UserApi userApi;

    @PostMapping("/register")
    public Peer regTest(@RequestParam("email") String email,
                        @RequestParam("channel") String channel,
                        @RequestParam("level") String level,
                        @RequestParam("dir") String dir) throws UserException {
        return peerService.register(email, channel, level, dir);
    }

    @PostMapping("/login")
    public Peer loginTest(@RequestParam("email") String email,
                          @RequestParam("dir") String dir) throws UserException {
        return peerService.login(email, dir);
    }

    @PostMapping("/upload")
    public void uploadTest(@RequestParam("file") String file,
                           @RequestParam("password") String password) throws Exception {
        try {
            transferService.encryptFile(file, password);
        } catch (Exception e) {
            throw new CryptoExcetion("File encrypt failed!");
        }
        String filePath = PATH_TEMP + Paths.get(file).getFileName();
        transferService.uploadFile(filePath);
    }

    @PostMapping("/store")
    public void storeTest(@RequestPart("multipart") MultipartFile multipart) throws FileException {
        transferService.storeFile(multipart);
    }

    @PostMapping("/contract")
    public void processContractTest(@RequestBody Contract contract) throws ContractException {
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();
        FileTransaction transaction = fileApi.getTransaction(contract.getTransactionId(), contract.getTransactionType());

        if (!transaction.hash().equals(contract.getTransactionHash())) {
            throw new ContractException("Transaction.hash != contract.transaction.hash");
        }

        //获取signer的公钥
        String publicKey = userApi.getPeer(contract.getSenderSign(), "").getPublicKey();

        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getPeer().getEmail()).getPrivateKey();

        if (contractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract,
                RsaUtil.getPublicKeyFromString(publicKey))) {
            contractHandler.sign(Contract.RECEIVER_SIGN, contract, peerPrivateKey);

            contractApi.verifySignedContract(contract);
            return;
        }

        throw new ContractException("Contract verify error!");
    }
}
