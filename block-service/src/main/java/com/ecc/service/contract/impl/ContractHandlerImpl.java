package com.ecc.service.contract.impl;

import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.exceptions.ContractException;
import com.ecc.service.contract.ContractHandler;
import com.ecc.util.crypto.RsaUtil;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

import static com.ecc.domain.contract.Contract.*;

public class ContractHandlerImpl implements ContractHandler {

    private static ContractHandlerImpl handler = new ContractHandlerImpl();

    public static ContractHandlerImpl getHandler() {
        return handler;
    }

    private ContractHandlerImpl() {

    }

    @Override
    public void sign(String signType, Contract contract,PrivateKey privateKey) {
        switch (signType) {
            case SENDER_SIGN:
                contract.setSenderSign(RsaUtil.sign(contract.getRawMessage(),privateKey));
                break;
            case RECEIVER_SIGN:
                String temp = contract.getRawMessage() + contract.getSenderSign();
                contract.setReceiverSign(RsaUtil.sign(temp));
                break;
            default:
                throw new ContractException("Invalid type of signature!");
        }
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

    @Override
    public Contract extractContractFromBlock(String contractId, Block block) {
        List<Contract> contracts = block.getContracts();
        for (Contract contract : contracts) {
            if (contract.getId().equals(contractId)) {
                return contract;
            }
        }
        throw new ContractException("Cannot extract contract in given block!");
    }
}
