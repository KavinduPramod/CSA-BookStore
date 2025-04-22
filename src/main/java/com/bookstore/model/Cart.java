/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.bookstore.model;

import com.bookstore.exception.BookNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kavin
 */
public class Cart {
    private int customerId;
    private List<Book> books = new ArrayList<>();
    private Map<Integer, Integer> quantities = new HashMap<>(); // bookId -> quantity

    public Cart() {}

    public Cart(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public List<Book> getBooks() {
        return books;
    }

    public Map<Integer, Integer> getQuantities() {
        return quantities;
    }

    public void addBook(Book book, int quantity) {
        books.removeIf(b -> b.getId() == book.getId());
        books.add(book);
        quantities.put(book.getId(), quantity);
    }

    public void removeBook(int bookId) {
        books.removeIf(b -> b.getId() == bookId);
        quantities.remove(bookId);
    }
    
        public void updateBookQuantity(int bookId, int quantity) {
        if (quantities.containsKey(bookId)) {
            quantities.put(bookId, quantity);
        } else {
            throw new BookNotFoundException("Book with ID " + bookId + " not found in cart");
        }
    }
}
