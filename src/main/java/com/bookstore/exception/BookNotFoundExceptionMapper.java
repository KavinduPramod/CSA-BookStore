/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.exception;

import com.bookstore.response.ErrorResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kavin
 */
@Provider
public class BookNotFoundExceptionMapper implements ExceptionMapper<BookNotFoundException> {
    private static final Logger logger = LoggerFactory.getLogger(BookNotFoundException.class);
    
    @Override
    public Response toResponse(BookNotFoundException exception) {
        logger.error("Book not found: {}", exception.getMessage(), exception);

        ErrorResponse errorResponse = new ErrorResponse(
            "Book Not Found",
            exception.getMessage()
        );

        return Response.status(Response.Status.NOT_FOUND)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}