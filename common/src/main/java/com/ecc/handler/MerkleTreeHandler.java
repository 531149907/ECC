package com.ecc.handler;

import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;
import com.ecc.util.crypto.HashUtil;

import java.util.ArrayList;
import java.util.List;

public class MerkleTreeHandler {

    public static String buildTree(Block block) {
        String root;
        List<String> hashLayer;
        List<String> higherHashLayer;
        List<Contract> contracts = block.getContracts();

        if (block.getContracts() != null && block.getContracts().size() != 0) {
            if (block.getContracts().size() == 1) {
                return contracts.get(0).hash();
            }
            hashLayer = new ArrayList<>();
            higherHashLayer = new ArrayList<>();
            for (Contract contract : contracts) {
                hashLayer.add(contract.hash());
            }
            while (hashLayer.size() != 1) {
                for (int index = 0; index < hashLayer.size(); index = index + 2) {
                    try {
                        hashLayer.get(index + 1);
                    } catch (IndexOutOfBoundsException e) {
                        higherHashLayer.add(hashLayer.get(index));
                        break;
                    }
                    String parentHash = hash(hashLayer.get(index), hashLayer.get(index + 1));
                    higherHashLayer.add(parentHash);
                }
                hashLayer.clear();
                hashLayer.addAll(higherHashLayer);
                higherHashLayer.clear();
            }
            root = hashLayer.get(0);
            return root;
        }
        return "";
    }

    private static String hash(String left, String right) {
        return HashUtil.hash(left + right);
    }
}
