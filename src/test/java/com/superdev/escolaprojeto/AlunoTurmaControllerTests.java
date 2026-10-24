package com.superdev.escolaprojeto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class AlunoTurmaControllerTests {
  @Autowired MockMvc mvc;
  @Autowired JdbcTemplate jdbc;
  private final ObjectMapper json = new ObjectMapper();

  @BeforeEach
  void limparBanco() {
    jdbc.execute("TRUNCATE turmas, alunos, professores, unidades RESTART IDENTITY CASCADE");
  }

  private ResultActions request(String method, String route, Object body) throws Exception {
    var builder =
        switch (method) {
          case "POST" -> post(route);
          case "PUT" -> put(route);
          case "PATCH" -> patch(route);
          default -> get(route);
        };
    if (body != null)
      builder.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(body));
    return mvc.perform(builder);
  }

  private long create(String route, Object body) throws Exception {
    String response =
        request("POST", route, body).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
    return json.readTree(response).get("id").asLong();
  }

  private long unidade() throws Exception {
    return create("/api/v1/unidades", Map.of("nome", "Blumenau", "sigla", "BNU"));
  }

  private long professor() throws Exception {
    return create("/api/v1/professores", Map.of("nome", "Maria Silva"));
  }

  @Test
  void alunoNormalizaCpfEImpedeDuplicidade() throws Exception {
    long id =
        create(
            "/api/v1/alunos",
            Map.of("nome", "Ana Souza", "cpf", "529.982.247-25", "dataNascimento", "2000-01-01"));
    request("GET", "/api/v1/alunos/" + id, null).andExpect(jsonPath("$.cpf").value("52998224725"));

    request(
            "POST",
            "/api/v1/alunos",
            Map.of("nome", "Outra Ana", "cpf", "52998224725", "dataNascimento", "2000-01-01"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("RG0300"));
  }

  @Test
  void alunoNaoPermiteAlterarCpf() throws Exception {
    long id =
        create(
            "/api/v1/alunos",
            Map.of("nome", "Ana Souza", "cpf", "52998224725", "dataNascimento", "2000-01-01"));
    request(
            "PUT",
            "/api/v1/alunos/" + id,
            Map.of("nome", "Ana Silva", "cpf", "12345678909", "dataNascimento", "2001-01-01"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("RG0302"));
  }

  @Test
  void turmaExigeProfessorAtivoENomeUnicoNaUnidade() throws Exception {
    long professor = professor();
    long unidade = unidade();
    long turma =
        create(
            "/api/v1/turmas",
            Map.of("nome", "Java", "professorId", professor, "unidadeId", unidade, "cargaHoraria", 20));
    request("GET", "/api/v1/turmas/" + turma, null).andExpect(jsonPath("$.status").value("ATIVA"));

    request(
            "POST",
            "/api/v1/turmas",
            Map.of("nome", "java", "professorId", professor, "unidadeId", unidade, "cargaHoraria", 20))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("RG0402"));

    long professorInativo = professor();
    request("PATCH", "/api/v1/professores/" + professorInativo + "/inativar", null)
        .andExpect(status().isOk());
    request(
            "POST",
            "/api/v1/turmas",
            Map.of(
                "nome", "Spring",
                "professorId", professorInativo,
                "unidadeId", unidade,
                "cargaHoraria", 20))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("RG0400"));
  }

  @Test
  void turmaMantemUnidadeEPermiteInativacao() throws Exception {
    long professor = professor();
    long unidade = unidade();
    long turma =
        create(
            "/api/v1/turmas",
            Map.of("nome", "Java", "professorId", professor, "unidadeId", unidade, "cargaHoraria", 20));
    long outraUnidade = create("/api/v1/unidades", Map.of("nome", "Pomerode", "sigla", "POM"));

    request(
            "PUT",
            "/api/v1/turmas/" + turma,
            Map.of(
                "nome", "Java Avançado",
                "professorId", professor,
                "unidadeId", outraUnidade,
                "cargaHoraria", 20))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.codigo").value("RG0409"));

    request("PATCH", "/api/v1/turmas/" + turma + "/inativar", null)
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("INATIVA"));
  }
}
