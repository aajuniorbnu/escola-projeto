package com.superdev.escolaprojeto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UnidadeControllerTests {
    @Autowired MockMvc mvc;
    private final ObjectMapper json = new ObjectMapper();
    private final String rota = "/api/v1/unidades";

    private long criar() throws Exception {
        var response = mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("nome", "Blumenau", "sigla", " bnu "))))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.sigla").value("BNU"))
                .andExpect(jsonPath("$.createdAt").exists()).andReturn();
        return json.readTree(response.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test void RF0100_cadastrar() throws Exception { criar(); }
    @Test void RF0101_atualizar() throws Exception {
        long id = criar();
        mvc.perform(put(rota + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Blumenau Centro\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Blumenau Centro"));
    }
    @Test void RF0102_listarComPaginacao() throws Exception {
        criar();
        mvc.perform(get(rota).param("size", "1").param("sort", "nome,asc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(1));
    }
    @Test void RF0103_obterPorId() throws Exception {
        long id = criar();
        mvc.perform(get(rota + "/" + id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
        mvc.perform(get(rota + "/999999")).andExpect(status().isNotFound());
    }
    @Test void RF0104_filtrarNomeESigla() throws Exception {
        criar();
        mvc.perform(get(rota).param("nome", "BLU").param("sigla", "bn"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get(rota).param("nome", "inexistente"))
                .andExpect(jsonPath("$.totalElements").value(0));
    }
    @Test void RG0100_siglaDuplicada() throws Exception {
        criar();
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Outra Unidade\",\"sigla\":\"bnu\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.codigo").value("RG0100"));
    }
    @Test void RG0101_siglaImutavel() throws Exception {
        long id = criar();
        mvc.perform(put(rota + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Blumenau\",\"sigla\":\"SP\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.codigo").value("RG0101"));
    }
    @Test void RG0102_formato() throws Exception {
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"x\",\"sigla\":\"!\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.campos").isArray());
    }
    @Test void RG0103_nomeDuplicado() throws Exception {
        criar();
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"blumenau\",\"sigla\":\"SP\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.codigo").value("RG0103"));
    }
    @Test void naoDisponibilizaExclusao() throws Exception {
        long id = criar();
        mvc.perform(delete(rota + "/" + id)).andExpect(status().isMethodNotAllowed());
    }
    @Test void rejeitaPaginacaoInvalida() throws Exception {
        mvc.perform(get(rota).param("size", "0")).andExpect(status().isBadRequest());
        mvc.perform(get(rota).param("sort", "senha,asc")).andExpect(status().isBadRequest());
    }
    @Test void documentacaoOpenApi() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/unidades']").exists());
    }
}
