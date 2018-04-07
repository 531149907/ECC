package com.ecc.domain.transaction.impl;

import com.ecc.domain.common.Hashable;
import com.ecc.domain.transaction.Transaction;
import com.ecc.util.converter.OutputFormatter;
import com.ecc.util.crypto.HashUtil;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileTransaction extends Transaction implements Serializable, Hashable {
    private String id;
    private String originalFileName;
    private String hashedFileName;
    private String fileHash;
    private String shardId;
    private String shardHash;
    private String owner;
    private String holder;
    private String timestamp;
    private String fileLevel;

    public String hash() {
        return HashUtil.hash(getRawMessage());
    }

    public String getRawMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(id)
                .append(originalFileName)
                .append(hashedFileName)
                .append(fileHash)
                .append(shardId)
                .append(shardHash)
                .append(owner)
                .append(holder)
                .append(timestamp)
                .append(fileLevel);
        return builder.toString();
    }

    public void show() {
        System.out.println("========================================================================================");
        System.out.println("TRANSACTION STRUCTURE - [ FILE_TRANSACTION ]");
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("                Id:\t" + OutputFormatter.format(id, 23));
        System.out.println("Original file name:\t" + OutputFormatter.format(originalFileName, 23));
        System.out.println("  Hashed file name:\t" + OutputFormatter.format(hashedFileName, 23));
        System.out.println("         File hash:\t" + OutputFormatter.format(fileHash, 23));
        System.out.println("          Shard id:\t" + OutputFormatter.format(shardId, 23));
        System.out.println("        Shard hash:\t" + OutputFormatter.format(shardHash, 23));
        System.out.println("             Owner:\t" + OutputFormatter.format(owner, 23));
        System.out.println("            Holder:\t" + OutputFormatter.format(holder, 23));
        System.out.println("         Timestamp:\t" + OutputFormatter.format(timestamp, 23));
        System.out.println("        File level:\t" + OutputFormatter.format(fileLevel, 23));
        System.out.println("========================================================================================");
        System.out.println("\n\n");
    }
}
