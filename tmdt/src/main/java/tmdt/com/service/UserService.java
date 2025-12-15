package tmdt.com.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tmdt.com.entity.User;
import tmdt.com.entity.UserAddress;
import tmdt.com.repository.UserAddressRepository;
import tmdt.com.repository.UserRepository;
import tmdt.com.util.PasswordUtil;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository; 
	@Autowired
	private UserAddressRepository userAddressRepository;
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
	public User resetPass(String email, String newPass)
	{
		User user = userRepository.findByEmail(email);
		if (user == null)
		{
			return null;
		}
		user.setPasswordHash(PasswordUtil.hashPassword(newPass));
		return userRepository.save(user);
	}
	public boolean hasAddress(User user)
	{
		UserAddress userAddress = userAddressRepository.findByUser(user);
		if (userAddress == null)
		{
			return false;
		}
		return true;
	}
	public UserAddress getAddress(User user, String addressLine, String ward, String district, String province)
	{
		UserAddress userAddress = new UserAddress();
		userAddress.setUser(user);
		userAddress.setFullName(user.getFullName());
		userAddress.setPhone(user.getPhone());
		userAddress.setAddressLine(addressLine);
		userAddress.setWard(ward);
		userAddress.setDistrict(district);
		userAddress.setProvince(province);
		userAddress.setIsDefault(true);
		userAddressRepository.save(userAddress);
		return userAddress;
	}
}
