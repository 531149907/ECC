package com.ecc.service.block.impl;

import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;
import com.ecc.service.RestTemplate;
import com.ecc.service.block.BlockHandler;
import com.ecc.service.contract.impl.ContractHandlerImpl;
import com.ecc.util.converter.BytesUtil;
import com.ecc.util.converter.DateUtil;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.ecc.constants.ApplicationConstants.PATH_BLOCK;
import static com.ecc.constants.ApplicationConstants.SUFFIX_BLOCK;

public class BlockHandlerImpl implements BlockHandler {
    private static final Path blockBasePath = Paths.get(PATH_BLOCK);
    private static final Path tempBlockPath = Paths.get(PATH_BLOCK + "temp" + SUFFIX_BLOCK);

    private static BlockHandlerImpl handler = new BlockHandlerImpl();
    private HashMap<Integer, Path> blockPaths = new HashMap<>();
    private int maxBlockIndex;

    public static BlockHandlerImpl getHandler() {
        return handler;
    }

    private BlockHandlerImpl() {
        BlockVisitor blockVisitor = new BlockVisitor();
        try {
            Files.walkFileTree(blockBasePath, blockVisitor);
            maxBlockIndex = blockVisitor.getMaxBlockIndex();
            blockPaths = blockVisitor.getBlockPaths();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTempBlock() {
        deleteTempBlock();
        try {
            Files.createFile(tempBlockPath);
            flushBlocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTempBlock() {
        try {
            Files.deleteIfExists(tempBlockPath);
            flushBlocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteBlock(int blockIndex) {
        Path blockPath = Paths.get(PATH_BLOCK + blockIndex + SUFFIX_BLOCK);
        try {
            Files.deleteIfExists(blockPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addContractToBlock(Contract contract) throws Exception {
        //todo: 获取 public key 要区分 sender, receiver
        PublicKey publicKey = null;

        if (!ContractHandlerImpl.getHandler().verify(Contract.VERIFY_SENDER_SIGN, contract, publicKey)) {
            throw new Exception("Cannot add contract to block! Because signature verification failed!");
        }

        if (!ContractHandlerImpl.getHandler().verify(Contract.VERIFY_RECEIVER_SIGN, contract, publicKey)) {
            throw new Exception("Cannot add contract to block! Because signature verification failed!");
        }

        try {
            byte[] rawData = Files.readAllBytes(tempBlockPath);
            List<Contract> contracts = ((Block) BytesUtil.toObject(rawData)).getContracts();
            contracts.add(contract);
            contracts.sort(Comparator.comparing(Contract::getTimestamp));
            rawData = BytesUtil.toBytes(contracts);
            Files.write(tempBlockPath, rawData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Block getTempBlock() {
        if (Files.exists(tempBlockPath)) {
            try {
                byte[] blockBytes = Files.readAllBytes(tempBlockPath);
                return (Block) BytesUtil.toObject(blockBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Block getBlock(int blockIndex) {
        Path blockPath = blockPaths.get(blockIndex);
        if (blockPath != null) {
            try {
                byte[] blockBytes = Files.readAllBytes(blockPath);
                return (Block) BytesUtil.toObject(blockBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    public List<Block> getBlocks() {
        List<Block> blockList = new ArrayList<>();
        try {
            for (int index : blockPaths.keySet()) {
                byte[] bytes = Files.readAllBytes(blockPaths.get(index));
                blockList.add((Block) BytesUtil.toObject(bytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void createGodBlock() {
        Block godBlock = Block.builder()
                .index(0)
                .prevHash("")
                .contracts(null)
                .merkleTreeRoot("")
                .timestamp(DateUtil.getDate())
                .build();

        Path godBlockPath = Paths.get(PATH_BLOCK + 0 + SUFFIX_BLOCK);

        try {
            if (Files.exists(godBlockPath)) {
                Files.deleteIfExists(godBlockPath);
            }
            Files.createFile(godBlockPath);
            byte[] godBlockByte = BytesUtil.toBytes(godBlock);
            Files.write(godBlockPath, godBlockByte);
            flushBlocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyAndImportBlock(Block newBlock) throws Exception {
        Block tempBlock = getTempBlock();
        if (!tempBlock.hash().equals(newBlock.getPrevHash())) {
            importMissingBlocks(newBlock.getIndex());
        }
        if (tempBlock.getContracts().size() != newBlock.getContracts().size()) {
            importMissingContracts(newBlock);
        }

        if (tempBlock.getMerkleTreeRoot().equals(newBlock.getMerkleTreeRoot())) {
            deleteTempBlock();

            Path newBlockPath = Paths.get(PATH_BLOCK + newBlock.getIndex() + SUFFIX_BLOCK);
            try {
                Files.deleteIfExists(newBlockPath);
                Files.createFile(newBlockPath);
                Files.write(newBlockPath, BytesUtil.toBytes(newBlock));
            } catch (IOException e) {
                e.printStackTrace();
            }

            flushBlocks();
            return true;
        }

        return false;
    }

    @Override
    public void importMissingBlocks(int currentBlockIndex) {
      /*  RestTemplate connection = new RestTemplate();
        HashMap<String, Object> blockHashMap = new HashMap<>();

        try {
            for (int index = 0; index < currentBlockIndex - 1; index++) {
                Path blockPath = Paths.get(PATH_BLOCK + index + SUFFIX_BLOCK);
                if (Files.exists(blockPath)) {
                    byte[] blockBytes = Files.readAllBytes(blockPath);
                    Block block = (Block) BytesUtil.toObject(blockBytes);
                    blockHashMap.put(String.valueOf(index), block.hash());
                }
            }

            //todo: 从 order service 获取缺失的 blocks(service 根据hash是否一致，返回不一致hash的blocks), Key->index
            HashMap<String, Block> missingBlocks = (HashMap<String, Block>) connection.post("", blockHashMap, null);

            for (String index : missingBlocks.keySet()) {
                Path blockPath = Paths.get(PATH_BLOCK + index + SUFFIX_BLOCK);
                Files.deleteIfExists(blockPath);
                Files.createFile(blockPath);
                byte[] blockBytes = BytesUtil.toBytes(missingBlocks.get(index));
                Files.write(blockPath, blockBytes);
            }

            flushBlocks();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void importMissingContracts(Block newBlock) throws Exception {
/*        Block tempBlock = getTempBlock();
        RestTemplate restTemplate = new RestTemplate();
        List<String> missingIds = new ArrayList<>();
        HashMap<String, Contract> newBlockContracts = new HashMap<>();


        boolean exists = false;
        for (Contract newContract : newBlock.getContracts()) {
            for (Contract tempContract : tempBlock.getContracts()) {
                if (tempContract.hash().equals(newContract.hash())) {
                    exists = true;
                }
            }
            if (!exists) {
                missingIds.add(newContract.getId());
                newBlockContracts.put(newContract.getId(), newContract);
            }
            exists = false;
        }

        //todo: 获取Peer列表
        List<String> peerList = (List<String>) restTemplate.get("", null, null);
        peerList = peerList.subList(0, (int) (peerList.size() * 0.4));

        HashMap<String, List<Contract>> contractCollection = new HashMap<>();

        for (String peer : peerList) {
            //todo: 请求 peer 根据 block index 和给定 id 返回 contracts 列表
            HashMap<String, Object> contractIdCollection = new HashMap<>();
            contractIdCollection.put("contract_ids", missingIds);
            contractIdCollection.put("block_id", maxBlockIndex);
            contractCollection.put(peer, (List<Contract>) restTemplate.post("", null, null));
        }

        for (String contractId : missingIds) {
            int match = 0;
            int total = 0;
            String contractIdKey = "";

            for (String peer : contractCollection.keySet()) {
                for (Contract contract : contractCollection.get(peer)) {
                    if (contractId.equals(contract.getId())) {
                        total++;
                        if (contract.hash().equals(newBlockContracts.get(contractId).hash())) {
                            match++;
                            contractIdKey = contractId;
                        }
                    }
                }
            }

            if (match / total > 0.75) {
                addContractToBlock(newBlockContracts.get(contractIdKey));
            }
        }*/
    }

    @Override
    public void flushBlocks() {
        BlockVisitor blockVisitor = new BlockVisitor();
        try {
            Files.walkFileTree(blockBasePath, blockVisitor);
            maxBlockIndex = blockVisitor.getMaxBlockIndex();
            blockPaths = blockVisitor.getBlockPaths();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Getter
    private class BlockVisitor extends SimpleFileVisitor<Path> {
        private HashMap<Integer, Path> blockPaths = new HashMap<>();
        private int maxBlockIndex = -1;
        private String suffix = SUFFIX_BLOCK;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.toString().endsWith(suffix)) {
                String fileName = file.getFileName().toString().replace(suffix, "");
                int index = Integer.valueOf(fileName);
                blockPaths.put(index, file);
                if (index > maxBlockIndex) {
                    maxBlockIndex = index;
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
