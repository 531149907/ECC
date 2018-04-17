package com.ecc.web.exceptions;

import com.ecc.exceptions.CustomException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

public class CustomExceptionHandler {
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Map<String, Object> handleCustomException(CustomException e) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("errorMsg", e.getExceptionCollection().getMessage());
        resultMap.put("code", e.getExceptionCollection().getCode());
        return resultMap;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleException(Exception e) {
        return e.getMessage();
    }

}
