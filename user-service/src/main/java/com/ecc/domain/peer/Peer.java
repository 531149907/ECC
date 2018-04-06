package com.ecc.domain.peer;

import com.ecc.util.converter.OutputFormatter;
import com.ecc.util.crypto.RsaUtil;
import lombok.*;

import java.io.Serializable;
import java.security.PublicKey;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Peer implements Serializable {
    private Integer id;
    private String email;
    private String publicKey;
    private String regDate;
    private String ip;
    private Integer port;
    private String dir;
    private String level;
    private String channel;

    public PublicKey getPublicKey(String publicKey) {
        return RsaUtil.getPublicKeyFromString(publicKey);
    }

    public void show() {
        System.out.println("==============================================================================");
        System.out.println("PEER STRUCTURE");
        System.out.println("------------------------------------------------------------------------------");
        System.out.println("        Id:\t" + OutputFormatter.format(id, 15));
        System.out.println("     Email:\t" + OutputFormatter.format(email, 15));
        System.out.println("Public key:\t" + OutputFormatter.format(publicKey, 15));
        System.out.println("  Reg date:\t" + OutputFormatter.format(regDate, 15));
        System.out.println("        Ip:\t" + OutputFormatter.format(ip, 15));
        System.out.println("      Port:\t" + OutputFormatter.format(port, 15));
        System.out.println("       Dir:\t" + OutputFormatter.format(dir, 15));
        System.out.println("     Level:\t" + OutputFormatter.format(level, 15));
        System.out.println("   Channel:\t" + OutputFormatter.format(channel, 15));
        System.out.println("==============================================================================");
        System.out.println("\n\n");
    }
}
