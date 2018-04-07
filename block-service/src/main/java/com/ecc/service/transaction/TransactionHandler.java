package com.ecc.service.transaction;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;

import java.util.List;

public interface TransactionHandler {
    List<FileTransaction> createFileTransactions(String filePath, String owner, List<String> holder, String fileLevel);

    TicketTransaction createTicketTransaction(String fileId);
}
