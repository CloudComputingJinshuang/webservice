package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UPGRADE_REQUIRED)
public class NotVerifyException extends RuntimeException {
}
