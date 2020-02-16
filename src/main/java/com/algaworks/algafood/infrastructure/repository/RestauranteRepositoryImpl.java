package com.algaworks.algafood.infrastructure.repository;

import static com.algaworks.algafood.infrastructure.repository.spec.RestauranteSpecs.comFreteGratis;
import static com.algaworks.algafood.infrastructure.repository.spec.RestauranteSpecs.comNomeSemelhante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.algaworks.algafood.domain.model.Restaurante;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepositoryQueries;

@Repository
public class RestauranteRepositoryImpl implements RestauranteRepositoryQueries {
	

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired @Lazy
	private RestauranteRepository restauranteRepository;
	
	@Override
	public List<Restaurante> find(String nome, 
			BigDecimal taxaFreteInicial, BigDecimal taxaFreteFinal) {
		
		//TODO: Consulta dinâmica com JPQL
		
//		var jpql = new StringBuilder();
//		jpql.append("from Restaurante where 0 = 0 ");
//		if(nome != null) {
//			jpql.append("and nome like :nome ");
//		}
//		if(taxaFreteInicial != null) {
//			jpql.append("and nome like :nome");
//		}
		/*
		 * manda esse return ai de baixo para um TypedQuery<Restaurante> query para quebrar a QUERY
		 * var parametros = new HashMap<String, Object>(); => A chave é o nome do parâmetro (String) e o valor é tipo Object
		 * dentro de cada if vai fazendo parametros.put em vez de setParameter
		 *  como quebrou a query você tem acesso a ela de fora
		 * parametros.forEach((chave, valor) -> query.setParametros(chave, valor));
		 * return query.getResultList();
		 *  */
		
		
//		return manager.createQuery(jpql.toString(), Restaurante.class)
//		.setParameter("nome","%" + nome + "%")
//		.setParameter("taxaInicial",taxaFreteInicial)
//		.setParameter("taxaFinal",taxaFreteFinal)
//		.getResultList();
//		
		
//	O StringUtils verifica se não é nulo e não está vazio. Verifica se o length é maior que 0	
		
//		TODO: Consulta dinâmica com Criteria Api
		
		
//		instância um builder que vem de manager
		var builder = manager.getCriteriaBuilder();
		
		var criteria = builder.createQuery(Restaurante.class);
		var root = criteria.from(Restaurante.class);

		var predicates = new ArrayList<Predicate>();
		
		if (StringUtils.hasText(nome)) {
			predicates.add(builder.like(root.get("nome"), "%" + nome + "%"));
		}
		
		if (taxaFreteInicial != null) {
			predicates.add(builder.greaterThanOrEqualTo(root.get("taxaFrete"), taxaFreteInicial));
		}
		
		if (taxaFreteFinal != null) {
			predicates.add(builder.lessThanOrEqualTo(root.get("taxaFrete"), taxaFreteFinal));
		}
		
		criteria.where(predicates.toArray(new Predicate[0]));
		
		var query = manager.createQuery(criteria);
		return query.getResultList();
	}

	@Override
	public List<Restaurante> findComFreteGratis(String nome) {
		return restauranteRepository.findAll(comFreteGratis()
				.and(comNomeSemelhante(nome)));
	}
	
}
