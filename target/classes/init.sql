CREATE DATABASE IF NOT EXISTS library_db;

USE library_db;

DROP TABLE IF EXISTS Books;
CREATE TABLE Books(
	id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    remaining INT NOT NULL
);

INSERT INTO Books (id, name, remaining) VALUES 
('b01', 'Lập trình Java', 3),
('b02', 'Cấu trúc dữ liệu', 3),
('b03', 'Mạng máy tính', 4);

DROP TABLE IF EXISTS Readers;
CREATE TABLE Readers(
	id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO Readers (id, name) VALUES 
('r04', 'Hiển'),
('r01', 'Hoàng'),
('r02', 'Nam'),
('r03', 'Duy');

DROP TABLE IF EXISTS Loans;
CREATE TABLE Loans(
	readerID VARCHAR(50),
    bookID VARCHAR(50),
    PRIMARY KEY (readerID, bookID),
    FOREIGN KEY (readerID) REFERENCES Readers(id) ON DELETE CASCADE,
    FOREIGN KEY (bookID) REFERENCES Books(id) ON DELETE CASCADE
);

INSERT INTO Loans (readerID, bookID) VALUES 
('r01', 'b01'),
('r01', 'b02');

DROP TABLE IF EXISTS TransactionHistory;
CREATE TABLE TransactionHistory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    readerID VARCHAR(50),
    bookID VARCHAR(255),
    actionType VARCHAR(20), -- 'Mượn' hoặc 'Trả'
    transactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (readerID) REFERENCES Readers(id) ON DELETE CASCADE,
    FOREIGN KEY (bookID) REFERENCES Books(id) ON DELETE CASCADE
);

INSERT INTO TransactionHistory (readerID, bookID, actionType, transactionDate) VALUES
('r01', 'b01', 'Mượn', '2026-03-10 08:30:00'),
('r01', 'b02', 'Mượn', '2026-03-12 14:15:00');