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
	
//	TODO: Injetando o próprio repositório na implementação customizada
//	Só é injetado quando alguém precisa dele. Senão vai criar uma dependência circular
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
		
//		antes: return manager.createQuery("from Restaurante", Restaurante.class).getResultList();
		
		//precisa: buider, criteria, root, predicates(parameters da query)
//		instância um builder que vem de manager
		var builder = manager.getCriteriaBuilder();
		var criteria = builder.createQuery(Restaurante.class); //ONDE VAI RESOLVER A QUERY
		var root = criteria.from(Restaurante.class); //ENTIDADE ONDE VAI BUSCAR
		
//		<named-native-query name="criteria.from(*)"
//				result-class="createQuery">

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
		
//		para construir os parâmetros e adicionar no array de Predicates é necessário uma instância de builder
//		finalmente faz um where em criteria passando o Predicate -> convertendo para Array (não ArrayList)
		
		
		criteria.where(predicates.toArray(new Predicate[0]));
//		criteria.where(PREDICATES);
//		convertendo List to Array: usa to Array na lista e passa de parâmetro um new Array vazio -> [0]
//		para converter de Array para List: 
//		ArrayList<Element> arrayList = new ArrayList<Element>(Arrays.asList(array));

		var query = manager.createQuery(criteria);
		return query.getResultList();
	}

	@Override
	public List<Restaurante> findComFreteGratis(String nome) {
		return restauranteRepository.findAll(comFreteGratis()
				.and(comNomeSemelhante(nome)));
	}
	
}
