package com.example.modam.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorDefine {

    // Bad Request
    INVALID_ARGUMENT("4000", HttpStatus.BAD_REQUEST, "Bad Request: Invalid Arguments"),
    INVALID_HEADER_ERROR("4006", HttpStatus.BAD_REQUEST, "Bad Request: Invalid Header Error"),
    SELF_FRIEND_REQUEST("400A", HttpStatus.BAD_REQUEST, "Bad Request: Cannot send a friend request to yourself."),

    // Onboarding
    USER_ALREADY_ONBOARDED("4007", HttpStatus.BAD_REQUEST, "Bad Request: User has already completed onboarding"),
    NICKNAME_DUPLICATION("4008", HttpStatus.BAD_REQUEST, "Bad Request: The nickname is already in already in use."),

    // Not Found
    USER_NOT_FOUND("4040", HttpStatus.NOT_FOUND, "Not Found: KaKao User Not Found"),
    BOOK_NOT_FOUND("4041", HttpStatus.NOT_FOUND, "Not Found: Book Not Found"),
    STATUS_NOT_FOUND("4042", HttpStatus.NOT_FOUND, "Not Found: Book Status Not Found"),
    BOOKCASE_NOT_FOUND("4043", HttpStatus.NOT_FOUND, "Not Found: Bookcase Not Found"),
    FRIEND_REQUEST_NOT_FOUND("4044", HttpStatus.NOT_FOUND, "Not Found: Friend request not found."),

    // File/Image Upload
    FILE_IS_EMPTY("4009", HttpStatus.BAD_REQUEST, "Bad Request: Upload file cannot be empty."),
    INVALID_FILE_FORMAT("400A", HttpStatus.BAD_REQUEST, "Bad Request: Invalid file format or type."),

    //Already
    USER_ALREADY_HAS_BOOK("4092", HttpStatus.NOT_FOUND, "Already Has: User Already Has Book."),
    REVIEW_ALREADY_EXISTS("4093", HttpStatus.NOT_FOUND, "Already Has: Review Already Exists."),
    FRIEND_ALREADY_REQUESTED("4094", HttpStatus.CONFLICT, "Conflict: Friend request has already been sent."),
    ALREADY_FRIENDS("4095", HttpStatus.CONFLICT, "Conflict: Users are already friends."),

    // Forbidden
    UNAUTHORIZED_USER("4030", HttpStatus.FORBIDDEN, "Forbidden: Unauthorized User"),
    UNAUTHORIZED_STATUS("4091", HttpStatus.FORBIDDEN, "Forbidden: Unauthorized Book Status"),

    // social login
    LOGIN_ACCESS_DENIED("4031", HttpStatus.FORBIDDEN, "Forbidden: Login Access Denied"),
    TOKEN_MALFORMED("4032", HttpStatus.FORBIDDEN, "Forbidden: Token Malformed"),
    TOKEN_TYPE("4033", HttpStatus.FORBIDDEN, "Forbidden: Token Type"),
    TOKEN_EXPIRED("4034", HttpStatus.FORBIDDEN, "Forbidden: Token Expired"),
    TOKEN_UNSUPPORTED("4035", HttpStatus.FORBIDDEN, "Forbidden: Token Unsupported"),
    TOKEN_UNKNOWN("4036", HttpStatus.FORBIDDEN, "Forbidden: Token Unknown"),
    TOKEN_INVALID("4037", HttpStatus.FORBIDDEN, "Forbidden: Token Invalid"),

    // REVIEW
    EXCEED_MAX_COMMENT_LENGTH("4038", HttpStatus.FORBIDDEN, "Forbidden: Comment Exceed Max Length"),
    EXCEED_MAX_RATING_NUM("4030", HttpStatus.FORBIDDEN, "Forbidden: Rating Exceed Max Number"),
    INVALID_HASHTAG("4039", HttpStatus.FORBIDDEN, "Forbidden: Invalid Hashtag"),

    //Date
    INVALID_DATE("4038", HttpStatus.FORBIDDEN, "Forbidden: Date Invalid"),

    //Redis
    INVALID_ACCESS_TO_REDIS("R401", HttpStatus.NOT_FOUND, "Invalid: Can Not access to Redis");

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorDefine(String errorCode, HttpStatus httpStatus, String message) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}