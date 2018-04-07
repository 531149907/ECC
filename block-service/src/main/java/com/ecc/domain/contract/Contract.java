package com.ecc.domain.contract;

import com.ecc.domain.common.Hashable;
import com.ecc.util.converter.OutputFormatter;
import com.ecc.util.crypto.HashUtil;
import lombok.*;

import java.io.Serializable;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contract implements Serializable, Hashable {
    public static final String SENDER_SIGN = "sender_sign";
    public static final String RECEIVER_SIGN = "receiver_sign";

    public static final String VERIFY_SENDER_SIGN = "verify_sender_sign";
    public static final String VERIFY_RECEIVER_SIGN = "verify_receiver_sign";

    private String id;
    private String channel;
    private String transactionType;
    private String transactionId;
    private String transactionHash;
    private String timestamp;
    private String senderSign;
    private String receiverSign;

    @Override
    public String hash() {
        return HashUtil.hash(getRawMessage());
    }

    public String getRawMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(id)
                .append(channel)
                .append(transactionType)
                .append(transactionId)
                .append(transactionHash)
                .append(timestamp);
        return builder.toString();
    }

    public void show() {
        System.out.println("========================================================================================");
        System.out.println("CONTRACT STRUCTURE");
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("              Id:\t" + OutputFormatter.format(id, 21));
        System.out.println("         Channel:\t" + OutputFormatter.format(channel, 21));
        System.out.println("Transaction type:\t" + OutputFormatter.format(transactionType, 21));
        System.out.println("  Transaction id:\t" + OutputFormatter.format(transactionId, 21));
        System.out.println("Transaction hash:\t" + OutputFormatter.format(transactionHash, 21));
        System.out.println("       Timestamp:\t" + OutputFormatter.format(timestamp, 21));
        System.out.println("     Sender sign:\t" + OutputFormatter.format(senderSign, 21));
        System.out.println("   Receiver sign:\t" + OutputFormatter.format(receiverSign, 21));
        System.out.println("==============================================================================");
        System.out.println("\n\n");
    }
}
