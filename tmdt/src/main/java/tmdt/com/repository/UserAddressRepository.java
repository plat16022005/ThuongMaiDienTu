package tmdt.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tmdt.com.entity.User;
import tmdt.com.entity.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, Integer> {
	UserAddress findByUser(User user);
}
