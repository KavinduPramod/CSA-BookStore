/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.resource;

import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import com.bookstore.response.ApiResponse;
import java.util.ArrayList;
import java.util.List;
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
@Path("/books")
public class BookResource {

    private static final Logger logger = LoggerFactory.getLogger(BookResource.class);
    private static final List<Book> books = new ArrayList<>();
    private static int nextId = 0;

    static {
        List<Author> authors = AuthorResource.getAllAuthorsStatic();
        books.add(new Book(nextId++, "Harry Potter", authors.get(0).getId(), "123-456-789-10112", 1997, 1000, 20));
        books.add(new Book(nextId++, "Song Of Ice And Fire", authors.get(1).getId(), "123-456-789-10112", 1996, 1500, 30));
    }

    static List<Book> getAllBooksStatic() {
        return books;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Book> getAllBooks() {
        logger.info("GET request for all books");
        return books;
    }

    @GET
    @Path("/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Book getBookById(@PathParam("bookId") int bookId) {
        logger.info("GET request for book with ID: {}", bookId);
        return books.stream()
                .filter(book -> book.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("book with ID " + bookId + " not found"));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addStudent(Book book) {
        validateBook(book);
        book.setId(nextId++);
        books.add(book);
        logger.info("Added new book with ID: {}", book.getId());
        return Response.status(Response.Status.CREATED)
                .entity(book)
                .build();
    }

    @PUT
    @Path("/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("bookId") int bookId, Book updatedBook) {
        validateBook(updatedBook);
        logger.info("PUT request to update book with ID: {}", bookId);
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId() == bookId) {
                updatedBook.setId(bookId);
                books.set(i, updatedBook);
                logger.info("Updated book with ID: {}", bookId);
                return Response.ok(updatedBook).build();
            }
        }
        throw new BookNotFoundException("Book with ID " + bookId + " not found for update");
    }

    @DELETE
    @Path("/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBook(@PathParam("bookId") int bookId) {
        logger.info("DELETE request for book with ID: {}", bookId);
        boolean removed = books.removeIf(book -> book.getId() == bookId);
        if (!removed) {
            throw new BookNotFoundException("book with ID " + bookId + " not found for deletion");
        }
        logger.info("Deleted book with ID: {}", bookId);
        ApiResponse response = new ApiResponse("Book deleted successfully with ID: " + bookId);
        return Response.ok(response).build();
    }

    private void validateBook(Book book) {
        if (book == null || book.getTitle() == null || book.getTitle().isEmpty()) {
            throw new InvalidInputException("Book title cannot be null or empty");
        }
        if (book.getAuthor() <= 0) {
            throw new InvalidInputException("Invalid author ID");
        }
        if (book.getPublicationYear() <= 0) {
            throw new InvalidInputException("Invalid publication year");
        }
        if (book.getPrice() < 0) {
            throw new InvalidInputException("Price cannot be negative");
        }
        if (book.getStockQuantity() < 0) {
            throw new InvalidInputException("Stock cannot be negative");
        }
    }
}
