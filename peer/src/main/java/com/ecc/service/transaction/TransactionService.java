package com.ecc.service.transaction;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.RsaUtil;
import com.ecc.web.api.ContractServiceApi;
import com.ecc.web.api.FileServiceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {
    @Autowired
    FileServiceApi fileServiceApi;
    @Autowired
    ContractServiceApi contractServiceApi;

    public void signTicket(String fileId, String signFor, String permissions) throws Exception {
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();

        //check if fileId belongs to signer
        List<FileTransaction> fileTransactions = fileServiceApi.getFileTransactions(fileId);
        if (!fileTransactions.get(0).getOwner().equals(Peer.getPeer().getEmail())) {
            throw new Exception("File not belongs to you! Cannot sign ticket!");
        }

        String transactionId = UUID.randomUUID().toString();
        TicketTransaction ticketTransaction = TicketTransaction.builder()
                .id(transactionId)
                .fileId(fileId)
                .signFor(signFor)
                .timestamp(DateUtil.getDate())
                .permissions(permissions)
                .signer(Peer.getPeer().getEmail())
                .build();

        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getPeer().getEmail()).getPrivateKey();

        Contract contract = Contract.builder()
                .id(UUID.randomUUID().toString())
                .channel("default_queue")
                .transactionType(TransactionType.TICKET)
                .transactionId(transactionId)
                .timestamp(DateUtil.getDate())
                .build();
        contractHandler.sign(Contract.SENDER_SIGN, contract, peerPrivateKey);

        sendTransaction(ticketTransaction);
        sendContract(contract);
    }

    private void sendContract(Contract contract) {
        contractServiceApi.uploadSenderSignedContract(contract);
    }

    private void sendTransaction(TicketTransaction transaction) {
        fileServiceApi.createTicket(transaction);
    }
}
