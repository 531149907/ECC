package com.ecc.domain.peer;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Verification implements Serializable {
    private Integer id;
    private String email;
    private String code;
    private String password;
}
