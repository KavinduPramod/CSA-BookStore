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
public class InvalidInputExceptionMapper implements ExceptionMapper<InvalidInputException> {

    private static final Logger logger = LoggerFactory.getLogger(InvalidInputException.class);

    @Override
    public Response toResponse(InvalidInputException exception) {
        logger.error("Invalid Input: {}", exception.getMessage(), exception);

        ErrorResponse errorResponse = new ErrorResponse(
                "Invalid Input",
                exception.getMessage()
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
