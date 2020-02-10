package com.algaworks.algafood;

import org.flywaydb.core.Flyway;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.algaworks.algafood.domain.model.Cozinha;
import com.algaworks.algafood.domain.repository.CozinhaRepository;
import com.algaworks.algafood.domain.repository.RestauranteRepository;
import com.algaworks.algafood.domain.service.CadastroCozinhaService;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("/application.test-properties")
public class CadastroCozinhaIT {

	@LocalServerPort
	private int port;

	@Autowired
	private Flyway flyway;

	@Autowired
	CadastroCozinhaService cadastroCozinhaService;

	@Autowired
	RestauranteRepository restauranteRepository;

	@Autowired
	CozinhaRepository cozinhaRepository;
	
	@Autowired
	private com.algaworks.algafood.util.DatabaseCleaner databaseCleaner;

//	@Test
//	public void deveAtribuirId_quandoCadastrarCozinhaComDadosCorretos() {
//		Cozinha cozinhaDeTeste = new Cozinha();
//		cozinhaDeTeste.setNome("AmazonFood");
//
//		cozinhaDeTeste = cadastroCozinhaService.salvar(cozinhaDeTeste);
//
//
//		assertThat(cozinhaDeTeste).isNotNull();
//		assertThat(cozinhaDeTeste.getId()).isNotNull();
//
//	}
//
//	@Test(expected = ConstraintViolationException.class)
//	public void deveFalharAoCadastrarCozinha_QuandoSemNome() {
//
//		Cozinha cozinhaDeTeste = new Cozinha();
//		cozinhaDeTeste.setId(null);
//		cozinhaDeTeste = cadastroCozinhaService.salvar(cozinhaDeTeste);
//
//	}
//
//	@Test(expected = EntidadeEmUsoException.class)
//	public void deveFalhar_QuandoExcluirCozinhaEmUso() {
//		cadastroCozinhaService.excluir(1L);
//	}
//
//	@Test(expected = CozinhaNaoEncontradaException.class)
//	public void deveFalhar_QuandoExcluirCozinhaInexistente() {
//		cadastroCozinhaService.excluir(100L);
//	}

	@Before
	public void setUp() {
		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
		RestAssured.port = port;
		RestAssured.basePath = "/cozinhas";
		databaseCleaner.clearTables();
		this.prepararDados();
	}

	@Test
	public void deveRetornarStatus200_QuandoConsultarCozinhas() {

		RestAssured.given().accept(ContentType.JSON).when().get().then()
				.statusCode(org.springframework.http.HttpStatus.OK.value());
	}

	@Test
	public void deveConter2Cozinhas_QuandoConsultarCozinhas() {

		RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

		RestAssured.given().accept(ContentType.JSON).when().get().then().body("", Matchers.hasSize(2));
	}

	@Test
	public void deveRetornarStatus201_QuandoCadastrarCozinha() {
		RestAssured.given().body("{ \"nome\": \"Chinesa\" }").contentType(ContentType.JSON).accept(ContentType.JSON)
				.when().post().then().statusCode(HttpStatus.CREATED.value());
	}
	
	@Test
	public void deveRetornarRespostaEStatusCorretos_QuandoConsultarCozinhaExistente() {
		RestAssured.given()
		.pathParam("cozinhaId", 2)
		.accept(ContentType.JSON)
		.when()
		.get("/{cozinhaId}")
		.then()
		.statusCode(HttpStatus.OK.value())
		.body("nome", Matchers.equalTo("Americana"));
	}
	
	@Test
	public void deveRetornarStatus404_QuandoConsultarCozinhaExistente() {
		RestAssured.given()
		.pathParam("cozinhaId", 100)
		.accept(ContentType.JSON)
		.when()
		.get("/{cozinhaId}")
		.then()
		.statusCode(HttpStatus.NOT_FOUND.value());
	}
	
	private void prepararDados() {
		Cozinha cozinha1 = new Cozinha();
		cozinha1.setNome("Tailandesa");
		cozinhaRepository.save(cozinha1);
		Cozinha cozinha2 = new Cozinha();
		cozinha2.setNome("Americana");
		cozinhaRepository.save(cozinha2);
	}
}
