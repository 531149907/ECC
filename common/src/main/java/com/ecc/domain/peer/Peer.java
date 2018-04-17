package com.ecc.domain.peer;

import com.ecc.util.converter.OutputFormatter;
import com.ecc.util.crypto.RsaUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.security.PublicKey;

@Getter
@Setter
@Builder
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
    private String status;
    private String token;

    @JsonIgnore
    private String secretKey;

    public Peer() {}

    private static class PeerHolder{
        private static final Peer instance = new Peer();
    }

    public static Peer getInstance() {
        return PeerHolder.instance;
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
        System.out.println("    Secret:\t" + OutputFormatter.format(secretKey, 15));
        System.out.println("     Token:\t" + OutputFormatter.format(token, 15));
        System.out.println("==============================================================================");
        System.out.println("\n\n");
    }
}
