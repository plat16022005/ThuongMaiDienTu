package tmdt.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
        	System.out.println("Khác kìa!");
            redirectAttributes.addFlashAttribute("errorPass", "Vui lòng nhập lại mật khẩu đúng với đã nhập!");
            return "redirect:/account";
        }

        User newUser = userService.register(name, email, phone, password);
        if (newUser != null) {
            // Auto login nếu muốn:
            session.setAttribute("user", newUser);
            redirectAttributes.addFlashAttribute("success", "Tạo tài khoản thành công!");
            System.out.println("Tạo thành công");
            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Email đã có người sử dụng");
            System.out.println("Email xài r");
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
}
