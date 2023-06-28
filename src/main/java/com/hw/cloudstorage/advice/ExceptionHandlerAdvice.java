package com.hw.cloudstorage.advice;

import com.hw.cloudstorage.exceptions.Error;
import com.hw.cloudstorage.exceptions.UploadFileToFolderException;
import com.hw.cloudstorage.model.enums.ErrorType;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> illegalArgumentExceptionHandler(IllegalArgumentException e) {
        Error error = new Error(e.getMessage(), ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> badCredentialsExceptionHandler(BadCredentialsException e) {
        Error error = new Error(e.getMessage(), ErrorType.LOGIN_CREDENTIALS.getErrorId());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UploadFileToFolderException.class)
    public ResponseEntity<Error> uploadFileToFolderExceptionHandler(UploadFileToFolderException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.UPLOAD_FILE_TO_FOLDER_ERROR.getErrorId()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<Error> sizeLimitExceededExceptionHandler(SizeLimitExceededException e) {
        return new ResponseEntity<>(new Error("File size exceeded", ErrorType.UPLOAD_FILE_SIZE_EXCEEDED.getErrorId()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Error> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        String message = "Invalid argument for parameter "+ e.getName();
        return new ResponseEntity<>(new Error(message, ErrorType.INVALID_ARGUMENTS_DATA.getErrorId()), HttpStatus.BAD_REQUEST);
    }


    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException missingServletRequestParameterException, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error error = new Error(missingServletRequestParameterException.getMessage(), ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<String> errors = new ArrayList<>();
        for (FieldError error : methodArgumentNotValidException.getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }

        Error error = new Error(String.join(",", errors), ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
