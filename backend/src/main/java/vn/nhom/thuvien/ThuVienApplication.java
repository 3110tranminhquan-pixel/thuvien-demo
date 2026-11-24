package vn.nhom.thuvien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class ThuVienApplication {

    public static void main(String[] args) {
        // Mọi phép tính ngày giờ (hạn trả, quá hạn, hạn giữ) đều theo giờ Việt Nam
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SpringApplication.run(ThuVienApplication.class, args);
    }
}
