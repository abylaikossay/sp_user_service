package kz.smart.plaza.users.controllers;

import kz.smart.plaza.users.models.errors.ErrorResponse;
import kz.smart.plaza.users.models.errors.ServiceException;
import kz.smart.plaza.users.models.responses.success.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {
    protected ResponseEntity<?> buildResponse(Object data, HttpStatus httpStatus){
        return new ResponseEntity<>(data , httpStatus);
    }
    protected ResponseEntity<?> buildResponse(HttpStatus httpStatus){
        return new ResponseEntity<>(httpStatus);
    }

    protected ErrorResponse buildErrorResponse(ServiceException serviceException){
        return ErrorResponse.builder()
                .code(serviceException.getErrorCode())
                .message(serviceException.getMessage())
                .build();
    }

    protected ResponseEntity<?> buildSuccess(String message, Object data) {
        return new ResponseEntity<>(SuccessResponse.builder().message(message)
                .data(data)
                .build(), HttpStatus.OK);
    }

    protected ResponseEntity<?> buildSuccessResponse(Object data){
        return new ResponseEntity<>(data , HttpStatus.OK);
    }}
