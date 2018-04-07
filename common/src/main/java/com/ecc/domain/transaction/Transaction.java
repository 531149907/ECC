package com.ecc.domain.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {
    private String transactionType;

    public void show() {
        System.out.println("========================================================================================");
        System.out.println("TRANSACTION STRUCTURE - [ RAW_TRANSACTION ]");
        System.out.println("----------------------------------------------------------------------------------------");
        System.out.println("Transaction type:\t" + transactionType);
        System.out.println("========================================================================================");
        System.out.println("\n\n");
    }
}
