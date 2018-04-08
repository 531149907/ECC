package com.ecc.service.transfer.impl;

import com.ecc.service.transfer.TransferHandler;
import com.ecc.util.crypto.AesUtil;
import com.ecc.util.shard.ShardUtil;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;

import static com.ecc.constants.ApplicationConstants.PATH_DOWNLOAD;
import static com.ecc.constants.ApplicationConstants.PATH_TEMP;

public class TransferHandlerImpl implements TransferHandler {
    private static TransferHandlerImpl handler = new TransferHandlerImpl();

    public static TransferHandlerImpl getHandler() {
        return handler;
    }

    private TransferHandlerImpl() {
    }

    @Override
    public void encryptFile(String path, String password) throws Exception {
        Path input = Paths.get(path);
        Path output = Paths.get(PATH_TEMP + input.getFileName());
        AesUtil.encrypt(password, input, output);
    }

    @Override
    public void decryptFile(String path, String password) throws Exception {
        Path input = Paths.get(path);
        Path output = Paths.get(PATH_DOWNLOAD + input.getFileName());
        AesUtil.decrypt(password, input,output);
    }

    @Override
    public List<Path> splitFile(String path) throws Exception {
        //todo: deal with files using rs-code
        return ShardUtil.dealWithFile(ShardUtil.SPLIT_MODE,path);
    }

    @Override
    public List<Path> combineFiles(String path) throws Exception {
        return ShardUtil.dealWithFile(ShardUtil.RECOVER_MODE,path);
    }

    @Override
    public void deleteTempShards() {
        try {
            Path tempPath = Paths.get(PATH_TEMP);
            TempFileVisitor fileVisitor = new TempFileVisitor();
            Files.walkFileTree(tempPath, fileVisitor);
            HashMap<String, Path> shardPaths = fileVisitor.getShardPaths();
            for (String fileName : shardPaths.keySet()) {
                Files.delete(shardPaths.get(fileName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<String, Path> getShardPaths() {
        Path tempPath = Paths.get(PATH_TEMP);
        TempFileVisitor fileVisitor = new TempFileVisitor();
        try {
            Files.walkFileTree(tempPath, fileVisitor);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileVisitor.getShardPaths();
    }

    @Getter
    private class TempFileVisitor extends SimpleFileVisitor<Path> {
        private HashMap<String, Path> shardPaths = new HashMap<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            shardPaths.put(file.getFileName().toString(), file);
            return FileVisitResult.CONTINUE;
        }
    }
}
