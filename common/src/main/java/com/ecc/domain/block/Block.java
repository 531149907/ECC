package com.ecc.domain.block;

import com.ecc.domain.common.Hashable;
import com.ecc.domain.contract.Contract;
import com.ecc.util.converter.OutputFormatter;
import com.ecc.util.crypto.HashUtil;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Block implements Serializable, Hashable {
    private Integer index;
    private String timestamp;
    private String prevHash;
    private String merkleTreeRoot;
    private List<Contract> contracts;

    public String hash() {
        StringBuilder builder = new StringBuilder();
        builder.append(index)
                .append(timestamp)
                .append(prevHash)
                .append(merkleTreeRoot);
        if (contracts != null && contracts.size() != 0) {
            for (Contract contract : contracts) {
                builder.append(contract.hash());
            }
        }
        return HashUtil.hash(builder.toString());
    }

    public void show() {
        System.out.println("BLOCK STRUCTURE");
        System.out.println("==============================================================================");
        System.out.println("Block index: " + index + "\t\t\t Timestamp: " + timestamp);
        System.out.println("Prev hash: " + OutputFormatter.format(prevHash, 11));
        System.out.println("Tree root: " + OutputFormatter.format(merkleTreeRoot, 11));
        if (contracts != null && contracts.size() != 0) {
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("ID\t\tTIMESTAMP\t\tCHANNEL\t\tTRANS_TYPE\t\tTRANS_ID\t\tTRANS_HASH");
            for (Contract contract : contracts) {
                System.out.println(contract.getId() + "\t\t" + contract.getTimestamp() + "\t\t" + contract.getChannel() + "\t\t" + contract.getTransactionType() + "\t" + contract.getTransactionId() + "\t\t" + contract.getTransactionHash());
            }
            System.out.println("------------------------------------------------------------------------------");
            System.out.println("Contract(s): " + contracts.size());
        }
        System.out.println("==============================================================================");
        System.out.println("\n\n");
    }
}
