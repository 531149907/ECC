package com.service;

import lombok.Builder;

@Builder
public class TransactionList {
    private String id;
    private String type;
    private String hash;
    private String level;
    private String owner;
    private String timestamp;
}
