/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.resource;

import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.CartNotFoundException;
import com.bookstore.exception.InvalidInputException;
import com.bookstore.exception.OutOfStockException;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.response.ApiResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
@Path("/customers/{customerId}/cart")
public class CartResource {

    private static final Logger logger = LoggerFactory.getLogger(CartResource.class);
    private static final Map<Integer, Cart> cartMap = new HashMap<>();

    static {
        List<Book> books = BookResource.getAllBooksStatic();
            Cart cart = new Cart(1);
            cart.addBook(books.get(0), 1); // 1 copy of book 0
            cart.addBook(books.get(1), 2); // 2 copies of book 1
            cartMap.put(0, cart);
            cartMap.put(1, cart);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@PathParam("customerId") int customerId) {
        logger.info("GET request for cart of customer ID: {}", customerId);
        Cart cart = cartMap.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found for customer ID: " + customerId);
        }
        return Response.ok(cart).build();
    }

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItemJson(@PathParam("customerId") int customerId, Map<String, Object> payload) {
        logger.info("POST request to add item to cart for customer ID: {}", customerId);

        int bookId = getIntValue(payload, "bookId");
        int quantity = getIntValue(payload, "quantity");

        Book book = BookResource.getAllBooksStatic().stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found"));

        if (book.getStockQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock for book ID: " + bookId);
        }

        Cart cart = cartMap.computeIfAbsent(customerId, Cart::new);
        cart.addBook(book, quantity);

        logger.info("Added book ID {} x{} to cart for customer ID {}", bookId, quantity, customerId);
        return Response.ok(cart).build();
    }

    @PUT
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItemJson(@PathParam("customerId") int customerId,
                                   @PathParam("bookId") int bookId,
                                   Map<String, Object> payload) {
        logger.info("PUT request to update book ID {} in cart for customer ID: {}", bookId, customerId);
        int quantity = getIntValue(payload, "quantity");

        Cart cart = cartMap.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found for customer ID: " + customerId);
        }

        Book book = cart.getBooks().stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book with ID " + bookId + " not found in cart"));

        if (book.getStockQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock to update book ID: " + bookId);
        }

        cart.updateBookQuantity(bookId, quantity);
        logger.info("Updated book ID {} to quantity {} in cart for customer ID: {}", bookId, quantity, customerId);
        return Response.ok(cart).build();
    }

    @DELETE
    @Path("/items/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeItem(@PathParam("customerId") int customerId,
                               @PathParam("bookId") int bookId) {
        logger.info("DELETE request to remove book ID {} from cart of customer ID: {}", bookId, customerId);
        Cart cart = cartMap.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found for customer ID: " + customerId);
        }

        cart.removeBook(bookId); // silently does nothing if book not in cart
        return Response.ok(new ApiResponse("Removed the book from the cart with ID: " + bookId)).build();
    }

    public static Cart getCartForCustomer(int customerId) {
        return cartMap.get(customerId);
    }

    public static void clearCart(int customerId) {
        cartMap.remove(customerId);
    }

    // Helper to validate and safely extract integers from JSON payloads
    private int getIntValue(Map<String, Object> payload, String key) {
        if (!payload.containsKey(key)) {
            throw new InvalidInputException("Missing required field: " + key);
        }

        Object value = payload.get(key);
        if (!(value instanceof Number)) {
            throw new InvalidInputException("Field '" + key + "' must be a number");
        }

        return ((Number) value).intValue();
    }
}
