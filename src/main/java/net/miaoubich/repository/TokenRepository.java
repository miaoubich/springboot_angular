package net.miaoubich.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.miaoubich.user.Token;

public interface TokenRepository extends JpaRepository<Token, Integer>{

	Optional<Token> findByToken(String token);
}
