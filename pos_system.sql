-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS java_user_database;
USE java_user_database;

-- Products table
CREATE TABLE IF NOT EXISTS products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    category VARCHAR(50),
    barcode VARCHAR(50) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL UNIQUE
);

-- Sales table
CREATE TABLE IF NOT EXISTS sales (
    sale_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20),
    sale_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- Sale items table
CREATE TABLE IF NOT EXISTS sale_items (
    sale_item_id INT PRIMARY KEY AUTO_INCREMENT,
    sale_id INT,
    product_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Inventory transactions table
CREATE TABLE IF NOT EXISTS inventory_transactions (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    transaction_type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Insert Categories
INSERT INTO categories (category_name) VALUES
('Chinese Favorites'),
('Rice Meals'),
('Noodles'),
('Dimsum'),
('Beverages'),
('Desserts'),
('Lauriat'),
('Breakfast'),
('Value Meals'),
('Add-ons');

-- Insert Products (Chowking Philippines Menu)
INSERT INTO products (product_name, description, price, stock_quantity, category, barcode) VALUES
-- Chinese Favorites
('Chinese-Style Fried Chicken', 'Crispy fried chicken marinated in Chinese spices', 99.00, 100, 'Chinese Favorites', 'CFC001'),
('Sweet and Sour Pork', 'Crispy pork with sweet and sour sauce', 119.00, 100, 'Chinese Favorites', 'SSP001'),
('Salt and Pepper Spareribs', 'Crispy spareribs with salt and pepper seasoning', 129.00, 100, 'Chinese Favorites', 'SPS001'),
('Yang Chow Fried Rice', 'Special fried rice with Chinese sausage and vegetables', 89.00, 100, 'Chinese Favorites', 'YCF001'),

-- Rice Meals
('Pork Chao Fan', 'Chinese-style fried rice with pork', 89.00, 100, 'Rice Meals', 'PCF001'),
('Beef Chao Fan', 'Chinese-style fried rice with beef', 99.00, 100, 'Rice Meals', 'BCF001'),
('Chinese Sausage Chao Fan', 'Fried rice with Chinese sausage', 89.00, 100, 'Rice Meals', 'CSF001'),

-- Noodles
('Beef Wonton Mami', 'Noodle soup with beef and wontons', 109.00, 100, 'Noodles', 'BWM001'),
('Pork Wonton Mami', 'Noodle soup with pork and wontons', 99.00, 100, 'Noodles', 'PWM001'),
('Chicken Mami', 'Chicken noodle soup', 89.00, 100, 'Noodles', 'CHM001'),
('Pancit Canton', 'Stir-fried noodles with vegetables and meat', 99.00, 100, 'Noodles', 'PCN001'),

-- Dimsum
('Siomai', 'Steamed pork dumplings', 49.00, 100, 'Dimsum', 'SIO001'),
('Beef Siomai', 'Steamed beef dumplings', 55.00, 100, 'Dimsum', 'BSI001'),
('Chicken Siomai', 'Steamed chicken dumplings', 49.00, 100, 'Dimsum', 'CSI001'),
('Pork Buchi', 'Sweet rice balls with pork filling', 39.00, 100, 'Dimsum', 'PBU001'),

-- Beverages
('Chinese Tea', 'Traditional Chinese tea', 35.00, 100, 'Beverages', 'CTE001'),
('Sago Gulaman', 'Sweet drink with tapioca pearls and gelatin', 39.00, 100, 'Beverages', 'SGU001'),
('Coca-Cola', 'Classic Coke', 35.00, 100, 'Beverages', 'COK001'),
('Sprite', 'Lemon-lime soda', 35.00, 100, 'Beverages', 'SPR001'),

-- Desserts
('Buchi', 'Sweet rice balls with monggo filling', 39.00, 100, 'Desserts', 'BUC001'),
('Halo-Halo', 'Mixed dessert with shaved ice and sweet beans', 79.00, 100, 'Desserts', 'HAL001'),
('Nai Huang Bao', 'Custard bun', 45.00, 100, 'Desserts', 'NHB001'),

-- Lauriat
('Lauriat A', 'Chinese-style fried chicken, pancit canton, siomai, chicharap, buchi, drink', 199.00, 100, 'Lauriat', 'LAU001'),
('Lauriat B', 'Sweet and sour pork, yang chow fried rice, siomai, chicharap, buchi, drink', 219.00, 100, 'Lauriat', 'LAU002'),
('Lauriat C', 'Salt and pepper spareribs, pancit canton, siomai, chicharap, buchi, drink', 229.00, 100, 'Lauriat', 'LAU003'),

-- Breakfast
('Beef Tapsilog', 'Beef tapa with fried rice and egg', 119.00, 100, 'Breakfast', 'BTS001'),
('Tocilog', 'Tocino with fried rice and egg', 109.00, 100, 'Breakfast', 'TOC001'),
('Longsilog', 'Longganisa with fried rice and egg', 109.00, 100, 'Breakfast', 'LNG001'),

-- Value Meals
('Chinese-Style Chicken Value Meal', 'Fried chicken with rice and drink', 99.00, 100, 'Value Meals', 'CVM001'),
('Siomai Value Meal', '4pcs siomai with rice and drink', 89.00, 100, 'Value Meals', 'SVM001'),
('Sweet and Sour Pork Value Meal', 'Sweet and sour pork with rice and drink', 109.00, 100, 'Value Meals', 'PVM001'),

-- Add-ons
('Extra Rice', 'Plain steamed rice', 25.00, 100, 'Add-ons', 'RIC001'),
('Extra Siomai', 'Single piece of siomai', 15.00, 100, 'Add-ons', 'SIO002'),
('Chicharap', 'Crispy crackers', 20.00, 100, 'Add-ons', 'CHI001'),
('Extra Sauce', 'Choice of chili, soy, or sweet and sour sauce', 10.00, 100, 'Add-ons', 'SAU001'); 