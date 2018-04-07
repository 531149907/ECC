package com.ecc.domain.security;

import lombok.*;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyStorage implements Serializable {
    PublicKey publicKey;
    PrivateKey privateKey;
}
