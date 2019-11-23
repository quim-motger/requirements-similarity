package com.quim.tfm.similarity.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason = "There was an error with the SVM classifier tool")
public class InternalSVMException extends RuntimeException {
}
