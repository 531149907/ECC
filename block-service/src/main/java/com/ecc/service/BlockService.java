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
        try {
            byte[] rawBlock = blockMapper.getBlock(index);
            return (Block) BytesUtil.toObject(rawBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addBlock(byte[] rawBlock) {
        try {
            blockMapper.addBlock(rawBlock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<>();
        try {
            for (byte[] bytes : blockMapper.getAllBlocks()) {
                blocks.add((Block) BytesUtil.toObject(bytes));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return blocks;
    }
}
