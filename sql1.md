-- =========================================================
-- 🦊 BookFox — Bookstore Database Schema
-- Summer Training Project · Helwan Engineering Team
-- =========================================================

CREATE DATABASE IF NOT EXISTS bookfox
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bookfox;

-- =========================================================
-- 👤 المستخدمين (Admin + عملاء المتجر)
-- =========================================================
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE,
    phone VARCHAR(20),
    governorate VARCHAR(100),

    password VARCHAR(255) NOT NULL,
    token VARCHAR(255),
    role ENUM('admin','user') DEFAULT 'user',

    status TINYINT(1) DEFAULT 1,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_status (status)
);

-- =========================================================
-- ✍️ المؤلفون و دور النشر
-- =========================================================
CREATE TABLE authors (
    id INT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(150) NOT NULL,
    type ENUM('author','publisher') DEFAULT 'author',
    country VARCHAR(100),
    bio TEXT,
    joined VARCHAR(20),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_name (name)
);

-- =========================================================
-- 📚 المنتجات (كتب + أدوات مدرسية ومكتبية)
-- kind = book | supply
-- =========================================================
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,

    kind ENUM('book','supply') DEFAULT 'book',
    title VARCHAR(200) NOT NULL,
    author_id INT,
    category VARCHAR(100),
    price DECIMAL(10,2) NOT NULL,
    cover VARCHAR(500),
    description TEXT,

    quantity INT DEFAULT 0,
    min_stock INT DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE SET NULL,

    INDEX idx_kind (kind),
    INDEX idx_category (category),
    INDEX idx_author (author_id),
    INDEX idx_quantity (quantity)
);

-- =========================================================
-- 🧾 الطلبات
-- =========================================================
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,

    order_code VARCHAR(30) UNIQUE,

    user_id INT,
    customer VARCHAR(100) NOT NULL,
    email VARCHAR(150),

    subtotal DECIMAL(10,2) DEFAULT 0,
    shipping DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) DEFAULT 0,

    status ENUM('pending','processing','shipped','delivered','cancelled') DEFAULT 'pending',

    governorate VARCHAR(100),
    city VARCHAR(100),
    address TEXT,
    notes TEXT,
    payment VARCHAR(50),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,

    INDEX idx_user (user_id),
    INDEX idx_status (status),
    INDEX idx_order_code (order_code)
);

-- =========================================================
-- 🧾 بنود الطلب (عناصر كل طلب)
-- =========================================================
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,

    order_id INT NOT NULL,
    product_id INT,

    title VARCHAR(200) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    kind ENUM('book','supply') DEFAULT 'book',

    total DECIMAL(10,2),

    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE SET NULL,

    INDEX idx_order (order_id),
    INDEX idx_product (product_id)
);

-- =========================================================
-- 🧪 البيانات الأولية (Seed Data)
-- =========================================================

-- حسابات الدخول
INSERT INTO users (name, email, phone, governorate, password, role) VALUES
('Mahmoud Essam',  'admin@gmail.com',       '01000000000', 'Cairo',       '$2a$10$PKRk74WrCF0l9QIMvnBQ9OK1lBfBnTNwRAgUky7Lx3ICMEjn6gh7W', 'admin'), -- admin123
('Youssef Farag',  'user@gmail.com',        '01111111111', 'Giza',        '$2a$10$84xesuVpsfWV2jGrvNcDtO6iOGPCYpDAOSz06tzAIdVC8yNm7k3OK', 'user'),
('Karim Arafat',   'karimarafat@mail.com',  '01222222222', 'Alexandria',  '$2a$10$84xesuVpsfWV2jGrvNcDtO6iOGPCYpDAOSz06tzAIdVC8yNm7k3OK', 'user'),
('Youssef Mohamed','youssefmo@mail.com',     '01033333333', 'Cairo',       '$2a$10$84xesuVpsfWV2jGrvNcDtO6iOGPCYpDAOSz06tzAIdVC8yNm7k3OK', 'user'),
('Youssef Hegazy', 'youssefhegazy@mail.com', '01144444444', 'Mansoura',    '$2a$10$84xesuVpsfWV2jGrvNcDtO6iOGPCYpDAOSz06tzAIdVC8yNm7k3OK', 'user');

-- المؤلفون ودور النشر
INSERT INTO authors (name, type, country, bio, joined) VALUES
('F. Scott Fitzgerald', 'author',    'USA',   'American novelist best known for capturing the Jazz Age.', '1920'),
('Harper Lee',          'author',    'USA',   'Author of To Kill a Mockingbird, a cornerstone of modern literature.', '1960'),
('George Orwell',       'author',    'UK',    'English writer known for his penetrating social and political commentary.', '1933'),
('J.K. Rowling',        'author',    'UK',    'Creator of the Harry Potter universe.', '1997'),
('J.R.R. Tolkien',      'author',    'UK',    'Philologist and fantasy pioneer, author of The Hobbit.', '1937'),
('Jane Austen',         'author',    'UK',    'English novelist of witty social commentary and sharp irony.', '1811'),
('J.D. Salinger',       'author',    'USA',   'Famed for The Catcher in the Rye.', '1951'),
('Frank Herbert',       'author',    'USA',   'Best known for Dune, an epic of ecology and politics.', '1965'),
('Paulo Coelho',        'author',    'Brazil','Brazilian novelist whose The Alchemist became a global phenomenon.', '1988'),
('Dan Brown',           'author',    'USA',   'Author of thrillers blending cryptography, art and history.', '2003'),
('Alex Michaelides',    'author',    'UK',    'Author of the psychological thriller The Silent Patient.', '2019'),
('James Clear',         'author',    'USA',   'Author of Atomic Habits, focused on behaviour change.', '2018'),
('Andy Weir',           'author',    'USA',   'Author of The Martian, a hard science survival story.', '2014'),
('Stephen Hawking',     'author',    'UK',    'Physicist whose A Brief History of Time reached millions.', '1988');

-- الكتب (kind = book)
INSERT INTO products (kind, title, author_id, category, price, quantity) VALUES
('book', 'The Great Gatsby',                1,  'Classic',   12.99, 50),
('book', 'To Kill a Mockingbird',           2,  'Classic',   14.99, 40),
('book', '1984',                            3,  'Dystopian', 11.99, 60),
('book', 'Harry Potter',                    4,  'Fantasy',   9.99,  80),
('book', 'The Hobbit',                      5,  'Fantasy',   15.99, 45),
('book', 'Pride and Prejudice',             6,  'Classic',   10.99, 35),
('book', 'The Catcher in the Rye',          7,  'Fiction',   13.49, 30),
('book', 'Dune',                            8,  'Sci-Fi',    16.99, 25),
('book', 'The Alchemist',                   9,  'Fiction',   13.49, 55),
('book', 'The Da Vinci Code',               10, 'Thriller',  14.99, 40),
('book', 'The Silent Patient',              11, 'Thriller',  12.49, 38),
('book', 'Atomic Habits',                   12, 'Self-Help', 18.99, 70),
('book', 'The Martian',                     13, 'Sci-Fi',    13.99, 42),
('book', 'A Brief History of Time',         14, 'Science',   15.99, 20);

-- الأدوات المدرسية والمكتبية (kind = supply)
INSERT INTO products (kind, title, author_id, category, price, quantity) VALUES
('supply', 'Scientific Calculator FX-991',      NULL, 'Calculators', 19.99, 30),
('supply', 'Desktop Calculator 12-Digit',       NULL, 'Calculators', 6.49,  40),
('supply', 'Ballpoint Pens (Pack of 10)',       NULL, 'Writing',     3.99,  100),
('supply', 'Gel Pens Set — 24 Colors',          NULL, 'Writing',     8.99,  60),
('supply', 'Mechanical Pencil 0.5mm',           NULL, 'Writing',     2.49,  90),
('supply', 'A5 Spiral Notebooks (Pack of 3)',   NULL, 'Notebooks',   7.99,  75),
('supply', 'A4 Hardcover Sketchbook',           NULL, 'Art',         9.49,  45),
('supply', 'Geometry Set — 8 Pieces',           NULL, 'Geometry',    4.99,  55),
('supply', 'Transparent Ruler 30cm',            NULL, 'Geometry',    1.99,  120),
('supply', 'School Backpack 40L',               NULL, 'Bags',        24.99, 15),
('supply', 'Pencil Case with Compartments',     NULL, 'Writing',     5.49,  50),
('supply', 'Permanent Markers (Set of 12)',     NULL, 'Art',         6.99,  40),
('supply', 'Whiteboard Markers (Set of 4)',     NULL, 'Office',      4.99,  65),
('supply', 'Stapler with 1000 Staples',         NULL, 'Office',      5.99,  35),
('supply', 'Highlighter Set (5 Colors)',        NULL, 'Writing',     4.49,  80),
('supply', 'Binder Clips & Paper Clips Kit',    NULL, 'Office',      2.99,  95);

-- طلبات تجريبية
INSERT INTO orders (order_code, user_id, customer, email, subtotal, shipping, total, status, governorate, city, payment, notes) VALUES
('ORD-1032', 3, 'Karim Arafat',   'karimarafat@mail.com',  23.98, 0, 23.98, 'pending',   'Alexandria', 'Smouha',   'Cash on delivery', 'deliver after 5pm'),
('ORD-1031', 5, 'Youssef Hegazy', 'youssefhegazy@mail.com', 26.98, 0, 26.98, 'processing','Mansoura',   'Downtown', 'Visa / Mastercard', NULL),
('ORD-1030', 4, 'Youssef Mohamed','youssefmo@mail.com',     26.48, 0, 26.48, 'shipped',   'Cairo',      'Nasr City', 'PayPal', NULL);

INSERT INTO order_items (order_id, product_id, title, price, quantity, kind, total) VALUES
(1, 1,  'The Great Gatsby',        12.99, 1, 'book',   12.99),
(1, 17, 'Ballpoint Pens (Pack of 10)', 3.99, 2, 'supply', 7.98),
(2, 12, 'Atomic Habits',           18.99, 1, 'book',   18.99),
(2, 21, 'A5 Spiral Notebooks (Pack of 3)', 7.99, 1, 'supply', 7.99),
(3, 8,  'Dune',                    16.99, 1, 'book',   16.99),
(3, 22, 'A4 Hardcover Sketchbook', 9.49,  1, 'supply', 9.49);
