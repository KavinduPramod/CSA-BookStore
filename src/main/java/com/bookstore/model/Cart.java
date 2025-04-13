/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.bookstore.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kavin
 */
public class Cart {
    private int id;
    private Customer customer;
    private Map<Book, Integer> items;

    public Cart() {
        this.items = new HashMap<>();
    }

    public Cart(int id, Customer customer) {
        this.id = id;
        this.customer = customer;
        this.items = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Map<Book, Integer> getItems() {
        return items;
    }

    public void setItems(Map<Book, Integer> items) {
        this.items = items;
    }
}