package com.algaworks.algafood.domain.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.model.Restaurante;

//JpaSpecificationExecutor<Restaurante> serve para implementar o padrão de Specification no Repositório


//TODO: Customização do repositório base 
//SimpleJpaRepository é a implementação do JpaRepository

@Repository
public interface RestauranteRepository 
		extends CustomJpaRepository<Restaurante, Long>, RestauranteRepositoryQueries,
		JpaSpecificationExecutor<Restaurante> {

	@Query("from Restaurante r join fetch r.cozinha")
	List<Restaurante> findAll();
	
	List<Restaurante> queryByTaxaFreteBetween(BigDecimal taxaInicial, BigDecimal taxaFinal);
	
//	@Query("from Restaurante where nome like %:nome% and cozinha.id = :id")
	List<Restaurante> consultarPorNome(String nome, @Param("id") Long cozinha);
	
//	List<Restaurante> findByNomeContainingAndCozinhaId(String nome, Long cozinha);
	
	Optional<Restaurante> findFirstRestauranteByNomeContaining(String nome);
	
	List<Restaurante> findTop2ByNomeContaining(String nome);
	
	int countByCozinhaId(Long cozinha);
	
	Optional<Restaurante> findByCozinha(Cozinha cozinha);
	
}


//TODO: Resolvendo o problema do N + 1. Quando faz o join em toOne ele faz o fetch
// automaticamente, mas quando faz o fetch em toMany não faz o fetch automaticamente.
// Tem que ser adicionada a palavra fetch para obrigar a query a fazer o fetch, fazendo
// numa única query, numa única consulta sql