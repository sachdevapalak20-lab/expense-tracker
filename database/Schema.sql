
-- ============================================
-- Expense Tracker Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS expense_tracker;
USE expense_tracker;

-- Users table for Login Module
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,  -- stored as SHA-256 hash
    email VARCHAR(100),
    full_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Expenses/Transactions table
CREATE TABLE IF NOT EXISTS transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT,
    title VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    date DATE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Budgets table
CREATE TABLE IF NOT EXISTS budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    month INT NOT NULL,
    year INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- Insert default categories
INSERT INTO categories (name, type, user_id) VALUES
('Salary', 'INCOME', NULL),
('Freelance', 'INCOME', NULL),
('Business', 'INCOME', NULL),
('Food & Dining', 'EXPENSE', NULL),
('Transportation', 'EXPENSE', NULL),
('Shopping', 'EXPENSE', NULL),
('Entertainment', 'EXPENSE', NULL),
('Healthcare', 'EXPENSE', NULL),
('Utilities', 'EXPENSE', NULL),
('Education', 'EXPENSE', NULL),
('Rent', 'EXPENSE', NULL),
('Other', 'EXPENSE', NULL);

-- Default admin user (password: admin123)
INSERT INTO users (username, password, email, full_name) VALUES
('admin', 'ef92b778bafe771207b3f89f9f7b5e5ab5a0be8ace4892cd5b6b4e7e2a6d9c2c', 'admin@expense.com', 'Administrator');
USE expense_tracker;
SELECT username, password FROM users;
USE expense_tracker;
UPDATE users SET password = 'ef92b778bafe771207b3f89f9f7b5e5ab5a0be8ace4892cd5b6b4e7e2a6d9c2c' WHERE username = 'admin';
USE expense_tracker;
DELETE FROM users WHERE username = 'admin';
INSERT INTO users (username, password, email, full_name) 
VALUES ('admin', 'ef92b778bafe771207b3f89f9f7b5e5ab5a0be8ace4892cd5b6b4e7e2a6d9c2c', 'admin@expense.com', 'Administrator');
SELECT username, password FROM users WHERE username = 'admin';
USE expense_tracker;
UPDATE users 
SET password = '240be518fabd2724ddb6f04eeb1700740abd481032d30cbf2fbb3a90975fb849' 
WHERE username = 'admin';