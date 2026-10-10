package com.superdev.escolaprojeto.service;

import static com.superdev.escolaprojeto.repositories.FiltrosUnidade.*;
import static com.superdev.escolaprojeto.exceptions.RegraNegocio.check;

import com.superdev.escolaprojeto.repositories.FiltrosUnidade;
import com.superdev.escolaprojeto.service.PaginacaoService;
import com.superdev.escolaprojeto.dtos.unidade.UnidadeAtualizarDto;
import com.superdev.escolaprojeto.dtos.unidade.UnidadeCriarDto;
import com.superdev.escolaprojeto.dtos.unidade.UnidadeRespostaDto;
import com.superdev.escolaprojeto.exceptions.NaoEncontradoException;
import com.superdev.escolaprojeto.models.Unidade;
import com.superdev.escolaprojeto.repositories.UnidadeRepository;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UnidadeService {
  private final UnidadeRepository repository;

  public UnidadeService(UnidadeRepository repository) {
    this.repository = repository;
  }

  public Unidade get(Long id) {
    return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Unidade", id));
  }

  public UnidadeRespostaDto obterPorId(Long id) {
    return UnidadeRespostaDto.from(get(id));
  }

  public Page<UnidadeRespostaDto> listar(String nome, String sigla, Pageable p) {
    return repository
        .findAll(
            FiltrosUnidade.<Unidade>contains("nome", nome).and(contains("sigla", sigla)),
            PaginacaoService.check(p, "id", "nome", "sigla", "createdAt", "updatedAt"))
        .map(UnidadeRespostaDto::from);
  }

  @Transactional
  public UnidadeRespostaDto criar(UnidadeCriarDto r) {
    String sigla = r.sigla().toUpperCase(Locale.ROOT);
    check(!repository.existsBySiglaIgnoreCase(sigla), "RG0100", "Sigla já cadastrada.");
    check(
        !repository.existsByNomeIgnoreCaseAndIdNot(r.nome(), -1L),
        "RG0103",
        "Nome de unidade já cadastrado.");
    var e = Unidade.builder().nome(r.nome()).sigla(sigla).build();
    return UnidadeRespostaDto.from(repository.saveAndFlush(e));
  }

  @Transactional
  public UnidadeRespostaDto atualizar(Long id, UnidadeAtualizarDto r) {
    var e = get(id);
    check(
        r.sigla() == null || e.getSigla().equalsIgnoreCase(r.sigla()),
        "RG0101",
        "A sigla não pode ser alterada.");
    check(
        !repository.existsByNomeIgnoreCaseAndIdNot(r.nome(), id),
        "RG0103",
        "Nome de unidade já cadastrado.");
    e.setNome(r.nome());
    return UnidadeRespostaDto.from(repository.saveAndFlush(e));
  }
}
