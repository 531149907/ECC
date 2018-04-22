package com.domain;

import com.google.gson.Gson;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content implements Serializable {
    private int status;
    private String message;
    private Object data;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
