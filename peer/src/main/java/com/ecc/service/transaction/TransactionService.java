package com.ecc.service.transaction;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.handler.ContractHandler;
import com.ecc.service.RestTemplate;
import com.ecc.util.converter.Base64Util;
import com.ecc.util.converter.BytesUtil;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.RsaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.ecc.constants.ApplicationConstants.SERVER_URL;

@Service
public class TransactionService {
    @Autowired
    TaskExecutor taskExecutor;
    @Autowired
    RestTemplate restTemplate;

    public String signTicket(String fileId, String signFor, String permissions){
        HashMap<String, String> params = new HashMap<>();
        params.put("fileId", fileId);
        params.put("token", Peer.getInstance().getToken());
        List<FileTransaction> fileTransactions = restTemplate.get(SERVER_URL + "api/file-service/transaction/files/fileId", params, new ParameterizedTypeReference<List<FileTransaction>>() {
        });

        if (!fileTransactions.get(0).getOwner().equals(Peer.getInstance().getEmail())) {
            throw new CustomException(ExceptionCollection.FILE_TICKET_CREATE_ERROR);
        }

        String transactionId = UUID.randomUUID().toString();
        TicketTransaction ticketTransaction = TicketTransaction.builder()
                .id(transactionId)
                .fileId(fileId)
                .signFor(signFor)
                .timestamp(DateUtil.getDate())
                .permission(permissions)
                .signer(Peer.getInstance().getEmail())
                .build();
        ticketTransaction.setTransactionType(TransactionType.TICKET);
        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getInstance().getEmail()).getPrivateKey();

        Contract contract = Contract.builder()
                .id(UUID.randomUUID().toString())
                .channel("default_queue")
                .transactionType(TransactionType.TICKET)
                .transactionId(transactionId)
                .timestamp(DateUtil.getDate())
                .build();
        ContractHandler.sign(Contract.SENDER_SIGN, contract, peerPrivateKey);

        sendTransaction(ticketTransaction);
        sendContract(contract);

        createCodeImg(ticketTransaction);

        return Base64Util.encode(BytesUtil.toBytes(ticketTransaction));
    }

    private void sendContract(Contract contract) {
        taskExecutor.execute(() -> restTemplate.post(SERVER_URL + "api/contract-service/verify/sender", null, contract, null));

    }

    private void sendTransaction(TicketTransaction transaction) {
        taskExecutor.execute(() -> restTemplate.post(SERVER_URL + "api/file-service/transaction/ticket", null, transaction, null));
    }

    private void createCodeImg(TicketTransaction ticketTransaction){
        //todo: 生成二维码
    }
}
