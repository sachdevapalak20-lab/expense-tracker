package com.expensetracker.model;

/**
 * Category - Model class representing an expense/income category.
 */
public class Category {

    public enum Type { INCOME, EXPENSE }

    private int id;
    private String name;
    private Type type;
    private Integer userId; // null = global/default category

    public Category() {}

    public Category(String name, Type type, Integer userId) {
        this.name   = name;
        this.type   = type;
        this.userId = userId;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getName()                  { return name; }
    public void setName(String name)         { this.name = name; }

    public Type getType()                    { return type; }
    public void setType(Type type)           { this.type = type; }

    public Integer getUserId()               { return userId; }
    public void setUserId(Integer userId)    { this.userId = userId; }

    @Override
    public String toString() { return name; } // used by JComboBox
}