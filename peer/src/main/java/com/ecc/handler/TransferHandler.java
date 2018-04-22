package com.ecc.handler;

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

public class TransferHandler {

    public static void encryptFile(String path, String password) throws Exception {
        Path input = Paths.get(path);
        Path output = Paths.get(PATH_TEMP + input.getFileName());
        AesUtil.encrypt(password, input, output);
    }

    public static void decryptFile(String path, String password) throws Exception {
        Path input = Paths.get(path);
        Path output = Paths.get(PATH_DOWNLOAD + input.getFileName());
        AesUtil.decrypt(password, input, output);
    }

    public static List<Path> splitFile(String path){
        //todo: deal with files using rs-code
        return ShardUtil.dealWithFile(ShardUtil.SPLIT_MODE, path);
    }

    public static List<Path> combineFiles(String path){
        return ShardUtil.dealWithFile(ShardUtil.RECOVER_MODE, path);
    }

    public static void deleteTempShards() {
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

    public static HashMap<String, Path> getShardPaths() {
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
    private static class TempFileVisitor extends SimpleFileVisitor<Path> {
        private HashMap<String, Path> shardPaths = new HashMap<>();

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            shardPaths.put(file.getFileName().toString(), file);
            return FileVisitResult.CONTINUE;
        }
    }
}
