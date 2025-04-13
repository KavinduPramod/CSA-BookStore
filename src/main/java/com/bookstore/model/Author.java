/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bookstore.model;

/**
 *
 * @author kavin
 */
public class Author {
    private int id;
    private String name;
    private String bioGraphy;

    public Author() {
    }

    public Author(int id, String name, String bioGraphy) {
        this.id = id;
        this.name = name;
        this.bioGraphy = bioGraphy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBioGraphy() {
        return bioGraphy;
    }

    public void setBioGraphy(String bioGraphy) {
        this.bioGraphy = bioGraphy;
    }
}
