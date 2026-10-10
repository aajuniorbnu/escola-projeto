package com.superdev.escolaprojeto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfessorControllerTests {
    @Autowired MockMvc mvc;
    private final String rota = "/api/v1/professores";

    private long criar() throws Exception {
        var response = mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"  Professor Teste  \"}"))
                .andExpect(status().isCreated()).andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.nome").value("Professor Teste"))
                .andExpect(jsonPath("$.status").value("ATIVO"))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists()).andReturn();
        return new ObjectMapper().readTree(response.getResponse().getContentAsString()).get("id").asLong();
    }

    @Test void cadastrarConsultarAtualizarEFiltrar() throws Exception {
        long id = criar();
        mvc.perform(get(rota + "/" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
        mvc.perform(put(rota + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Professor Atualizado\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nome").value("Professor Atualizado"));
        mvc.perform(get(rota).param("nome", "ATUALIZADO").param("status", "ATIVO").param("size", "1").param("sort", "nome,asc"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get(rota).param("status", "INATIVO")).andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test void inativarEReativarComIdempotencia() throws Exception {
        long id = criar();
        for (int i = 0; i < 2; i++) {
            mvc.perform(patch(rota + "/" + id + "/inativar"))
                    .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("INATIVO"));
        }
        mvc.perform(patch(rota + "/" + id + "/reativar"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("ATIVO"));
    }

    @Test void rejeitarStatusNaEdicaoENomeInvalido() throws Exception {
        long id = criar();
        mvc.perform(put(rota + "/" + id).contentType(MediaType.APPLICATION_JSON)
                .content("{\"nome\":\"Professor Teste\",\"status\":\"INATIVO\"}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.codigo").value("RG0203"));
        mvc.perform(post(rota).contentType(MediaType.APPLICATION_JSON).content("{\"nome\":\"  \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get(rota + "/999999")).andExpect(status().isNotFound());
        mvc.perform(delete(rota + "/" + id)).andExpect(status().isMethodNotAllowed());
    }

    @Test void validarPaginacaoEDocumentacao() throws Exception {
        mvc.perform(get(rota).param("size", "0")).andExpect(status().isBadRequest());
        mvc.perform(get(rota).param("sort", "senha,asc")).andExpect(status().isBadRequest());
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/professores']").exists());
    }
}
