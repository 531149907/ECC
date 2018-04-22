package com.ecc.handler;

import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.TransactionType;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.util.crypto.RsaUtil;

import java.security.PublicKey;

import static com.ecc.domain.contract.Contract.VERIFY_RECEIVER_SIGN;
import static com.ecc.domain.contract.Contract.VERIFY_SENDER_SIGN;

public class BaseContractHandler {

    public static boolean verify(String verifyType, Contract contract, PublicKey publicKey){
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
                throw new CustomException(ExceptionCollection.CONTRACT_VERIFY_FAILED_NO_SUCH_TYPE_SUPPORT);
        }

        return RsaUtil.verify(publicKey, rawMessage, signedMessage);
    }

}
