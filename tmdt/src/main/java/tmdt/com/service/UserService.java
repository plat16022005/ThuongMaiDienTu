package tmdt.com.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tmdt.com.entity.User;
import tmdt.com.repository.UserRepository;
import tmdt.com.util.PasswordUtil;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository; 
	public User login(String email, String password)
	{
		User user = userRepository.findByEmail(email);
		if (user != null && PasswordUtil.hashPassword(password).equals(user.getPasswordHash()))
		{
			return user;
		}
		return null;
	}
	public User register(String fullName, String email, String phone, String password)
	{
	    User existing = userRepository.findByEmail(email);
	    if (existing != null) {
	        return null;
	    }

	    User newUser = new User();
	    newUser.setFullName(fullName);
	    newUser.setCreatedAt(LocalDateTime.now());
	    newUser.setEmail(email);
	    newUser.setPhone(phone);
	    newUser.setPasswordHash(PasswordUtil.hashPassword(password));
	    newUser.setRole(User.Role.CUSTOMER);
	    newUser.setStatus(User.Status.ACTIVE);
	    newUser.setUpdatedAt(LocalDateTime.now());
	    return userRepository.save(newUser);
	}

}
