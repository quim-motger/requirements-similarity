package com.quim.tfm.similarity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason = "The kernel is not implemented")
public class NotImplementedKernel extends RuntimeException {
}
