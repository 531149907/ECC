package com.ecc.service.contract;

import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.transaction.Transaction;

import java.security.KeyException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface ContractHandler {
    void sign(String signType, Contract contract, PrivateKey privateKey) throws KeyException;

    boolean verify(String verifyType, Contract contract, PublicKey publicKey);

    Contract extractContractFromBlock(String contractId, Block block);
}
