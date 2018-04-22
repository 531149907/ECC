package com.ecc.web;

import com.ecc.web.exceptions.ContractException;
import com.ecc.web.exceptions.FileException;
import com.ecc.web.exceptions.UserException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public String handleUserException(HttpServletResponse response, Exception e) {
        response.setStatus(500);
        return e.getMessage();
    }

    @ExceptionHandler(ContractException.class)
    public String handleContractException(HttpServletResponse response, Exception e) {
        response.setStatus(500);
        return e.getMessage();
    }

    @ExceptionHandler(FileException.class)
    public String handleFileException(HttpServletResponse response, Exception e) {
        response.setStatus(500);
        return e.getMessage();
    }

    @ExceptionHandler(Exception.class)
    public String handleDefaultException(HttpServletResponse response, Exception e) {
        response.setStatus(500);
        return e.getMessage();
    }
}
