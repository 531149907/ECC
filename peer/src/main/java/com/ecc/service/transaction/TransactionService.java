package com.ecc.service.transaction;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.service.RestTemplate;
import com.ecc.service.contract.ContractHandler;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.converter.DateUtil;
import com.ecc.util.crypto.RsaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

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

    public void signTicket(String fileId, String signFor, String permissions) throws Exception {
        /*ContractHandler contractHandler = ContractHandlerImpl.getHandler();

        //check if fileId belongs to signer
        HashMap<String, Object> params = new HashMap<>();
        params.put("fileId", fileId);
        params.put("token", Peer.getInstance().getToken());
        List<FileTransaction> fileTransactions = restTemplate.get(SERVER_URL + "file-service/transaction", params, List.class);

        if (!fileTransactions.get(0).getOwner().equals(Peer.getInstance().getEmail())) {
            throw new Exception("File not belongs to you! Cannot sign ticket!");
        }

        String transactionId = UUID.randomUUID().toString();
        TicketTransaction ticketTransaction = TicketTransaction.builder()
                .id(transactionId)
                .fileId(fileId)
                .signFor(signFor)
                .timestamp(DateUtil.getDate())
                .permissions(permissions)
                .signer(Peer.getInstance().getEmail())
                .build();

        PrivateKey peerPrivateKey = RsaUtil.loadKeyPair(Peer.getInstance().getEmail()).getPrivateKey();

        Contract contract = Contract.builder()
                .id(UUID.randomUUID().toString())
                .channel("default_queue")
                .transactionType(TransactionType.TICKET)
                .transactionId(transactionId)
                .timestamp(DateUtil.getDate())
                .build();
        contractHandler.sign(Contract.SENDER_SIGN, contract, peerPrivateKey);

        sendTransaction(ticketTransaction);
        sendContract(contract);*/
    }

    private void sendContract(Contract contract) {
        /*taskExecutor.execute(() -> {
            HashMap<String,Object> params = new HashMap<>();
            params.put("token",Peer.getInstance().getToken());
            params.put("contract",contract);
            restTemplate.post(SERVER_URL+"contract-service/upload",params,null);
        });*/
    }

    private void sendTransaction(TicketTransaction transaction) {
       /* taskExecutor.execute(() -> {
            HashMap<String,Object> params = new HashMap<>();
            params.put("token",Peer.getInstance().getToken());
            params.put("transaction",transaction);
            restTemplate.post(SERVER_URL+"ticket/create",params,null);
        });*/
    }
}
