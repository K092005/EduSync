-- ============================================================
-- EduSync Database Setup
-- Run this ONCE in MySQL before launching the app
-- ============================================================

CREATE DATABASE IF NOT EXISTS edusync;
USE edusync;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(100),
    role          VARCHAR(20)  DEFAULT 'student',
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- Notes table
CREATE TABLE IF NOT EXISTS notes (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT,
    subject    VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Resources (uploaded files) table
CREATE TABLE IF NOT EXISTS resources (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NOT NULL,
    note_id     INT,
    filename    VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    file_path   VARCHAR(500) NOT NULL,
    file_type   VARCHAR(50),
    file_size   BIGINT,
    category    VARCHAR(50)  DEFAULT 'General',
    uploaded_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE SET NULL
);

-- Chat messages table
CREATE TABLE IF NOT EXISTS messages (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT          NOT NULL,
    channel    VARCHAR(50)  DEFAULT 'general',
    content    TEXT         NOT NULL,
    sent_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- PYQ (Previous Year Questions) table
CREATE TABLE IF NOT EXISTS pyq (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NOT NULL,
    subject     VARCHAR(100) NOT NULL,
    year        INT,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    file_path   VARCHAR(500),
    filename    VARCHAR(255),
    uploaded_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ── Seed demo data ──────────────────────────────────────────

-- Demo users (password = "password" SHA-256 hashed)
INSERT IGNORE INTO users (username, password_hash, full_name, email) VALUES
('demo',    '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'Krishna Singh', 'krishna@mitwpu.edu.in'),
('isha',    '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'Isha Damle',    'isha@mitwpu.edu.in'),
('sadaf',   '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'Sadaf Jumani',  'sadaf@mitwpu.edu.in'),
('pratik',  '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8', 'Pratik Singh',  'pratik@mitwpu.edu.in');

-- Demo notes
INSERT IGNORE INTO notes (user_id, title, content, subject) VALUES
(1, 'DBMS Normalization', '1NF: Atomic values, no repeating groups.\n2NF: No partial dependency on composite key.\n3NF: No transitive dependency.\nBCNF: Every determinant is a superkey.', 'DBMS'),
(1, 'OS Process Scheduling', 'FCFS: Non-preemptive, simple.\nSJF: Shortest job first, can starve.\nRound Robin: Time quantum based, fair.\nPriority: Can cause starvation, use aging.', 'OS'),
(2, 'Java OOP Concepts', 'Encapsulation: Hide data with private fields + getters/setters.\nInheritance: extends keyword.\nPolymorphism: method overloading & overriding.\nAbstraction: abstract classes & interfaces.', 'Java'),
(3, 'TCP/IP Model', 'Application → Transport → Internet → Network Access.\nTCP: Reliable, connection-oriented.\nUDP: Fast, connectionless.\nIP Addressing: IPv4 (32-bit), IPv6 (128-bit).', 'CN');

-- Demo PYQs
INSERT IGNORE INTO pyq (user_id, subject, year, title, description) VALUES
(1, 'DBMS', 2023, 'Mid-Sem Paper — Unit 1-3', 'ER Diagrams, Normalization (1NF-BCNF), SQL Queries, Relational Algebra'),
(1, 'OS',   2023, 'End-Sem Paper',             'Process Scheduling, Deadlock Detection, Memory Management, File Systems'),
(2, 'CN',   2022, 'Mid-Sem Paper',             'OSI Model, TCP/IP Stack, Subnetting, Routing Algorithms'),
(2, 'DSA',  2023, 'End-Sem Paper',             'Trees, Graphs (BFS/DFS), Sorting Algorithms, Hashing Techniques'),
(1, 'Java', 2023, 'Mini Project Viva Q&A',     'OOP Concepts, JDBC, Swing GUI, Exception Handling, Collections'),
(3, 'DBMS', 2022, 'End-Sem Paper',             'Transactions, Concurrency Control, Indexing, B+ Trees, Query Optimization');

-- Demo messages
INSERT IGNORE INTO messages (user_id, channel, content) VALUES
(2, 'general', 'Hey everyone! Anyone done the DSA assignment?'),
(3, 'general', 'Almost done with Q1-3, Q4 is tough'),
(1, 'general', 'Same! The graph traversal part is tricky'),
(2, 'dbms',    'Can someone explain 3NF vs BCNF?'),
(3, 'dbms',    'BCNF is stricter — every determinant must be a superkey, not just non-prime attributes'),
(1, 'java',    'What is the best way to structure Swing layouts?'),
(2, 'java',    'Use BorderLayout for the frame, then nested panels with GridBagLayout for forms');
