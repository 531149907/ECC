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
public class TicketTransaction extends Transaction implements Serializable, Hashable {
    private String id;
    private String fileId;
    private String signFor;
    private String timestamp;
    private String permissions;
    private String signer;

    @Override
    public String hash() {
        return HashUtil.hash(getRawMessage());
    }

    public String getRawMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(id)
                .append(fileId)
                .append(signFor)
                .append(timestamp)
                .append(permissions)
                .append(signer);
        return builder.toString();
    }

    public void show() {
        System.out.println("========================================================================================");
        System.out.println("TRANSACTION STRUCTURE - [ TICKET_TRANSACTION ]");
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("         Id:\t" + OutputFormatter.format(id, 16));
        System.out.println("  Ticket id:\t" + OutputFormatter.format(fileId, 16));
        System.out.println("   Sign for:\t" + OutputFormatter.format(signFor, 16));
        System.out.println("  Timestamp:\t" + OutputFormatter.format(timestamp, 16));
        System.out.println("Permissions:\t" + OutputFormatter.format(permissions, 16));
        System.out.println("     Signer:\t" + OutputFormatter.format(signer, 16));
        System.out.println("========================================================================================");
        System.out.println("\n\n");
    }
}
