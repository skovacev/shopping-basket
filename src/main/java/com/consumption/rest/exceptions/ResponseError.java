/**
 * 
 */
package com.consumption.rest.exceptions;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.springframework.http.HttpStatus;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
@ApiObject(name = "ResponseError", description = "HTTP error response data.")
public class ResponseError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @ApiObjectField(name = "errorMessage", description = "Error message", required = true, order = 1)
    private String errorMessage;

    @ApiObjectField(name = "httpStatus", description = "HTTP status code", required = true, order = 1)
    private int httpStatusCode = HttpStatus.BAD_REQUEST.value();

    @ApiObjectField(name = "httpStatusReason", description = "HTTP status reason", required = true, order = 1)
    private String httpStatusReason = HttpStatus.BAD_REQUEST.getReasonPhrase();

    
    /**
     * Default Constructor
     *
     */
    public ResponseError() {
    }
    
    /**
     * Constructor for handling error response.
     *
     * @param errorMessage error message.
     */
    public ResponseError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.httpStatusCode = HttpStatus.NOT_FOUND.value();
        this.httpStatusReason = HttpStatus.NOT_FOUND.getReasonPhrase();
    }

    /**
     * Constructor for handling error response.
     *
     * @param errorMessage error message.
     * @param httpStatus {@link HttpStatus} status.
     */
    public ResponseError(String errorMessage, HttpStatus httpStatus) {
        this.errorMessage = errorMessage;
        this.httpStatusCode = httpStatus.value();
        this.httpStatusReason = httpStatus.getReasonPhrase();
    }

    /**
     * Get error message.
     * 
     * @return message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Get httpStatus.
     *
     * @return the httpStatus
     */
    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(httpStatusCode);
    }
}
