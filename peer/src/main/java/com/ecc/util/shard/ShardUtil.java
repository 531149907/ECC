package com.ecc.util.shard;

import com.ecc.exceptions.CustomException;
import com.ecc.exceptions.ExceptionCollection;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static com.ecc.constants.ApplicationConstants.PATH_DOWNLOAD;
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


    public static List<Path> dealWithFile(String mode, String filePath) {
        switch (mode) {
            case SPLIT_MODE:
                return split(filePath);
            case RECOVER_MODE:
                return recover(filePath);
        }
        return new ArrayList<>();
    }

    private static List<Path> split(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        String fileDir = Paths.get(filePath).toString().replace(fileName, "");
        if (osPlatform.toLowerCase().contains("mac")) {
            return splitOnMacOS(fileName, fileDir);
        } else if (osPlatform.toLowerCase().contains("windows")) {
            return splitOnWindows(fileName, fileDir);
        } else {
            throw new CustomException(ExceptionCollection.FILE_SPLIT_PLATFORM_NOT_SUPPORT);
        }
    }

    private static List<Path> splitOnMacOS(String fileName, String fileDir) {
        String scriptPath = "/Users/zhouzhixuan/Desktop/ECC/peer/src/main/resources/tools/shard/macOS/encoder";
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

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/chmod", "755", scriptPath);
            Process process = processBuilder.start();
            process.waitFor();

            Process ps = Runtime.getRuntime().exec(command);
            ps.waitFor();

            LocateFileVisitor fileVisitor = new LocateFileVisitor(fileName);
            Files.walkFileTree(Paths.get(PATH_TEMP), fileVisitor);

            if (fileVisitor.getPaths().isEmpty()) {
                throw new CustomException(ExceptionCollection.FILE_SPLIT_ERROR);
            }

            Files.deleteIfExists(Paths.get(fileDir + fileName));

            List<Path> resultPaths = fileVisitor.getPaths();
            resultPaths.remove(Paths.get(fileDir + fileName));
            return resultPaths;

        } catch (Exception e) {
            throw new CustomException(ExceptionCollection.FILE_SPLIT_ERROR);
        }
    }

    private static List<Path> splitOnWindows(String fileName, String fileDir) {
        List<Path> paths = new ArrayList<>();

        return paths;
    }

    private static List<Path> recover(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        String fileDir = Paths.get(filePath).toString().replace(fileName, "");
        if (osPlatform.toLowerCase().contains("mac")) {
            return recoverOnMacOS(fileName, fileDir);
        } else if (osPlatform.toLowerCase().contains("windows")) {
            return recoverOnWindows(fileName, fileDir);
        } else {
            throw new CustomException(ExceptionCollection.FILE_SPLIT_PLATFORM_NOT_SUPPORT);
        }
    }

    private static List<Path> recoverOnMacOS(String fileName, String fileDir) {
        List<Path> paths = new ArrayList<>();

        String scriptPath = "/Users/zhouzhixuan/Desktop/ECC/peer/src/main/resources/tools/shard/macOS/decoder";
        String[] command = new String[]{
                scriptPath,
                fileName,
                fileDir
        };

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/chmod", "755", scriptPath);
            Process process = processBuilder.start();
            process.waitFor();

            Process ps = Runtime.getRuntime().exec(command);
            ps.waitFor();

            LocateFileVisitor fileVisitor = new LocateFileVisitor(fileName);
            Files.walkFileTree(Paths.get(PATH_DOWNLOAD), fileVisitor);

            for (Path path : fileVisitor.getPaths()) {
                Files.deleteIfExists(path);
            }

            paths.add(Paths.get(fileDir + fileName));
            return paths;
        } catch (Exception e) {
            throw new CustomException(ExceptionCollection.FILE_COMBINE_ERROR);
        }
    }

    private static List<Path> recoverOnWindows(String fileName, String fileDir) {
        List<Path> list = new ArrayList<>();
        return list;
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
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.getFileName().toString().contains(fileName)) {
                paths.add(file);
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
