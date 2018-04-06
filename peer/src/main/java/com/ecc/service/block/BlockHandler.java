package com.ecc.service.block;

import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;

import java.util.List;

public interface BlockHandler {
    //产生临时Block
    void createTempBlock();

    //删除临时Block
    void deleteTempBlock();

    //根据 index 删除 block
    void deleteBlock(int blockIndex);

    //往临时 Block 添加Contract
    void addContractToBlock(Contract contract);

    //获取临时 Block
    Block getTempBlock();

    //根据 index 获取本地 Block
    Block getBlock(int blockIndex);

    //获取所有本地 Blocks
    List<Block> getBlocks();

    //创建创世 Block
    void createGodBlock();

    //验证 order service 广播的 block 与本地的 block 是否一致，并进行本地上链
    boolean verifyAndImportBlock(Block newBlock);

    //导入缺失的 blocks
    void importMissingBlocks(int currentBlockIndex);

    //导入temp block 中缺失的contracs
    void importMissingContracts(Block newBlock);

    //更新 Block 池
    void flushBlocks();
}
