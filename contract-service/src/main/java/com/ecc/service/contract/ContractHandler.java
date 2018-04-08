package com.ecc.service.contract;

import com.ecc.domain.contract.Contract;

import java.security.PublicKey;

public interface ContractHandler {

    boolean verify(String verifyType, Contract contract, PublicKey publicKey);

}
