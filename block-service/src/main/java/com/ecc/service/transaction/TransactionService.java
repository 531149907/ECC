package com.ecc.service.transaction;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.Transaction;
import com.ecc.service.block.BlockService;
import com.ecc.service.common.net.RestTemplate;
import com.ecc.service.contract.ContractService;
import com.ecc.service.peer.PeerService;
import com.ecc.service.transfer.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BlockService blockService;
    @Autowired
    ContractService contractService;
    @Autowired
    TransferService transferService;
    @Autowired
    PeerService peerService;

    /*public void signTicket(String fileId, String signFor, String permissions) {
        KeyStorage keyStorage = RsaUtil.loadKeyPair(Peer.getPeer().getEmail());
        ContractHandler contractHandler = ContractHandlerImpl.getHandler();

        //todo: check if fileId belongs to signer
        HashMap<String, Object> params = new HashMap<>();
        params.put("file_id", fileId);
        params.put("signer", Peer.getPeer().getEmail());
        Report report = restTemplate.get("", params, null);

        if (report.getCode() == 200) {
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

        return null;
    }*/

    private void sendContract(Contract contract) {
        //todo: 传输 contract 给 contract service
        restTemplate.post("", null, null);
    }

    private String sendTransaction(Transaction transaction) {
        //todo: 传输 transaction 给 file service
        return restTemplate.post("", null, String.class);
    }
}
