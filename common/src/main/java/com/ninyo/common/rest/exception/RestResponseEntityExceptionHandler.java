package com.ninyo.common.rest.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    /* Default handler to any uncaught exception */
    @ExceptionHandler({Exception.class})
    public ResponseEntity handleAnyException(Exception ex, HttpServletRequest req) {
        return handleException(ex, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity handleAccessDenied(Exception ex, HttpServletRequest req) {
        return handleException(ex, req, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UnsupportedOperationException.class})
    public ResponseEntity handleUnsupportedOperation(Exception ex, HttpServletRequest req) {
        return handleException(ex, req, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class, JsonProcessingException.class})
    public ResponseEntity handleConstraintViolation(Exception ex, HttpServletRequest req) {
        return handleException(ex, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity handleNoSuchElementException(Exception ex, HttpServletRequest req) {
        return handleException(ex, req, HttpStatus.NOT_FOUND);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        HttpServletRequest req = ((ServletWebRequest) request).getRequest();
        return handleException(ex, req, status);
    }

    protected ResponseEntity handleException(Exception ex, HttpServletRequest req, HttpStatus status) {
        String uri = req.getRequestURI();
        if (req.getQueryString() != null) {
            uri += '?' + req.getQueryString();
        }
        logger.error(String.format("Request URI: %s, method: %s, raised an exception with HTTP status: %s", uri, req.getMethod(), status), ex);

        ErrorMessage errorMessage = ErrorMessage.builder()
                .timestamp(generateTimestamp())
                .status(status.value())
                .error(status.getReasonPhrase())
                .exception(ex.getClass().getName())
                .message(ex.getMessage())
                .path(uri)
                .build();
        return new ResponseEntity<>(errorMessage, new HttpHeaders(), status);
    }

    private static String generateTimestamp() {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return dateFormatter.format(new Date());
    }
}
