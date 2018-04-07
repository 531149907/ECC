package com.ecc.service.transaction.impl;

import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.domain.transaction.impl.TicketTransaction;
import com.ecc.service.transaction.TransactionHandler;

import java.util.List;

public class TransactionHandlerImpl implements TransactionHandler {
    @Override
    public List<FileTransaction> createFileTransactions(String filePath, String owner, List<String> holder, String fileLevel) {
        return null;
    }


    @Override
    public TicketTransaction createTicketTransaction(String fileId) {
        return null;
    }
}
