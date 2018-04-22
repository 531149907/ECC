package com.ecc.util;

import com.ecc.util.crypto.AesUtil;
import com.ecc.util.shard.ShardUtil;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ShardTest {
    public static final String SPLIT_MODE = "split_mode";
    public static final String RECOVER_MODE = "recover_mode";

    private static final int MAX_SHARDS = 5;
    private static final int CHECK_BLOCK = 2;
    private static final int BIT_WORD_SIZE = 16;
    private static final int PACKET_SIZE = 8;
    private static final int BUFFER_SIZE = 1024;
    private static final String CODING_TECHNIQUE = "reed_sol_van";

    private static final String osPlatform = System.getProperty("os.name");

    @Test
    public void mainTest() throws Exception {
        //encryptFile();
        //shard();
        //combine();
        decryptFile();
    }

    @Test
    public void shard() throws Exception {
        ShardUtil.dealWithFile(ShardUtil.SPLIT_MODE, "/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted.PNG");
    }

    @Test
    public void combine() throws Exception {
        ShardUtil.dealWithFile(ShardUtil.RECOVER_MODE, "/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted.PNG");
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_k1.PNG"));
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_k2.PNG"));
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_k3.PNG"));
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_k4.PNG"));
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_k5.PNG"));
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_m1.PNG"));
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_m2.PNG"));
        Files.deleteIfExists(Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted_meta.txt"));
    }

    private void encryptFile() throws Exception {
        Path originalPath = Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098.PNG");
        Path encryptPath = Paths.get("/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098_encrypted.PNG");
        AesUtil.encrypt("ZhouZhiXuan520011", originalPath, encryptPath);
    }

    private void decryptFile() throws Exception {
        Path originalPath = Paths.get("/Users/zhouzhixuan/Desktop/test0/temp/IMG_0098.PNG");
        Path decryptPath = Paths.get("/Users/zhouzhixuan/Desktop/test0/temp/IMG_0098_decrypted.PNG");
        AesUtil.decrypt("ZhouZhiXuan520011", originalPath, decryptPath);
    }
}
