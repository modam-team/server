package com.example.modam.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorDefine {

    // Bad Request
    INVALID_ARGUMENT("4000", HttpStatus.BAD_REQUEST, "Bad Request: Invalid Arguments"),
    INVALID_HEADER_ERROR("4006", HttpStatus.BAD_REQUEST, "Bad Request: Invalid Header Error"),

    // Not Found
    USER_NOT_FOUND("4040", HttpStatus.NOT_FOUND, "Not Found: User Not Found"),
    BOOK_NOT_FOUND("4041", HttpStatus.NOT_FOUND, "Not Found: Book Not Found"),
    STATUS_NOT_FOUND("4042",HttpStatus.NOT_FOUND, "Not Found: Book Status Not Found"),

    //Already
    USER_ALREADY_HAS_BOOK("4092",HttpStatus.NOT_FOUND,"Already Has: User Already Has Book."),

    // Forbidden
    UNAUTHORIZED_USER("4030", HttpStatus.FORBIDDEN, "Forbidden: Unauthorized User"),

    // social login
    LOGIN_ACCESS_DENIED("4031", HttpStatus.FORBIDDEN, "Forbidden: Login Access Denied"),
    TOKEN_MALFORMED("4032", HttpStatus.FORBIDDEN, "Forbidden: Token Malformed"),
    TOKEN_TYPE("4033", HttpStatus.FORBIDDEN, "Forbidden: Token Type"),
    TOKEN_EXPIRED("4034", HttpStatus.FORBIDDEN, "Forbidden: Token Expired"),
    TOKEN_UNSUPPORTED("4035", HttpStatus.FORBIDDEN, "Forbidden: Token Unsupported"),
    TOKEN_UNKNOWN("4036", HttpStatus.FORBIDDEN, "Forbidden: Token Unknown"),
    TOKEN_INVALID("4037", HttpStatus.FORBIDDEN, "Forbidden: Token Invalid");


    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorDefine(String errorCode, HttpStatus httpStatus, String message) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}