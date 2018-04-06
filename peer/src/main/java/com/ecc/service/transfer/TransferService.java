package com.ecc.service.transfer;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.service.block.BlockService;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.ContractService;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transaction.TransactionService;
import com.ecc.service.transfer.impl.TransferHandlerImpl;
import com.ecc.util.converter.Base64Util;
import com.ecc.util.converter.BytesUtil;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.HashUtil;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.api.ContractApi;
import com.ecc.web.api.FileApi;
import com.ecc.web.api.FileUploadApi;
import com.ecc.web.api.UserApi;
import com.ecc.web.exceptions.FileException;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.ecc.constants.PeerConstants.PATH_STORE;

@Service
public class TransferService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BlockService blockService;
    @Autowired
    ContractService contractService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PeerService peerService;

    @Autowired
    UserApi userApi;
    @Autowired
    FileApi fileApi;
    @Autowired
    FileUploadApi fileUploadApi;
    @Autowired
    ContractApi contractApi;

    public void encryptFile(String path, String password) throws Exception {
        TransferHandler transferHandler = TransferHandlerImpl.getHandler();
        transferHandler.encryptFile(path, password);
    }

    public void decryptFile(String path, String password) throws Exception {
        TransferHandler transferHandler = TransferHandlerImpl.getHandler();
        transferHandler.decryptFile(path, password);
    }

    public void uploadFile(String filePath) throws Exception {
        TransferHandler transferHandler = TransferHandlerImpl.getHandler();
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();

        File uploadFile = Paths.get(filePath).toFile();

        //List<Path> shardPaths = transferHandler.splitFile(filePath);
        List<Path> shardPaths = new ArrayList<>();
        shardPaths.add(Paths.get(filePath));
        List<String> peerList = peerService.getPeerList(shardPaths.size());

        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getPeer().getEmail()).getPrivateKey();

        for (int i = 0; i < shardPaths.size(); i++) {
            String transactionId = UUID.randomUUID().toString();
            String contractId = UUID.randomUUID().toString();
            FileTransaction transaction = FileTransaction.builder()
                    .id(transactionId)
                    .originalFileName(uploadFile.getName())
                    .hashedFileName(HashUtil.hash(uploadFile.getName()))
                    .fileHash(HashUtil.hash(Paths.get(filePath)))
                    .shardId(String.valueOf(i))
                    .shardHash(HashUtil.hash(shardPaths.get(i)))
                    .owner(Peer.getPeer().getEmail())
                    .holder(peerList.get(i))
                    .timestamp(DateUtil.getDate())
                    .fileLevel("LEVEL_A")
                    .build();
            Contract contract = Contract.builder()
                    .id(contractId)
                    .channel("default_queue")
                    .transactionType("upload")
                    .transactionId(transactionId)
                    .transactionHash(transaction.hash())
                    .timestamp(DateUtil.getDate())
                    .build();
            contractHandler.sign(Contract.SENDER_SIGN, contract, peerPrivateKey);
            sendFileAndTransaction(shardPaths.get(i), transaction);
            sendContract(contract);
            Thread.sleep(1000);
        }

        transferHandler.deleteTempShards();
    }

    /*public Report downloadFile(String ticket) {
        byte[] ticketBytes = Base64Util.decode(ticket);
        TicketTransaction ticketTransaction = (TicketTransaction) BytesUtil.toObject(ticketBytes);
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();

        //todo: send ticket to file service and get List<String>(peer list)
        HashMap<String, Object> params = new HashMap<>();
        params.put("ticket", ticket);
        Report report = restTemplate.get("", params, Report.class);

        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getPeer().getEmail()).getPrivateKey();

        if (report.getCode() == 200) {
            Contract contract = Contract.builder()
                    .id(UUID.randomUUID().toString())
                    .channel("default_channel")
                    .transactionType(TransactionType.TICKET_REVOKE)
                    .transactionId(ticketTransaction.getId())
                    .timestamp(DateUtil.getDate())
                    .build();
            contractHandler.sign(Contract.SENDER_SIGN, contract, peerPrivateKey);
            sendContract(contract);

            //key:peer's ip and port    value:shardName
            HashMap<String, String> peerList = (HashMap<String, String>) report.getEntity();
            for (String peer : peerList.keySet()) {
                HashMap<String, Object> params1 = new HashMap<>();
                params1.put("file_name", peerList.get(peer));
                restTemplate.download(peer + "/file/push", params1, null);
            }
        }

        return report;
    }*/

    public void storeFile(MultipartFile multipartFile) throws FileException {
        try {
            Path filePath = Paths.get(PATH_STORE + multipartFile.getOriginalFilename());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            File downloadedFile = new File(filePath.toString());
            multipartFile.transferTo(downloadedFile);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new FileException("Store file failed!");
    }

    public byte[] pushFile(String params) {
        byte[] mapBytes = Base64Util.decode(params);
        HashMap<String, Object> map = (HashMap<String, Object>) BytesUtil.toObject(mapBytes);
        String fileName = (String) map.get("file_name");
        Path filePath = Paths.get(PATH_STORE + fileName);
        if (Files.exists(filePath)) {
            try {
                File file = filePath.toFile();
                InputStream inputStream = new FileInputStream(file);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024 * 4];
                int n;
                while ((n = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }
                return out.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void sendFileAndTransaction(Path filePath, FileTransaction transaction) {
        //todo: 传输 transaction 和 file 给 file service
        try {
            InputStream inputStream = new FileInputStream(filePath.toFile());
            MultipartFile file = new MockMultipartFile("file", inputStream);
            fileUploadApi.receiveFileAndTransaction(new Gson().toJson(transaction), file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendContract(Contract contract) {
        contractApi.receiveContract(contract);
    }
}
