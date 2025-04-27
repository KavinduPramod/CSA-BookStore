/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.resource;

import com.bookstore.exception.AuthorNotFoundException;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.response.ApiResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author kavin
 */
@Path("/authors")
public class AuthorResource {
    private static final Logger logger = LoggerFactory.getLogger(AuthorResource.class);
    private static final List<Author> authors = new ArrayList<>();
    private static int nextId = 1;

    static {
        authors.add(new Author(nextId++, "J.K. Rowling", "British author and philanthropist"));
        authors.add(new Author(nextId++, "George R.R. Martin", "American author"));
    }

    static List<Author> getAllAuthorsStatic() {
        return authors;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Author> getAllAuthors() {
        logger.info("GET request for all authors");
        return authors;
    }

    @GET
    @Path("/{authorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Author getAuthorById(@PathParam("authorId") int authorId) {
        logger.info("GET request for author with ID: {}", authorId);
        return authors.stream()
                      .filter(author -> author.getId() == authorId)
                      .findFirst()
                      .orElseThrow(() -> new AuthorNotFoundException("Author with ID " + authorId + " not found"));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAuthor(Author author) {
        validateAuthor(author);
        author.setId(nextId++);
        authors.add(author);
        logger.info("Added new author with ID: {}", author.getId());
        return Response.status(Response.Status.CREATED)
                       .entity(author)
                       .build();
    }

    @PUT
    @Path("/{authorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAuthor(@PathParam("authorId") int authorId, Author updatedAuthor) {
        validateAuthor(updatedAuthor);
        logger.info("PUT request to update author with ID: {}", authorId);
        for (int i = 0; i < authors.size(); i++) {
            if (authors.get(i).getId() == authorId) {
                updatedAuthor.setId(authorId);
                authors.set(i, updatedAuthor);
                logger.info("Updated author with ID: {}", authorId);
                return Response.ok(updatedAuthor).build();
            }
        }
        throw new AuthorNotFoundException("Author with ID " + authorId + " not found for update");
    }

    @DELETE
    @Path("/{authorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAuthor(@PathParam("authorId") int authorId) {
        logger.info("DELETE request for author with ID: {}", authorId);
        boolean removed = authors.removeIf(author -> author.getId() == authorId);
        if (!removed) {
            throw new AuthorNotFoundException("Author with ID " + authorId + " not found for deletion");
        }
        logger.info("Deleted author with ID: {}", authorId);
        ApiResponse response = new ApiResponse("Author deleted successfully with ID: " + authorId);
        return Response.ok(response).build();
    }

    @GET
    @Path("/{authorId}/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByAuthor(@PathParam("authorId") int authorId) {
        logger.info("GET request for books by author ID: {}", authorId);

        List<Book> books = BookResource.getAllBooksStatic()
                .stream()
                .filter(book -> book.getAuthor() == authorId)
                .collect(Collectors.toList());
        
        if (books.isEmpty()) {
        logger.warn("No books found for author ID: {}", authorId);
        throw new BookNotFoundException("No books found for author ID: " + authorId);
    }
            
        logger.info("Found {} book(s) for author ID: {}", books.size(), authorId);
        return Response.ok(books).build();
    }

    private void validateAuthor(Author author) {
        if (author == null) {
            throw new InvalidInputException("Author cannot be null");
        }
        if (author.getName() == null || author.getName().trim().isEmpty()) {
            throw new InvalidInputException("Author name cannot be null or empty");
        }
        if (author.getBiography() == null || author.getBiography().trim().isEmpty()) {
            throw new InvalidInputException("Author biography cannot be null or empty");
        }
    }
}
