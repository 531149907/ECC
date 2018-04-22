package com.ecc.service;

import com.ecc.dao.BlockMapper;
import com.ecc.domain.block.Block;
import com.ecc.util.converter.BytesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlockService {

    @Autowired
    BlockMapper blockMapper;

    public Block getBlock(int index) {
        byte[] rawBlock = blockMapper.getBlock(index);
        return (Block) BytesUtil.toObject(rawBlock);
    }

    public void addBlock(byte[] rawBlock) {
        blockMapper.addBlock(rawBlock);
    }

    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();

        for (byte[] bytes : blockMapper.getAllBlocks()) {
            blocks.add((Block) BytesUtil.toObject(bytes));
        }

        return blocks;
    }
}
