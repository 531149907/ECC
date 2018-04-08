package com.ecc.service.transfer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public interface TransferHandler {
    void encryptFile(String path, String password) throws Exception;

    void decryptFile(String path, String password) throws Exception;

    List<Path> splitFile(String path) throws Exception;

    List<Path> combineFiles(String path) throws Exception;

    void deleteTempShards();

    @Deprecated
    HashMap<String, Path> getShardPaths();
}
