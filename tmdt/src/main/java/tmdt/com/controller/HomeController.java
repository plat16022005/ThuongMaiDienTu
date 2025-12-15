package tmdt.com.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import tmdt.com.dto.UserAddressDTO;
import tmdt.com.entity.User;
import tmdt.com.service.UserService;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String showHome() {
        return "buyer/home";
    }

    @GetMapping("/account")
    public String showLoginForm(HttpServletRequest request, HttpSession session) {
        String referer = request.getHeader("Referer");
        if (referer != null 
                && !referer.contains("/account") 
                && !referer.contains("/login")) {
            session.setAttribute("redirectUrl", referer);
        }
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          RedirectAttributes redirectAttributes,
                          HttpSession session)
    {
        User user = userService.login(email, password);
        if (user != null)
        {
            session.setAttribute("user", user);
        	if (!userService.hasAddress(user))
        	{
        		return "redirect:/get-address";
        	}
            String redirectUrl = (String) session.getAttribute("redirectUrl");
            if (redirectUrl != null) {
                session.removeAttribute("redirectUrl");
                return "redirect:" + redirectUrl;
            }

            return "redirect:/";
        }
        else
        {
            redirectAttributes.addFlashAttribute("error", "Sai email hoặc mật khẩu!");
            return "redirect:/account";
        }
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String name,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam String password,
                             @RequestParam("confirm_password") String confirmPassword,
                             RedirectAttributes redirectAttributes,
                             HttpSession session)
    {
        if (!confirmPassword.equals(password))
        {
            redirectAttributes.addFlashAttribute("errorPass", "Vui lòng nhập lại mật khẩu đúng với đã nhập!");
            return "redirect:/account";
        }

        User newUser = userService.register(name, email, phone, password);
        if (newUser != null) {
            redirectAttributes.addFlashAttribute("success", "Tạo tài khoản thành công!");
            System.out.println("Tạo thành công");
            return "redirect:/account";
        } else {
            redirectAttributes.addFlashAttribute("error", "Email đã có người sử dụng");
            return "redirect:/account";
        }

    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("success", "Đăng xuất thành công!");
        return "redirect:/";
    }
    
    @GetMapping("/forgot-pass")
    public String showForgot()
    {
    	return "forgetpass";
    }
    @GetMapping("/verify-otp")
    public String showVerifyOtp(HttpSession session, Model model)
    {
        String email = (String) session.getAttribute("otp_email");
        if (email == null) {
            return "redirect:/forgot-password"; // hoặc trang nhập email
        }
        model.addAttribute("email", email);
    	return "otp";	
    }
    @PostMapping("/api/email/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestBody Map<String, String> body,
            HttpSession session) {

        String inputOtp = body.get("otp");
        String emailUser = body.get("userEmail");
        String sessionOtp = (String) session.getAttribute("otp");

        if (sessionOtp == null) {
            return ResponseEntity.status(401).body("OTP đã hết hạn");
        }

        if (!sessionOtp.equals(inputOtp)) {
            return ResponseEntity.badRequest().body("OTP không đúng");
        }

        // ✅ OTP đúng → cho phép reset mật khẩu
        session.setAttribute("otp_verified", true);
        session.setAttribute("emailUser", emailUser);
        return ResponseEntity.ok("Xác thực OTP thành công");
    }
    @GetMapping("/reset-password")
    public String showResetForm()
    {
    	return "newpass";
    }
    @PostMapping("/api/password/do-reset-pass")
    public ResponseEntity<String> handleResetPass(@RequestBody Map<String, String> body, HttpSession session)
    {
    	String email = (String) session.getAttribute("emailUser");
    	String newPass = body.get("newPassword");
        User user = userService.resetPass(email, newPass);
        if (user == null) {
            return ResponseEntity.status(404).body("Không tìm thấy tài khoản");
        }
        return ResponseEntity.ok("Đổi mật khẩu thành công");
    }
    @GetMapping("/get-address")
    public String showGetAddress()
    {
    	return "getaddress";
    }
    @PostMapping("/api/address/get-address")
    public ResponseEntity<String> handleGetAddress(@RequestBody UserAddressDTO body, HttpSession session)
    {
    	User user = (User) session.getAttribute("user");
    	userService.getAddress(user, body.getAddressLine(), body.getWard(), body.getDistrict(), body.getProvince());
    	return ResponseEntity.ok("Cập nhật địa chỉ thành công!");
    }
}
