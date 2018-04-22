package com.ecc.service.transfer;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.PermissionType;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.handler.ContractHandler;
import com.ecc.handler.TransferHandler;
import com.ecc.service.RestTemplate;
import com.ecc.service.contract.ContractService;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transaction.TransactionService;
import com.ecc.util.converter.Base64Util;
import com.ecc.util.converter.BytesUtil;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.HashUtil;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.util.system.NetworkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.ecc.constants.ApplicationConstants.*;

@Service
public class TransferService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ContractService contractService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    PeerService peerService;
    @Autowired
    TaskExecutor taskExecutor;

    public void encryptFile(String path, String password) throws Exception {
        TransferHandler.encryptFile(path, password);
    }

    public void decryptFile(String path, String password) throws Exception {
        TransferHandler.decryptFile(path, password);
    }

    public void uploadFile(String filePath) throws Exception {
        File uploadFile = Paths.get(filePath).toFile();

        List<Path> shardPaths = TransferHandler.splitFile(filePath);
        List<String> peerList = peerService.getPeerList(shardPaths.size());

        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getInstance().getEmail()).getPrivateKey();

        String var0 = UUID.randomUUID().toString();
        for (int i = 0; i < shardPaths.size(); i++) {
            String transactionId = UUID.randomUUID().toString();
            String contractId = UUID.randomUUID().toString();
            FileTransaction transaction = FileTransaction.builder()
                    .transactionId(transactionId)
                    .fileId(HashUtil.hash(Peer.getInstance().getEmail() + var0))
                    .originalFileName(uploadFile.getName())
                    .hashedFileName(HashUtil.hash(uploadFile.getName() + var0))
                    .fileHash(HashUtil.hash(Paths.get(filePath)))
                    .shardId(String.valueOf(i))
                    .originalShardName(shardPaths.get(i).getFileName().toString())
                    .hashedShardName(HashUtil.hash(shardPaths.get(i).getFileName().toString() + var0))
                    .shardHash(HashUtil.hash(shardPaths.get(i)))
                    .owner(Peer.getInstance().getEmail())
                    .holder(peerList.get(i))
                    .timestamp(DateUtil.getDate())
                    .fileLevel("LEVEL_A")
                    .build();
            transaction.setTransactionType(TransactionType.FILE);
            Contract contract = Contract.builder()
                    .id(contractId)
                    .channel("default_queue")
                    .transactionType(TransactionType.FILE)
                    .transactionId(transactionId)
                    .transactionHash(transaction.hash())
                    .timestamp(DateUtil.getDate())
                    .build();
            ContractHandler.sign(Contract.SENDER_SIGN, contract, peerPrivateKey);
            sendFileAndTransaction(shardPaths.get(i), transaction);
            sendContract(contract);
        }

    }

    public void download(String password, String ticketCode) {
        TicketTransaction transaction;
        try {
            transaction = (TicketTransaction) BytesUtil.toObject(Base64Util.decode(ticketCode));
        } catch (Exception e) {
            throw new CustomException(ExceptionCollection.FILE_TICKET_UNRECOGNIZED_ERROR);
        }

        if (!transaction.getSignFor().equals(Peer.getInstance().getEmail())) {
            throw new CustomException(ExceptionCollection.FILE_TICKET_USAGE_ERROR);
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("ticketId", transaction.getId());
        params.put("token", Peer.getInstance().getToken());
        if (restTemplate.get(SERVER_URL + "api/file-service/transaction/ticket/revoke", params, Boolean.class)) {
            throw new CustomException(ExceptionCollection.FILE_TICKET_REVOKED_ERROR);
        }

        switch (transaction.getPermission()) {
            case PermissionType.DOWNLOAD:
                downloadFile(password, transaction);
                break;
            case PermissionType.SIGN:
                break;
            case PermissionType.VIEW:
                break;
        }
    }

    public void storeFile(String fileName, MultipartFile multipartFile) throws Exception {
        try {
            Path filePath = Paths.get(PATH_STORE + fileName);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            multipartFile.transferTo(filePath.toFile());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new Exception("Store file failed!");
    }

    public byte[] pushFile(String fileName) {
        Path filePath = Paths.get(PATH_STORE + fileName);
        if (Files.exists(filePath)) {
            HashMap<String, String> params = new HashMap<>();
            params.put("hashedShardName", fileName);
            params.put("token", Peer.getInstance().getToken());
            String shardFileHash = restTemplate.get(SERVER_URL + "api/file-service/transaction/file/hash", params, String.class);
            if (!HashUtil.hash(filePath).equals(shardFileHash)) {
                throw new CustomException(ExceptionCollection.FILE_PUSH_ERROR);
            }
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
        throw new CustomException(ExceptionCollection.FILE_PUSH_ERROR);
    }

    private void sendFileAndTransaction(Path filePath, FileTransaction transaction) {
        taskExecutor.execute(() -> {
            try {
                restTemplate.post(SERVER_URL + "api/file-service/transaction/file", null, transaction, null);

                HashMap<String, String> params = new HashMap<>();
                params.put("email", transaction.getHolder());
                Peer peer = restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class);

                FileSystemResource resource = new FileSystemResource(filePath.toFile());

                params = new HashMap<>();
                params.put("fileName", transaction.getHashedShardName());
                params.put("destination", peer.getIp() + ":" + peer.getPort());

                restTemplate.postForUpload(SERVER_URL + "api/file-service/file/upload", params, resource, null);

                Files.deleteIfExists(filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendContract(Contract contract) {
        taskExecutor.execute(() -> restTemplate.post(SERVER_URL + "api/contract-service/verify/sender", null, contract, null));
    }

    private void downloadFile(String password, TicketTransaction ticketTransaction) {
        HashMap<String, String> params = new HashMap<>();
        params.put("fileId", ticketTransaction.getFileId());
        List<FileTransaction> fileTransactions = restTemplate.get(SERVER_URL + "api/file-service/transaction/files/fileId", params, new ParameterizedTypeReference<List<FileTransaction>>() {
        });

        for (FileTransaction transaction : fileTransactions) {
            params = new HashMap<>();
            params.put("email", transaction.getHolder());
            Peer holderPeer = restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class);
            String holderIp = holderPeer.getIp();
            int holderPort = holderPeer.getPort();
            params = new HashMap<>();
            params.put("fileName", transaction.getHashedShardName());
            params.put("originalShardName", transaction.getOriginalShardName());
            restTemplate.download(holderIp + ":" + holderPort + "/file/push", params, PATH_TEMP);
        }

        params = new HashMap<>();
        params.put("ticketId", ticketTransaction.getId());
        params.put("timestamp", DateUtil.getDate());
        params.put("ip", NetworkUtil.getLocalAddress());
        params.put("port", String.valueOf(29626));
        restTemplate.post(SERVER_URL + "api/file-service/transaction/ticket/revoke", params, null, null);

        List<Path> downloadedFilePath = TransferHandler.combineFiles(PATH_TEMP + fileTransactions.get(0).getOriginalFileName());
        if (downloadedFilePath.isEmpty()) {
            throw new CustomException(ExceptionCollection.FILE_DOWNLOAD_COMBINE_ERROR);
        }

        try {
            decryptFile(PATH_TEMP + fileTransactions.get(0).getOriginalFileName(), password);
            for (FileTransaction transaction : fileTransactions) {
                Files.deleteIfExists(Paths.get(PATH_TEMP + transaction.getOriginalShardName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("download success!");
    }
}
