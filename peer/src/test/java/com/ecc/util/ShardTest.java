package com.ecc.util;

import com.ecc.util.shard.ShardUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

;import java.io.IOException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
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
    public void test() throws Exception {
        String fileDir = "/Users/zhouzhixuan/Desktop/test0/_local/";
        String fileName = "IMG_0098.PNG";
        String scriptPath = "./src/main/resources/tools/shard/macOS/encoder";

        String[] command = new String[]{
                scriptPath,
                fileDir + fileName,
                String.valueOf(MAX_SHARDS),
                String.valueOf(CHECK_BLOCK),
                CODING_TECHNIQUE,
                String.valueOf(BIT_WORD_SIZE),
                String.valueOf(PACKET_SIZE),
                String.valueOf(BUFFER_SIZE),
        };

        ProcessBuilder processBuilder = new ProcessBuilder("/bin/chmod", "755", scriptPath);
        Process process = processBuilder.start();
        process.waitFor();

        Process ps = Runtime.getRuntime().exec(command);
        ps.waitFor();

    }

    @Test
    public void test0() throws Exception {
        ShardUtil.dealWithFile(ShardUtil.RECOVER_MODE,"/Users/zhouzhixuan/Desktop/test0/_local/IMG_0098.PNG");
    }
}
