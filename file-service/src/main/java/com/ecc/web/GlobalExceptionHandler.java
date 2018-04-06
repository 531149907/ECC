package com.ecc.web;

import com.ecc.web.exceptions.FileException;
import com.ecc.web.exceptions.UserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    public String handleUserException(HttpServletResponse response, UserException e) {
        response.setStatus(e.getCode());
        return e.getMessage();
    }

    @ExceptionHandler(FileException.class)
    public String handleFileException(HttpServletResponse response, FileException e) {
        response.setStatus(e.getCode());
        return e.getMessage();
    }
}
