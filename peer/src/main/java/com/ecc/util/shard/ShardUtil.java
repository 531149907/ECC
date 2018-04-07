package com.ecc.util.shard;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static com.ecc.constants.ApplicationConstants.PATH_TEMP;

public class ShardUtil {
    public static final String SPLIT_MODE = "split_mode";
    public static final String RECOVER_MODE = "recover_mode";

    private static final int MAX_SHARDS = 5;
    private static final int CHECK_BLOCK = 2;
    private static final int BIT_WORD_SIZE = 16;
    private static final int PACKET_SIZE = 8;
    private static final int BUFFER_SIZE = 1024;
    private static final String CODING_TECHNIQUE = "reed_sol_van";

    private static final String osPlatform = System.getProperty("os.name");


    public static List<Path> dealWithFile(String mode, String filePath) throws Exception {
        switch (mode) {
            case SPLIT_MODE:
                return split(filePath);
            case RECOVER_MODE:
                return recover(filePath);
            default:
                throw new Exception("Not supported mode!");
        }
    }

    private static List<Path> split(String filePath) throws Exception {
        String fileName = Paths.get(filePath).getFileName().toString();
        String fileDir = Paths.get(filePath).toString().replace(fileName, "");
        if (osPlatform.toLowerCase().contains("mac")) {
            return splitOnMacOS(fileName, fileDir);
        } else if (osPlatform.toLowerCase().contains("windows")) {
            return splitOnWindows(fileName, fileDir);
        } else {
            throw new Exception("Platform not supported!");
        }
    }

    private static List<Path> splitOnMacOS(String fileName, String fileDir) throws Exception {
        String scriptPath = "./peer/src/main/resources/tools/shard/macOS/encoder";
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
        Files.deleteIfExists(Paths.get(fileDir + fileName));

        LocateFileVisitor fileVisitor = new LocateFileVisitor(fileName);
        Files.walkFileTree(Paths.get(PATH_TEMP), fileVisitor);

        if (fileVisitor.getPaths().isEmpty()) {
            throw new Exception("Cannot split file!");
        }
        return fileVisitor.getPaths();
    }

    private static List<Path> splitOnWindows(String fileName, String fileDir) {
        List<Path> paths = new ArrayList<>();

        return paths;
    }

    private static List<Path> recover(String filePath) throws Exception {
        List<Path> paths = new ArrayList<>();

        return paths;
    }

    private static class LocateFileVisitor extends SimpleFileVisitor<Path> {
        private List<Path> paths = new ArrayList<>();
        private String fileName;

        LocateFileVisitor(String fileName) {
            String[] var0 = fileName.split("\\.");
            if (var0.length != 1) {
                this.fileName = fileName.replace("." + var0[var0.length - 1], "");
            } else {
                this.fileName = fileName;
            }
        }

        public List<Path> getPaths() {
            return paths;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (file.getFileName().toString().contains(fileName)) {
                paths.add(file);
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
