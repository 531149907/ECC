package com.ecc.domain.report;

import com.ecc.exceptions.ExceptionCollection;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
public class Report implements Serializable {
    private ExceptionCollection collection;
    private Object data;
}
