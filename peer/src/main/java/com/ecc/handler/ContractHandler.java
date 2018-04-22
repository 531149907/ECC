package com.ecc.handler;

import com.ecc.domain.contract.Contract;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.util.crypto.RsaUtil;

import java.security.PrivateKey;

import static com.ecc.domain.contract.Contract.RECEIVER_SIGN;
import static com.ecc.domain.contract.Contract.SENDER_SIGN;

public class ContractHandler extends BaseContractHandler {

    public static void sign(String signType, Contract contract, PrivateKey privateKey){
        switch (signType) {
            case SENDER_SIGN:
                contract.setSenderSign(RsaUtil.sign(contract.getRawMessage(), privateKey));
                break;
            case RECEIVER_SIGN:
                String temp = contract.getRawMessage() + contract.getSenderSign();
                contract.setReceiverSign(RsaUtil.sign(temp));
                break;
            default:
                throw new CustomException(ExceptionCollection.CONTRACT_VERIFY_FAILED_NO_SUCH_TYPE_SUPPORT);
        }
    }
}
