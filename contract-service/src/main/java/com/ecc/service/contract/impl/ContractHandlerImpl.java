package com.ecc.service.contract.impl;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.service.exceptions.ContractException;
import com.ecc.service.contract.ContractHandler;
import com.ecc.util.crypto.RsaUtil;

import java.security.PublicKey;

import static com.ecc.domain.contract.Contract.VERIFY_RECEIVER_SIGN;
import static com.ecc.domain.contract.Contract.VERIFY_SENDER_SIGN;

public class ContractHandlerImpl implements ContractHandler {

    private static ContractHandlerImpl handler = new ContractHandlerImpl();

    public static ContractHandlerImpl getHandler() {
        return handler;
    }

    private ContractHandlerImpl() {

    }

    @Override
    public boolean verify(String verifyType, Contract contract, PublicKey publicKey) {
        if (verifyType.equals(VERIFY_RECEIVER_SIGN)
                && contract.getTransactionType().equals(TransactionType.TICKET)) {
            return true;
        }

        String rawMessage = contract.getRawMessage();
        String signedMessage;

        switch (verifyType) {
            case VERIFY_SENDER_SIGN:
                signedMessage = contract.getSenderSign();
                break;
            case VERIFY_RECEIVER_SIGN:
                rawMessage = rawMessage + contract.getSenderSign();
                signedMessage = contract.getReceiverSign();
                break;
            default:
                throw new ContractException("Invalid type of verification!");
        }

        return RsaUtil.verify(publicKey, rawMessage, signedMessage);
    }
}
