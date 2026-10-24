package com.superdev.escolaprojeto.service;

import static com.superdev.escolaprojeto.repositories.FiltrosUnidade.*;
import static com.superdev.escolaprojeto.exceptions.RegraNegocio.check;

import com.superdev.escolaprojeto.repositories.FiltrosUnidade;
import com.superdev.escolaprojeto.dtos.professor.ProfessorAtualizarDto;
import com.superdev.escolaprojeto.dtos.professor.ProfessorCriarDto;
import com.superdev.escolaprojeto.dtos.professor.ProfessorRespostaDto;
import com.superdev.escolaprojeto.enums.ProfessorStatus;
import com.superdev.escolaprojeto.enums.TurmaStatus;
import com.superdev.escolaprojeto.exceptions.NaoEncontradoException;
import com.superdev.escolaprojeto.models.Professor;
import com.superdev.escolaprojeto.repositories.ProfessorRepository;
import com.superdev.escolaprojeto.repositories.TurmaRepository;
import java.time.*;
import java.util.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProfessorService {
  private final ProfessorRepository repository;
  private final TurmaRepository turmas;

  public ProfessorService(ProfessorRepository repository, TurmaRepository turmas) {
    this.repository = repository;
    this.turmas = turmas;
  }

  public Professor get(Long id) {
    return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Professor", id));
  }

  public ProfessorRespostaDto obterPorId(Long id) {
    return ProfessorRespostaDto.from(get(id));
  }

  public Page<ProfessorRespostaDto> listar(String nome, ProfessorStatus status, Pageable p) {
    return repository
        .findAll(
            FiltrosUnidade.<Professor>contains("nome", nome).and(equal("status", status)),
            PaginacaoService.check(p, "id", "nome", "status", "createdAt", "updatedAt"))
        .map(ProfessorRespostaDto::from);
  }

  @Transactional
  public ProfessorRespostaDto criar(ProfessorCriarDto r) {
    var e = Professor.builder().nome(r.nome()).status(ProfessorStatus.ATIVO).build();
    return ProfessorRespostaDto.from(repository.saveAndFlush(e));
  }

  @Transactional
  public ProfessorRespostaDto atualizar(Long id, ProfessorAtualizarDto r) {
    var e = get(id);
    check(
        r.status() == null || r.status() == e.getStatus(),
        "RG0203",
        "Use as operações inativar ou reativar para alterar o status.");
    e.setNome(r.nome());
    return ProfessorRespostaDto.from(repository.saveAndFlush(e));
  }

  @Transactional
  public ProfessorRespostaDto status(Long id, ProfessorStatus status) {
    var e = get(id);
    if (e.getStatus() == status) return ProfessorRespostaDto.from(e);
    if (status == ProfessorStatus.INATIVO)
      check(
          !turmas.existsByProfessorIdAndStatus(id, TurmaStatus.ATIVA),
          "RG0200",
          "Professor possui turmas ativas e não pode ser inativado.");
    e.setStatus(status);
    return ProfessorRespostaDto.from(repository.saveAndFlush(e));
  }

}
