CREATE TABLE IF NOT EXISTS users (
                                     id INT PRIMARY KEY AUTO_INCREMENT,
                                     name VARCHAR(100) NOT NULL,
                                     email VARCHAR(150) NOT NULL UNIQUE,
                                     password_hash VARCHAR(255) NOT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB AUTO_INCREMENT = 1001;


CREATE TABLE IF NOT EXISTS products (
                                        id INT PRIMARY KEY AUTO_INCREMENT,
                                        name VARCHAR(100) NOT NULL,
                                        description TEXT,
                                        price DECIMAL(10, 2) NOT NULL,
                                        stock_qty INT NOT NULL DEFAULT 0,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        CONSTRAINT chk_stock CHECK (stock_qty >= 0),
                                        CONSTRAINT chk_price CHECK (price >= 0.00)
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS orders (
                                      id INT PRIMARY KEY AUTO_INCREMENT,
                                      user_id INT NOT NULL,
                                      total_amount DECIMAL(10, 2) NOT NULL,
                                      order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT,
                                      CONSTRAINT chk_total CHECK (total_amount >= 0.00)
) ENGINE = InnoDB;


CREATE TABLE IF NOT EXISTS order_items (
                                           id INT PRIMARY KEY AUTO_INCREMENT,
                                           order_id INT NOT NULL,
                                           product_id INT NOT NULL,
                                           quantity INT NOT NULL DEFAULT 1,
                                           unit_price DECIMAL(10, 2) NOT NULL,
                                           FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                                           FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT,
                                           CONSTRAINT chk_quantity CHECK (quantity > 0)
) ENGINE = InnoDB;


CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_items_order ON order_items(order_id);
