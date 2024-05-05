package net.miaoubich.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.miaoubich.user.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByEmail(String email);
}
