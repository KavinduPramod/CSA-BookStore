/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kavin
 */
public class AuthorNotFoundExceptionMapper implements ExceptionMapper<AuthorNotFoundException> {
    private static final Logger logger = LoggerFactory.getLogger(AuthorNotFoundException.class);
    
    @Override
    public Response toResponse(AuthorNotFoundException exception) {
        logger.error("Author not found: ", exception.getMessage(), exception);

        return Response.status(Response.Status.NOT_FOUND)
                      .entity(exception.getMessage())
                      .type(MediaType.TEXT_PLAIN)
                      .build();
    }
}
