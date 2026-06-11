# VietNews - Web Tin Tức Java Spring Boot

## Yêu cầu
- Java 17+
- Maven 3.6+
- XAMPP (Apache + MySQL)

## Cài đặt và chạy

### 1. Khởi động XAMPP
- Bật **Apache** và **MySQL** trong XAMPP Control Panel

### 2. Tạo database
- Vào **phpMyAdmin** → SQL → chạy:
```sql
CREATE DATABASE newsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Cấu hình (nếu cần)
Mở `src/main/resources/application.properties`:
```
spring.datasource.username=root
spring.datasource.password=         ← để trống nếu XAMPP mặc định
```

### 4. Build và chạy
```bash
cd news-app
mvn spring-boot:run
```

### 5. Truy cập
- **Trang chủ**: http://localhost:8080
- **Admin**: http://localhost:8080/admin

## Tính năng
- ✅ Trang chủ với hero section + bài nổi bật
- ✅ Xem bài viết chi tiết (đếm lượt xem)
- ✅ Phân loại theo chuyên mục
- ✅ Tìm kiếm bài viết
- ✅ Sidebar bài đọc nhiều nhất
- ✅ Admin: thêm/sửa/xóa/đăng bài
- ✅ Data mẫu tự động khi khởi động lần đầu

## Cấu trúc
```
src/main/java/com/news/
├── controller/
│   ├── HomeController.java    ← trang chủ, bài viết, tìm kiếm
│   └── AdminController.java   ← quản trị
├── model/
│   ├── Article.java
│   └── Category.java
├── repository/
│   └── ArticleRepository.java
└── service/
    ├── ArticleService.java
    └── DataInitializer.java   ← seed data mẫu
```
