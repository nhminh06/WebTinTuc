package com.news.service;

import com.news.model.Article;
import com.news.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.news.model.ArticleStatus;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public void run(String... args) {
        if (articleRepository.count() > 0) return;

        String[] categories = {"Công nghệ", "Thể thao", "Kinh tế", "Giải trí", "Xã hội"};
        String[] authors = {"Nguyễn Văn A", "Trần Thị B", "Lê Văn C", "Phạm Thị D"};

        String[][] data = {
            {"Trí tuệ nhân tạo thay đổi thế giới như thế nào?",
             "AI đang dần thâm nhập vào mọi lĩnh vực của cuộc sống, từ y tế đến giáo dục và sản xuất.",
             "Công nghệ"},
            {"Đội tuyển Việt Nam giành chiến thắng lịch sử",
             "Sau trận đấu căng thẳng, đội tuyển quốc gia đã xuất sắc vượt qua đối thủ mạnh để tiến vào vòng chung kết.",
             "Thể thao"},
            {"Thị trường chứng khoán tăng trưởng mạnh quý III",
             "Chỉ số VN-Index liên tục ghi nhận mức tăng trưởng ấn tượng nhờ dòng tiền từ nhà đầu tư nước ngoài.",
             "Kinh tế"},
            {"Phim bom tấn Việt Nam phá kỷ lục phòng vé",
             "Bộ phim đạt doanh thu 100 tỷ đồng chỉ sau 3 ngày công chiếu, tạo nên cơn sốt tại các rạp chiếu phim.",
             "Giải trí"},
            {"Hà Nội triển khai xe buýt điện toàn thành phố",
             "Dự án xe buýt điện thân thiện môi trường chính thức được đưa vào vận hành trên 10 tuyến đường chính.",
             "Xã hội"},
            {"Startup Việt gọi vốn thành công 50 triệu USD",
             "Công ty khởi nghiệp công nghệ tài chính của Việt Nam vừa nhận được khoản đầu tư lớn từ quỹ ngoại.",
             "Kinh tế"},
            {"Robot phẫu thuật được ứng dụng tại bệnh viện lớn",
             "Lần đầu tiên tại Việt Nam, robot phẫu thuật hiện đại được đưa vào sử dụng thành công trong ca mổ tim.",
             "Công nghệ"},
            {"SEA Games: Việt Nam đứng thứ 2 toàn đoàn",
             "Đoàn thể thao Việt Nam kết thúc SEA Games với 120 huy chương vàng, vượt chỉ tiêu đề ra.",
             "Thể thao"},
        };

        String[] images = {
            "https://picsum.photos/seed/ai/800/500",
            "https://picsum.photos/seed/sport/800/500",
            "https://picsum.photos/seed/stock/800/500",
            "https://picsum.photos/seed/film/800/500",
            "https://picsum.photos/seed/bus/800/500",
            "https://picsum.photos/seed/startup/800/500",
            "https://picsum.photos/seed/robot/800/500",
            "https://picsum.photos/seed/games/800/500",
        };

        for (int i = 0; i < data.length; i++) {
            Article a = new Article();
            a.setTitle(data[i][0]);
            a.setSummary(data[i][1]);
            a.setContent("<p>" + data[i][1] + "</p><p>Đây là nội dung chi tiết của bài viết. "
                + "Bài viết cung cấp những thông tin mới nhất và chính xác nhất về chủ đề này. "
                + "Độc giả có thể theo dõi các bài viết liên quan để cập nhật thêm thông tin.</p>"
                + "<p>Các chuyên gia trong ngành nhận định rằng xu hướng này sẽ tiếp tục phát triển "
                + "mạnh mẽ trong thời gian tới, mang lại nhiều cơ hội và thách thức mới.</p>");
            a.setCategory(data[i][2]);
            a.setAuthor(authors[i % authors.length]);
            a.setImageUrl(images[i]);
            a.setStatus(ArticleStatus.PUBLISHED);
            a.setViewCount((int)(Math.random() * 1000 + 50));
            articleRepository.save(a);
        }

        System.out.println("✅ Đã tạo " + data.length + " bài viết mẫu");
    }
}
