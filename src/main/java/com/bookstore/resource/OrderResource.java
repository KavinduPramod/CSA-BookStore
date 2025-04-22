/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.resource;

import com.bookstore.exception.InvalidInputException;
import com.bookstore.exception.OutOfStockException;
import com.bookstore.model.Book;
import com.bookstore.model.Cart;
import com.bookstore.model.Order;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
@Path("/customers/{customerId}/orders")
public class OrderResource {

    private static final Logger logger = LoggerFactory.getLogger(OrderResource.class);
    private static final Map<Integer, List<Order>> ordersByCustomer = new HashMap<>();
    private static int nextOrderId = 1;

    static {
        List<Book> books = BookResource.getAllBooksStatic();
        Book book1 = books.get(0);
        Book book2 = books.get(1);

        Map<Integer, Integer> quantities = new HashMap<>();
        quantities.put(book1.getId(), 1);
        quantities.put(book2.getId(), 2);

        double total = book1.getPrice() + 2 * book2.getPrice();
        List<Book> orderedBooks = List.of(book1, book2);

        Order initialOrder = new Order(
                nextOrderId++,
                1,
                new ArrayList<>(orderedBooks),
                quantities,
                total,
                new Date()
        );

        ordersByCustomer.put(2, new ArrayList<>(List.of(initialOrder)));

        // Update stock
        book1.setStockQuantity(book1.getStockQuantity() - 1);
        book2.setStockQuantity(book2.getStockQuantity() - 2);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response placeOrder(@PathParam("customerId") int customerId) {
        logger.info("POST request to place order for customer ID: {}", customerId);

        Cart cart = CartResource.getCartForCustomer(customerId);
        if (cart == null || cart.getBooks().isEmpty()) {
            logger.warn("Attempted to place order with empty cart for customer ID: {}", customerId);
            throw new InvalidInputException("Cart is empty for customer ID: " + customerId);
        }

        double total = 0;
        Map<Integer, Integer> cartQuantities = cart.getQuantities();
        List<Book> booksToOrder = new ArrayList<>();

        for (Book book : cart.getBooks()) {
            int quantity = cartQuantities.get(book.getId());
            if (book.getStockQuantity() < quantity) {
                logger.warn("Book '{}' is out of stock for requested quantity: {}", book.getTitle(), quantity);
                throw new OutOfStockException("Book '" + book.getTitle() + "' is out of stock");
            }
            total += quantity * book.getPrice();
            booksToOrder.add(book);
        }

        // Update stock after validation
        for (Book book : booksToOrder) {
            int quantity = cartQuantities.get(book.getId());
            book.setStockQuantity(book.getStockQuantity() - quantity);
        }

        Order order = new Order(
                nextOrderId++,
                customerId,
                new ArrayList<>(booksToOrder),
                new HashMap<>(cartQuantities),
                total,
                new Date()
        );

        ordersByCustomer.computeIfAbsent(customerId, k -> new ArrayList<>()).add(order);
        CartResource.clearCart(customerId);

        logger.info("Order placed successfully with ID: {} for customer ID: {}", order.getId(), customerId);
        return Response.status(Response.Status.CREATED).entity(order).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrders(@PathParam("customerId") int customerId) {
        logger.info("GET request for all orders of customer ID: {}", customerId);
        List<Order> orders = ordersByCustomer.get(customerId);
        if (orders == null || orders.isEmpty()) {
            logger.warn("No orders found for customer ID: {}", customerId);
            throw new OutOfStockException("No orders found for customer ID: " + customerId);
        }
        return Response.ok(orders).build();
    }

    @GET
    @Path("/{orderId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("customerId") int customerId,
            @PathParam("orderId") int orderId) {
        logger.info("GET request for order ID: {} of customer ID: {}", orderId, customerId);

        List<Order> customerOrders = ordersByCustomer.getOrDefault(customerId, new ArrayList<>());
        return customerOrders.stream()
                .filter(o -> o.getId() == orderId)
                .findFirst()
                .map(Response::ok)
                .orElseThrow(() -> {
                    logger.warn("Order ID: {} not found for customer ID: {}", orderId, customerId);
                    return new InvalidInputException("Order with ID " + orderId + " not found for customer ID: " + customerId);
                })
                .build();
    }
}
