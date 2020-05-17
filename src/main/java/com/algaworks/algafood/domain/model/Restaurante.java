package com.algaworks.algafood.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.algaworks.algafood.core.validation.Groups;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Restaurante {

	@EqualsAndHashCode.Include
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank
	@Column(nullable = false)
	private String nome;
	
	@NotNull
	@PositiveOrZero
	@Column(name = "taxa_frete", nullable = false)
	private BigDecimal taxaFrete;
	
//	TODO: Todas as associações toOne usam EagerLoading, carregamento ansioso ou atencipado.
//	Para cada restaurante buscado, o JPA vai buscar um cozinha. Busca independente de 
//	ser necessário. Nesse caso está sendo ruim, trazendo não apenas a cozinha como 
//	também a cidade. O JPA tem um "cache" e não faz selects à toa. Busca por id faz um select só.
//	Buscar todos faz mais select, sem usar joins. Mas o fato de ser eager não tem nada a ver
//	com o número de selects. Se o atributo não tiver como nullable = false, vai ser nullable = true,
//	ai o JPA pode tentar fazer left join ao invés de inner join para evitar que valores nulos
//	não retornem nada
	
	//O JsonIgnore não evita que os selects sejam feitos. Problema N + 1, porque o fetch padrão
//	é ansioso. Não quer saber se está usando ou não, já busca automaticamente.
	
	@Valid
	@ConvertGroup(from = Default.class, to = Groups.CozinhaId.class)
	@NotNull
	@ManyToOne
	@JoinColumn(name = "cozinha_id", nullable = false)
	private Cozinha cozinha;
	
	@JsonIgnore
	@Embedded
	private Endereco endereco;
	
	//TODO: em @CreationTimestamp pega a hora e data atual e atribui a entidade na hora da
//	criação
	
	@JsonIgnore
	@CreationTimestamp
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime dataCadastro;
	
	
	
	//TODO: em @CreationTimestamp pega a hora e data atual e atribui a entidade na hora da
//	atualização
	@JsonIgnore
	@UpdateTimestamp
	@Column(nullable = false, columnDefinition = "datetime")
	private LocalDateTime dataAtualizacao;
	
	
	//TODO: em @ManyToMany é necessário: @JoinTable e 2 @JoinColumn(um pra joinColumns
//	e um pra inverseJoinColumns). JoinColumns pega a chave primária referente a Restaurantes
//	 e inverseJoinColumns pega a chave primária referente a FormasPagamento, visto que
//	a chave primária da tabela de relacionamento é composta por 2 chaves estrangeiras
	
//	TODO: Alto impacto do payload gerado ao serializar todas as formas de pagamento
	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "restaurante_forma_pagamento",
			joinColumns = @JoinColumn(name = "restaurante_id"),
			inverseJoinColumns = @JoinColumn(name = "forma_pagamento_id"))
	private List<FormaPagamento> formasPagamento = new ArrayList<>();
	
	//TODO: Todo carregamento ToMany é feito por demanda. Só ocorre quando o recurso vai 
//	ser utilizado. Quando eu pedir uma lista de restaurantes e tiver que serializar as 
//	formas de pagamentos o JPA vai ver que não tem e vai lembrar que toMany é Lazy, então
//	vai no banco buscar esses registros. Se eu tiver uma lista com 100 restaurantes ele vai
//	fazer 100 selects para saber quem são as formas de pagamento. Não vai gerar cache
	
//	restaurantes.get(0).getFormasPagamento().forEach(System.out::println);
	
	@JsonIgnore
	@OneToMany(mappedBy = "restaurante")
	private List<Produto> produtos = new ArrayList<>();
	
}


//TODO: Alterando de Eager para Lazy: Mudando um toOne de Eager para Lazy só faz 1 select no listAll e quando você 
//pega o nome do get(0) ai ele faz um select a mais (no caso 2). Usar uma cozinha significa
//chamar algum método da instância retornada de cozinha. O JPA cria um proxy, uma classe
//que encapsula a cozinha. Isso é feito no RunTime. A classe proxy é instanciada e coloca 
//na cozinha. Não foi possível serializar Cozinha. Antes tinha o JsonIgnore que não dava
//problema. 

//JsonIgnoreProperties ignora propriedades dentro da Cozinha, não a Cozinha em si
// como o JsonIgnore faz. O ignoreProperties ignora propriedades dentro da instância.
// Fez um select para cada cozinha. Os mesmos select feitos quando estava usando
//Eager

//Eager: consulta os restaurantes e já consulta as cozinhas
//Lazy: consulta os restaurantes e não consulta cozinhas. Quando precisa ele vai e busca


//TODO: Alterando de Lazy para Eager: Faz select de forma de pagamento mesmo
//sem eu estar usando, pois estou buscando apenas os restaurantes. 
//Na prática não se usa muito de lazy para eager, geralmente usa-se mais
//eager to lazy