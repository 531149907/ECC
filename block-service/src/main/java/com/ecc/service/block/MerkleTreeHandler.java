package com.ecc.service.block;

import com.ecc.domain.block.Block;

public interface MerkleTreeHandler {
    String buildTree(Block block);
}
