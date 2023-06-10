package com.hw.cloudstorage.advice;

import com.hw.cloudstorage.exceptions.Error;
import com.hw.cloudstorage.exceptions.UploadFileToFolderException;
import com.hw.cloudstorage.model.enums.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> illegalArgumentException(IllegalArgumentException e) {
        Error error = new Error(e.getMessage(), ErrorType.INVALID_ARGUMENTS_DATA.getErrorId());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> badCredentialsExceptionHandler(BadCredentialsException e) {
        Error error = new Error(e.getMessage(), ErrorType.LOGIN_CREDENTIALS.getErrorId());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UploadFileToFolderException.class)
    public ResponseEntity<Error> sizeLimitExceededException(UploadFileToFolderException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.UPLOAD_FILE_TO_FOLDER_ERROR.getErrorId()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Error> usernameNotFoundException(UsernameNotFoundException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.LOGIN_CREDENTIALS.getErrorId()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : e.getFieldErrors()) {
            errors.add(error.getDefaultMessage());
        }
        Error error = new Error(errors.toString(), ErrorType.LOGIN_EMPTY_LOGIN_PASSWORD.getErrorId());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Error> handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.JWT_AUTHENTICATION_INVALID.getErrorId()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Error> expiredJwtExceptionHandler(ExpiredJwtException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.TOKEN_EXPIRED.getErrorId()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<Error> unsupportedJwtExceptionHandler(UnsupportedJwtException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.UNSUPPORTED_TOKEN.getErrorId()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Error> malformedJwtExceptionHandler(MalformedJwtException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.TOKEN_IS_NOT_JWT.getErrorId()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Error> handleAuthenticationException(Exception e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.JWT_AUTHENTICATION_INVALID.getErrorId()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<Error> sizeLimitExceededException(SizeLimitExceededException e) {
        return new ResponseEntity<>(new Error(e.getMessage(), ErrorType.UPLOAD_FILE_SIZE_EXCEEDED.getErrorId()), HttpStatus.UNAUTHORIZED);
    }

}
