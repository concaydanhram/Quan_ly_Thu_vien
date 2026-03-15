CREATE DATABASE IF NOT EXISTS library_db;

USE library_db;

CREATE TABLE NOT EXISTS Books(
	id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    remaining INT NOT NULL
);

INSERT INTO Books (id, name, remaining) VALUES 
('b01', 'Lập trình Java', 3),
('b02', 'Cấu trúc dữ liệu', 3),
('b03', 'Mạng máy tính', 4);

CREATE TABLE IF NOT EXISTS Readers(
	id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

INSERT INTO Readers (id, name) VALUES 
('r04', 'Hiển'),
('r01', 'Hoàng'),
('r02', 'Nam'),
('r03', 'Duy');

CREATE TABLE IF NOT EXISTS Loans(
	readerID VARCHAR(50),
    bookID VARCHAR(50),
    PRIMARY KEY (readerID, bookID),
    FOREIGN KEY (readerID) REFERENCES Readers(id) ON DELETE CASCADE,
    FOREIGN KEY (bookID) REFERENCES Books(id) ON DELETE CASCADE
);

INSERT INTO Loans (readerID, bookID) VALUES 
('r01', 'b01'),
('r01', 'b02');