package com.quim.tfm.similarity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason = "Instance empty or null")
public class BadRequestCustomException extends RuntimeException {
}
