package tmdt.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import tmdt.com.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByEmail(String email);
}
