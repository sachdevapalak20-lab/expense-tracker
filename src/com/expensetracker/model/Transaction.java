package com.expensetracker.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Transaction - Model class representing an income or expense entry.
 */
public class Transaction {

    public enum Type { INCOME, EXPENSE }

    private int id;
    private int userId;
    private int categoryId;
    private String categoryName;   // joined from categories table
    private String title;
    private double amount;
    private Type type;
    private LocalDate date;
    private String description;
    private LocalDateTime createdAt;

    // ── Constructors ───────────────────────────────────────────
    public Transaction() {}

    public Transaction(int userId, int categoryId, String title,
                       double amount, Type type, LocalDate date, String description) {
        this.userId      = userId;
        this.categoryId  = categoryId;
        this.title       = title;
        this.amount      = amount;
        this.type        = type;
        this.date        = date;
        this.description = description;
    }

    // ── Getters & Setters ──────────────────────────────────────
    public int getId()                           { return id; }
    public void setId(int id)                    { this.id = id; }

    public int getUserId()                       { return userId; }
    public void setUserId(int userId)            { this.userId = userId; }

    public int getCategoryId()                   { return categoryId; }
    public void setCategoryId(int categoryId)    { this.categoryId = categoryId; }

    public String getCategoryName()              { return categoryName; }
    public void setCategoryName(String n)        { this.categoryName = n; }

    public String getTitle()                     { return title; }
    public void setTitle(String title)           { this.title = title; }

    public double getAmount()                    { return amount; }
    public void setAmount(double amount)         { this.amount = amount; }

    public Type getType()                        { return type; }
    public void setType(Type type)               { this.type = type; }

    public LocalDate getDate()                   { return date; }
    public void setDate(LocalDate date)          { this.date = date; }

    public String getDescription()               { return description; }
    public void setDescription(String desc)      { this.description = desc; }

    public LocalDateTime getCreatedAt()                  { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt)    { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", title='" + title + "', amount=" + amount +
               ", type=" + type + ", date=" + date + "}";
    }
}