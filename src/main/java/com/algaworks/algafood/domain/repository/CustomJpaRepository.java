package com.algaworks.algafood.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

//TODO: Impedir que sejam levantados beans para um repositório intermediário

@NoRepositoryBean
public interface CustomJpaRepository<T, ID> extends JpaRepository<T, ID> {

	Optional<T> buscarPrimeiro();
	
}
