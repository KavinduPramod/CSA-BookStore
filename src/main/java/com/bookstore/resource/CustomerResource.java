/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.resource;

import com.bookstore.exception.CustomerNotFoundException;
import com.bookstore.exception.InvalidInputException;
import com.bookstore.model.Customer;
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
@Path("/customers")
public class CustomerResource {

    private static final Logger logger = LoggerFactory.getLogger(CustomerResource.class);
    private static final List<Customer> customers = new ArrayList<>();
    private static int nextId = 0;

    static {
        customers.add(new Customer(nextId++, "John Doe", "john@example.com", "password123"));
        customers.add(new Customer(nextId++, "Jane Smith", "jane@example.com", "securepass"));
    }

    static List<Customer> getAllCustomersStatic() {
        return customers;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Customer> getAllCustomers() {
        logger.info("GET request for all customers");
        return customers;
    }

    @GET
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomerById(@PathParam("customerId") int customerId) {
        logger.info("GET request for customer with ID: {}", customerId);
        return customers.stream()
                .filter(customer -> customer.getId() == customerId)
                .findFirst()
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found"));
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCustomer(Customer customer) {
        validateCustomer(customer);
        customer.setId(nextId++);
        customers.add(customer);
        logger.info("Added new customer with ID: {}", customer.getId());
        return Response.status(Response.Status.CREATED)
                .entity(customer)
                .build();
    }

    @PUT
    @Path("/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("customerId") int customerId, Customer updatedCustomer) {
        validateCustomer(updatedCustomer);
        logger.info("PUT request to update customer with ID: {}", customerId);
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId() == customerId) {
                updatedCustomer.setId(customerId);
                customers.set(i, updatedCustomer);
                logger.info("Updated customer with ID: {}", customerId);
                return Response.ok(updatedCustomer).build();
            }
        }
        throw new CustomerNotFoundException("Customer with ID " + customerId + " not found for update");
    }

    @DELETE
    @Path("/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCustomer(@PathParam("customerId") int customerId) {
        logger.info("DELETE request for customer with ID: {}", customerId);
        boolean removed = customers.removeIf(customer -> customer.getId() == customerId);
        if (!removed) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found for deletion");
        }
        logger.info("Deleted customer with ID: {}", customerId);
        return Response.ok(new ApiResponse("Customer deleted successfully with ID: " + customerId)).build();
    }

    private void validateCustomer(Customer customer) {
        if (customer == null || customer.getName() == null || customer.getName().isEmpty()) {
            throw new InvalidInputException("Customer name cannot be null or empty");
        }
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new InvalidInputException("Customer email cannot be null or empty");
        }
        if (customer.getPassword() == null || customer.getPassword().length() < 6) {
            throw new InvalidInputException("Password must be at least 6 characters");
        }
    }
}
