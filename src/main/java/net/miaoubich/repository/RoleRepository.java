package net.miaoubich.repository;

import java.util.Optional;

import net.miaoubich.role.Role;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer>{

	Optional<Role> findByName(String role);
}
