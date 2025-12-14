package tmdt.com.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import tmdt.com.repository.UserRepository;
import tmdt.com.service.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> body,
                                          HttpSession session) throws MessagingException {

        String email = body.get("email");

        String otp = emailService.generateOTP(6);

        // Lưu OTP vào session (có thể kèm email + thời gian hết hạn)
        session.setAttribute("otp", otp);
        session.setAttribute("otp_email", email);
        session.setAttribute("otp_exp", System.currentTimeMillis() + 5 * 60 * 1000); // 5 phút

        emailService.sendEmail(
            email,
            "Mã xác nhận UTEBuk",
            "Mã OTP của bạn là: " + otp + "\nMã có hiệu lực trong 5 phút."
        );

        return ResponseEntity.ok("Mã OTP đã được gửi tới " + email);
    }
}
