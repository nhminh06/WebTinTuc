-- Chạy script này trong phpMyAdmin hoặc MySQL CLI
-- trước khi khởi động ứng dụng

CREATE DATABASE IF NOT EXISTS newsdb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE newsdb;

-- Bảng articles và categories sẽ tự động tạo bởi Hibernate (ddl-auto=update)
-- Script này chỉ cần tạo database

-- Nếu muốn reset toàn bộ:
-- DROP DATABASE newsdb;
-- CREATE DATABASE newsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
