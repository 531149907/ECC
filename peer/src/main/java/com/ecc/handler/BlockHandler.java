package com.ecc.handler;

import com.ecc.domain.block.Block;
import com.ecc.domain.contract.Contract;
import com.ecc.domain.peer.Peer;
import com.ecc.domain.transaction.impl.FileTransaction;
import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;
import com.ecc.service.RestTemplate;
import com.ecc.util.converter.BytesUtil;
import com.ecc.util.crypto.RsaUtil;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.ecc.constants.ApplicationConstants.*;

public class BlockHandler {
    private static final Path blockBasePath = Paths.get(PATH_BLOCK);

    private static Integer counter = 0;
    private static ConcurrentHashMap<Integer, Path> blockPaths = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Contract> tempBlock = new ConcurrentHashMap<>();
    private static int maxBlockIndex;

    public static void addContractToBlock(Contract contract) {
        RestTemplate restTemplate = new RestTemplate();
        HashMap<String, String> params = new HashMap<>();

        params.put("id", contract.getTransactionId());
        FileTransaction transaction = restTemplate.get(SERVER_URL + "api/file-service/transaction/file", params, FileTransaction.class);
        String sender = transaction.getOwner();
        String receiver = transaction.getHolder();

        params = new HashMap<>();
        params.put("email", sender);
        String senderPublicKey = restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class).getPublicKey();

        params = new HashMap<>();
        params.put("email", receiver);
        String receiverPublicKey = restTemplate.get(SERVER_URL + "api/user-service/peer", params, Peer.class).getPublicKey();


        if (!ContractHandler.verify(Contract.VERIFY_SENDER_SIGN, contract, RsaUtil.getPublicKeyFromString(senderPublicKey))) {
            throw new CustomException(ExceptionCollection.BLOCK_INSERT_ERROR);
        }

        if (!ContractHandler.verify(Contract.VERIFY_RECEIVER_SIGN, contract, RsaUtil.getPublicKeyFromString(receiverPublicKey))) {
            throw new CustomException(ExceptionCollection.BLOCK_INSERT_ERROR);
        }

        tempBlock.put(UUID.randomUUID().toString(), contract);
    }

    public static void syncBlock() {
        //todo: read Block form block-service
        flushBlocks();
        for (Map.Entry<Integer, Path> entry : blockPaths.entrySet()) {
            try {
                Files.deleteIfExists(entry.getValue());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        RestTemplate restTemplate = new RestTemplate();
        List<Block> blocks = restTemplate.get(SERVER_URL + "api/block-service/blocks", null, new ParameterizedTypeReference<List<Block>>() {
        });

        for (Block block : blocks) {
            Path path = Paths.get(PATH_BLOCK + block.getIndex() + SUFFIX_BLOCK);
            try {
                Files.write(path, BytesUtil.toBytes(block));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Contract getContractFromTempBlock(String contractId) {
        for (Map.Entry<String, Contract> contractEntry : tempBlock.entrySet()) {
            if (contractEntry.getValue().getId().equals(contractId)) {
                return contractEntry.getValue();
            }
        }
        return null;
    }

    public static void clearTempBlock() {
        tempBlock.clear();
    }

    public static boolean verifyAndImportBlock(Block newBlock) {
        ConcurrentHashMap<String, Contract> newTempBlock = new ConcurrentHashMap<>();
        newTempBlock.putAll(tempBlock);
        clearTempBlock();

        if (!getBlock(maxBlockIndex).hash().equals(newBlock.getPrevHash())) {
            importMissingBlocks();
        }

        if (newTempBlock.size() != newBlock.getContracts().size()) {
            //importMissingContracts(newBlock, newTempBlock);
        }

       /* Block block = new Block();
        List<Contract> tempContracts = new ArrayList<>();
        for (Map.Entry<String, Contract> entry : newTempBlock.entrySet()) {
            tempContracts.add(entry.getValue());
        }
        block.setContracts(tempContracts);

        if (block.getMerkleTreeRoot().equals(newBlock.getMerkleTreeRoot())) {
            clearTempBlock();

            Path newBlockPath = Paths.get(PATH_BLOCK + newBlock.getIndex() + SUFFIX_BLOCK);
            try {
                Files.deleteIfExists(newBlockPath);
                Files.write(newBlockPath, BytesUtil.toBytes(newBlock));
            } catch (IOException e) {
                e.printStackTrace();
            }

            flushBlocks();
            return true;
        }
        return false;*/

        try {
            Files.write(Paths.get(PATH_BLOCK + (++maxBlockIndex) + SUFFIX_BLOCK), BytesUtil.toBytes(newBlock));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Block getBlock(int blockIndex) {
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

    public static List<Block> getBlocks() {
        List<Block> blockList = new ArrayList<>();
        try {
            for (int index : blockPaths.keySet()) {
                byte[] bytes = Files.readAllBytes(blockPaths.get(index));
                blockList.add((Block) BytesUtil.toObject(bytes));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return blockList;
    }

    public static void importMissingBlocks() {
        List<Integer> missingBlockIndex = new ArrayList<>();

        try {
            for (int index = 0; index < maxBlockIndex; index++) {
                Block block = getBlock(index);
                Block nextBlock = getBlock(index + 1);
                if (block == null) {
                    for (int i = index; i <= maxBlockIndex; i++) {
                        missingBlockIndex.add(i);
                    }
                    break;
                } else if (nextBlock != null && !block.hash().equals(nextBlock.getPrevHash())) {
                    for (int i = index; i <= maxBlockIndex; i++) {
                        missingBlockIndex.add(i);
                    }
                    break;
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int var0 : missingBlockIndex) {
                builder.append(var0).append(",");
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("index", builder.toString());
            //todo: 从 order service 获取缺失的 blocks(service 根据hash是否一致，返回不一致hash的blocks), Key->index
            RestTemplate restTemplate = new RestTemplate();
            List<Block> missingBlocks = restTemplate.get(SERVER_URL + "api/block-service/blocks", params, new ParameterizedTypeReference<List<Block>>() {
            });

            for (Block block : missingBlocks) {
                Path blockPath = Paths.get(PATH_BLOCK + block.getIndex() + SUFFIX_BLOCK);
                Files.deleteIfExists(blockPath);
                byte[] blockBytes = BytesUtil.toBytes(block);
                Files.write(blockPath, blockBytes);
            }

            flushBlocks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    private static void importMissingContracts(Block newBlock, ConcurrentHashMap<String, Contract> newTempBlock) {
        RestTemplate restTemplate = new RestTemplate();
        List<String> missingIds = new ArrayList<>();
        HashMap<Peer, List<Contract>> tempContracts = new HashMap<>();

        for (Contract newBlockContracts : newBlock.getContracts()) {
            if (getContractFromTempBlock(newBlockContracts.getId(), newTempBlock) == null) {
                missingIds.add(newBlockContracts.getId());
            }
        }

        StringBuilder builder = new StringBuilder();
        for (String id : missingIds) {
            builder.append(id).append(",");
        }

        HashMap<String, String> params = new HashMap<>();
        List<Peer> peers = restTemplate.get(SERVER_URL + "api/user-service/peers", null, new ParameterizedTypeReference<List<Peer>>() {
        });
        for (Peer peer : peers) {
            List<Contract> contractList = restTemplate.get(peer.getIp() + ":" + peer.getPort() + "/block/contracts", params, new ParameterizedTypeReference<List<Contract>>() {
            });
            tempContracts.putIfAbsent(peer, contractList);
        }

    }

    private static Contract getContractFromTempBlock(String contractId, ConcurrentHashMap<String, Contract> newTempBlock) {
        for (Map.Entry<String, Contract> contractEntry : newTempBlock.entrySet()) {
            if (contractEntry.getValue().getId().equals(contractId)) {
                return contractEntry.getValue();
            }
        }
        return null;
    }

    private static void flushBlocks() {
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
    private static class BlockVisitor extends SimpleFileVisitor<Path> {
        private ConcurrentHashMap<Integer, Path> blockPaths = new ConcurrentHashMap<>();
        private int maxBlockIndex = -1;
        private String suffix = SUFFIX_BLOCK;

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.toString().endsWith(suffix)) {
                String fileName = file.getFileName().toString().replace(suffix, "");
                int index;
                try {
                    index = Integer.valueOf(fileName);
                } catch (NumberFormatException e) {
                    return FileVisitResult.CONTINUE;
                }
                blockPaths.put(index, file);
                if (index > maxBlockIndex) {
                    maxBlockIndex = index;
                }
            }
            return FileVisitResult.CONTINUE;
        }
    }

    /*private void createGodBlock() {
        Block godBlock = Block.builder()
                .index(0)
                .prevHash("")
                .contracts(null)
                .merkleTreeRoot("")
                .timestamp("2018-4-19")
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
    }*/
}
