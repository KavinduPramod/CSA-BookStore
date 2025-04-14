/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.resource;

import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.CartNotFoundException;
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
        if (books.size() >= 2) {
            Cart cart = new Cart(1);
            cart.addBook(books.get(0), 1); // 1 copy of book 0
            cart.addBook(books.get(1), 2); // 2 copies of book 1
            cartMap.put(1, cart);
        }
    }

    @POST
    @Path("/items")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItemJson(@PathParam("customerId") int customerId, Map<String, Object> payload) {
        int bookId = (int) payload.get("bookId");
        int quantity = (int) payload.get("quantity");

        Book book = BookResource.getAllBooksStatic().stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (book.getStockQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock");
        }

        Cart cart = cartMap.computeIfAbsent(customerId, Cart::new);
        cart.addBook(book, quantity);
        return Response.ok(cart).build();
    }

    @PUT
    @Path("/items/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateItemJson(@PathParam("customerId") int customerId,
            @PathParam("bookId") int bookId,
            Map<String, Object> payload) {
        int quantity = (int) payload.get("quantity");

        Cart cart = cartMap.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found");
        }

        Book book = cart.getBooks().stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElseThrow(() -> new BookNotFoundException("Book not found in cart"));

        if (book.getStockQuantity() < quantity) {
            throw new OutOfStockException("Not enough stock to update");
        }

        cart.addBook(book, quantity);
        return Response.ok(cart).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCart(@PathParam("customerId") int customerId) {
        Cart cart = cartMap.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found");
        }
        return Response.ok(cart).build();
    }

    @DELETE
    @Path("/items/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeItem(@PathParam("customerId") int customerId,
            @PathParam("bookId") int bookId) {
        Cart cart = cartMap.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found");
        }
        cart.removeBook(bookId);
        
        ApiResponse response = new ApiResponse("Removed the book from the cart with ID: " + bookId);
        
        return Response.ok(response).build();
    }

    public static Cart getCartForCustomer(int customerId) {
        return cartMap.get(customerId);
    }

    public static void clearCart(int customerId) {
        cartMap.remove(customerId);
    }
}
