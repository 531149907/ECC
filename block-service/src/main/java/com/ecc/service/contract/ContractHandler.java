package com.ecc.service.contract;

import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface ContractHandler {
    void sign(String signType, Contract contract, PrivateKey privateKey);

    boolean verify(String verifyType, Contract contract, PublicKey publicKey);

    Contract extractContractFromBlock(String contractId, Block block);
}
