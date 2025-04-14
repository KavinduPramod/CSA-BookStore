/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kavin
 */
public class Order {
    private int id;
    private int customerId;
    private List<Book> books = new ArrayList<>();
    private Map<Integer, Integer> quantities = new HashMap<>();
    private double total;
    private Date date;

    public Order() {}

    public Order(int id, int customerId, List<Book> books, Map<Integer, Integer> quantities, double total, Date date) {
        this.id = id;
        this.customerId = customerId;
        this.books = books;
        this.quantities = quantities;
        this.total = total;
        this.date = date;
    }

    public int getId() {
        return id;
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

    public double getTotal() {
        return total;
    }

    public Date getDate() {
        return date;
    }
}
