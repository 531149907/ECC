package com.ecc.domain.broadcast;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BroadcastContent implements Serializable {
    @JsonIgnore
    public static final String ACTION_BLOCK_RECEIVE = "action_block_receive";
    @JsonIgnore
    public static final String ACTION_FILE_PUSH = "action_file_push";
    @JsonIgnore
    public static final String ACTION_CONTRACT_PUSH = "action_contract_push";

    private Integer code;
    private String action;
    private String message;
    private Object data;
}
