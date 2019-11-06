package com.quim.tfm.similarity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason = "Entity not found")
public class NotFoundCustomException extends RuntimeException {
}
