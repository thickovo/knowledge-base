package com.gao.knowledgebase.exception;

import com.gao.knowledgebase.common.Result;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        return Result.error("系统异常，稍后重试");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validException(MethodArgumentNotValidException e) {
        System.out.println("校验异常被捕获了");
        e.printStackTrace();
        String bindingResult = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        return Result.error(bindingResult);
    }
}
