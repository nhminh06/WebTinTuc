# VietNews - Web Tin Tức Java Spring Boot

## Công nghệ sử dụng
| Công nghệ | Phiên bản | Vai trò |
|---|---|---|
| Java | 17 | Ngôn ngữ lập trình |
| Maven | 3.6+ | Build tool, quản lý dependency |
| Spring Boot | 3.2.0 | Framework chính |
| Spring Web | — | HTTP server, Controller, Tomcat nhúng |
| Spring Data JPA + Hibernate | — | Kết nối DB, tự sinh SQL |
| Spring Security | — | Xác thực, phân quyền ROLE_USER/ROLE_ADMIN |
| Spring Validation | — | Validate dữ liệu đầu vào (@NotBlank...) |
| Thymeleaf | — | Template engine render HTML phía server |
| thymeleaf-extras-springsecurity6 | — | Dùng sec:authorize trong HTML |
| thymeleaf-layout-dialect | — | Layout dùng chung cho các trang |
| MySQL (XAMPP) | — | Cơ sở dữ liệu |
| mysql-connector-j | — | Driver kết nối Java ↔ MySQL |
| Cloudinary | 1.36.0 | Lưu trữ ảnh trên cloud |
| Lombok | — | Giảm boilerplate code |

---

## Yêu cầu
- Java 17+
- Maven 3.6+
- XAMPP (Apache + MySQL)

---

## Cài đặt và chạy

### 1. Khởi động XAMPP
- Bật **Apache** và **MySQL** trong XAMPP Control Panel

### 2. Tạo database
Vào **phpMyAdmin** → SQL → chạy:
```sql
CREATE DATABASE newsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Cấu hình (nếu cần)
Mở `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:13306/newsdb
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
- **Đăng nhập**: http://localhost:8080/login
- **Đăng ký**: http://localhost:8080/register
- **Admin**: http://localhost:8080/admin
- **Dashboard user**: http://localhost:8080/user/articles

---

## Tính năng

### Trang công khai
- ✅ Trang chủ với bài mới nhất + bài đọc nhiều nhất
- ✅ Xem chi tiết bài viết (đếm lượt xem)
- ✅ Phân loại theo chuyên mục
- ✅ Tìm kiếm bài viết
- ✅ Sidebar bài đọc nhiều nhất
- ✅ Bài viết liên quan

### Xác thực & Bảo mật
- ✅ Đăng ký tài khoản (validate trùng username/email)
- ✅ Đăng nhập / Đăng xuất
- ✅ Mã hóa mật khẩu BCrypt
- ✅ Phân quyền ROLE_USER / ROLE_ADMIN
- ✅ Bảo vệ CSRF tự động

### User (ROLE_USER)
- ✅ Viết bài mới (lưu dạng DRAFT, chờ admin duyệt)
- ✅ Sửa / Xóa bài của chính mình
- ✅ Xem danh sách bài đã viết
- ✅ Cập nhật hồ sơ cá nhân + avatar
- ✅ Đổi mật khẩu
- ✅ Bình luận bài viết
- ✅ Like / Unlike bình luận
- ✅ Báo cáo bình luận vi phạm
- ✅ Gửi tin nhắn liên hệ admin
- ✅ Xem lịch sử tin nhắn và phản hồi
- ✅ Upload ảnh lên Cloudinary

### Admin (ROLE_ADMIN)
- ✅ Dashboard thống kê (tổng bài, user, lượt xem, báo cáo chờ)
- ✅ Quản lý bài viết: thêm/sửa/xóa/duyệt (toggle DRAFT→PUBLISHED→HIDDEN)
- ✅ Quản lý người dùng: tìm kiếm, khoá/mở tài khoản, đổi role, xóa
- ✅ Quản lý báo cáo vi phạm: duyệt xóa comment / bỏ qua / xóa báo cáo
- ✅ Quản lý liên hệ: xem tin nhắn, phản hồi, xóa

---

## Cấu trúc project
```
src/main/java/com/news/
├── config/
│   ├── SecurityConfig.java        ← phân quyền URL, login/logout
│   ├── PasswordEncoderConfig.java ← BCrypt bean
│   └── CloudinaryConfig.java      ← kết nối Cloudinary
├── controller/
│   ├── HomeController.java        ← trang chủ, chi tiết bài, tìm kiếm, chuyên mục
│   ├── AuthController.java        ← đăng ký, đăng nhập
│   ├── UserArticleController.java ← CRUD bài viết của user
│   ├── ProfileController.java     ← hồ sơ, đổi mật khẩu, avatar
│   ├── CommentController.java     ← bình luận, like, báo cáo
│   ├── ContactController.java     ← liên hệ admin
│   ├── CloudinaryController.java  ← upload ảnh
│   └── AdminController.java       ← toàn bộ trang quản trị
├── model/
│   ├── Article.java               ← bảng articles
│   ├── ArticleStatus.java         ← enum: DRAFT / PUBLISHED / HIDDEN
│   ├── Category.java              ← bảng categories
│   ├── User.java                  ← bảng users
│   ├── Comment.java               ← bảng comments
│   ├── CommentLike.java           ← bảng comment_likes
│   ├── Contact.java               ← bảng contacts
│   └── Report.java                ← bảng reports
├── repository/
│   ├── ArticleRepository.java
│   ├── CategoryRepository.java
│   ├── UserRepository.java
│   ├── CommentRepository.java
│   ├── CommentLikeRepository.java
│   ├── ContactRepository.java
│   └── ReportRepository.java
├── service/
│   ├── ArticleService.java        ← CRUD bài, toggle status, phân trang
│   ├── UserService.java           ← đăng ký, tìm user, UserDetailsService
│   ├── CommentService.java        ← thêm/xóa comment, like/unlike
│   ├── ContactService.java        ← gửi/trả lời tin nhắn
│   ├── ReportService.java         ← báo cáo vi phạm
│   ├── CloudinaryService.java     ← upload/xóa ảnh
│   └── DataInitializer.java       ← seed data mẫu lần đầu khởi động
└── NewsAppApplication.java        ← điểm khởi động ứng dụng

src/main/resources/
├── static/
│   ├── css/style.css              ← giao diện trang public
│   └── css/user-layout.css        ← giao diện dashboard user
├── templates/
│   ├── index.html                 ← trang chủ
│   ├── article.html               ← chi tiết bài viết + bình luận
│   ├── category.html              ← danh sách theo chuyên mục
│   ├── search.html                ← kết quả tìm kiếm
│   ├── auth/
│   │   ├── login.html
│   │   └── register.html
│   ├── user/
│   │   ├── articles.html          ← danh sách bài của user
│   │   ├── article-form.html      ← form viết/sửa bài
│   │   ├── profile.html           ← hồ sơ cá nhân
│   │   └── contact.html           ← liên hệ admin
│   ├── admin/
│   │   ├── dashboard.html
│   │   ├── articles.html
│   │   ├── article-form.html
│   │   ├── users.html
│   │   ├── reports.html
│   │   └── contacts.html
│   └── fragments/
│       ├── layout.html            ← layout dùng chung
│       └── user-sidebar.html      ← sidebar dashboard user
└── application.properties         ← cấu hình DB, Cloudinary, server
```

---

## Luồng trạng thái bài viết
```
User tạo bài → DRAFT
                 ↓ Admin duyệt
             PUBLISHED  ←→  HIDDEN
                 ↑               ↓
              DRAFT   ←←←←←←←←←←
```

- **DRAFT** — bài nháp, chỉ tác giả và admin thấy
- **PUBLISHED** — bài đã duyệt, hiển thị trang chủ
- **HIDDEN** — bài bị ẩn, không hiển thị trang chủ
