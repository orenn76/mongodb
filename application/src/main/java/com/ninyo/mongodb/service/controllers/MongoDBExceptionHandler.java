package com.ninyo.mongodb.service.controllers;

import com.ninyo.common.rest.exception.RestResponseEntityExceptionHandler;
import com.ninyo.mongodb.service.exceptions.HierarchyManagerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class MongoDBExceptionHandler extends RestResponseEntityExceptionHandler {

    @ExceptionHandler({HierarchyManagerException.class})
    public ResponseEntity handleHierarchyManagerException(Exception ex, HttpServletRequest req) {
        return handleException(ex, req, HttpStatus.BAD_REQUEST);
    }
}
