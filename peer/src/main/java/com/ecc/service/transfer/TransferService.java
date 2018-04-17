package com.ecc.service.transfer;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.service.RestTemplate;
import com.ecc.service.block.BlockService;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.ContractService;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transaction.TransactionService;
import com.ecc.service.transfer.impl.TransferHandlerImpl;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.HashUtil;
import com.ecc.util.crypto.RsaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.List;
import java.util.UUID;

import static com.ecc.constants.ApplicationConstants.PATH_STORE;

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
    TaskExecutor taskExecutor;

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

        List<Path> shardPaths = transferHandler.splitFile(filePath);
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
            Contract contract = Contract.builder()
                    .id(contractId)
                    .channel("default_queue")
                    .transactionType(TransactionType.FILE)
                    .transactionId(transactionId)
                    .transactionHash(transaction.hash())
                    .timestamp(DateUtil.getDate())
                    .build();
            contractHandler.sign(Contract.SENDER_SIGN, contract, peerPrivateKey);
            sendFileAndTransaction(shardPaths.get(i), transaction);
            sendContract(contract);
        }

        transferHandler.deleteTempShards();
    }

    public void download(String ticketCode) throws Exception {
        /*TicketTransaction transaction;
        try {
            transaction = (TicketTransaction) BytesUtil.toObject(Base64Util.decode(ticketCode));
        } catch (Exception e) {
            throw new Exception("Ticket code not correct, unrecognized!");
        }

        if (!transaction.getSignFor().equals(Peer.getInstance().getEmail())) {
            throw new Exception("Ticket usage denied! Not signed for you to view the file!");
        }

        HashMap<String,Object> params = new HashMap<>();
        params.put("ticketId",transaction.getId());
        params.put("token",Peer.getInstance().getToken());
        if (restTemplate.get(SERVER_URL+"ticket/revoke",params,String.class).equals("true")) {
            throw new Exception("Ticket was revoked!");
        }

        switch (transaction.getPermissions()) {
            case PermissionType.DOWNLOAD:
                downloadFile(transaction);
                break;
            case PermissionType.SIGN:
                break;
            case PermissionType.VIEW:
                break;
        }*/
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

    public byte[] pushFile(String fileName) throws Exception {
        /*Path filePath = Paths.get(PATH_STORE + fileName);
        if (Files.exists(filePath)) {
            HashMap<String,Object> params = new HashMap<>();
            params.put("hashedShardName",fileName);
            params.put("token",Peer.getInstance().getToken());
            String shardFileHash = restTemplate.get(SERVER_URL+"shard_hash",params,String.class);
            if (!HashUtil.hash(filePath).equals(shardFileHash)) {
                throw new Exception("Shard hash not match! File maybe damaged!");
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
        throw new Exception("File not exists!");*/
        return null;
    }

    private void sendFileAndTransaction(Path filePath, FileTransaction transaction) {
        /*try {
            InputStream inputStream = new FileInputStream(filePath.toFile());
            MultipartFile file = new MockMultipartFile("file", inputStream);
            fileServiceUploadApi.receiveFileAndTransaction(new Gson().toJson(transaction),
                    transaction.getHashedShardName(), file);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    private void sendContract(Contract contract) {
        //taskExecutor.execute(() -> contractServiceApi.receiveSenderSignedContract(contract));
    }

    private void downloadFile(TicketTransaction ticketTransaction) throws Exception {
        /*List<FileTransaction> fileTransactions = fileServiceApi.getFileTransactions(ticketTransaction.getFileId());
        for (FileTransaction transaction : fileTransactions) {
            Peer holderPeer = userServiceApi.getPeer(transaction.getHolder(), "");
            String holderIp = holderPeer.getIp();
            int holderPort = holderPeer.getPort();
            HashMap<String, Object> params = new HashMap<>();
            params.put("fileName", transaction.getOriginalShardName());
            restTemplate.download(holderIp + ":" + holderPort + "/file/push", params, PATH_DOWNLOAD);
        }

        //todo: send revoke ticket
        fileServiceApi.revokeTicket(ticketTransaction.getId(), DateUtil.getDate(), Peer.getInstance().getIp(), Peer.getInstance().getPort());
        TransferHandler handler = TransferHandlerImpl.getHandler();
        List<Path> downloadedFilePath = handler.combineFiles(PATH_DOWNLOAD + fileTransactions.get(0).getOriginalFileName());
        if (downloadedFilePath.isEmpty()) {
            throw new Exception("Failed to recover file from shards!");
        }*/
    }
}
