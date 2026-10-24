package com.superdev.escolaprojeto.service;

import static com.superdev.escolaprojeto.exceptions.RegraNegocio.check;

import com.superdev.escolaprojeto.dtos.turma.TurmaAtualizarDto;
import com.superdev.escolaprojeto.dtos.turma.TurmaCriarDto;
import com.superdev.escolaprojeto.dtos.turma.TurmaRespostaDto;
import com.superdev.escolaprojeto.enums.ProfessorStatus;
import com.superdev.escolaprojeto.enums.TurmaStatus;
import com.superdev.escolaprojeto.exceptions.NaoEncontradoException;
import com.superdev.escolaprojeto.models.Turma;
import com.superdev.escolaprojeto.repositories.FiltrosUnidade;
import com.superdev.escolaprojeto.repositories.ProfessorRepository;
import com.superdev.escolaprojeto.repositories.TurmaRepository;
import com.superdev.escolaprojeto.repositories.UnidadeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TurmaService {
  private final TurmaRepository repository;
  private final ProfessorRepository professores;
  private final UnidadeRepository unidades;

  public TurmaService(
      TurmaRepository repository, ProfessorRepository professores, UnidadeRepository unidades) {
    this.repository = repository;
    this.professores = professores;
    this.unidades = unidades;
  }

  public Turma get(Long id) {
    return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Turma", id));
  }

  public TurmaRespostaDto obterPorId(Long id) {
    return TurmaRespostaDto.from(get(id));
  }

  public Page<TurmaRespostaDto> listar(
      String nome, Long professorId, Long unidadeId, TurmaStatus status, Pageable pageable) {
    return repository
        .findAll(
            FiltrosUnidade.<Turma>contains("nome", nome)
                .and(FiltrosUnidade.equal("professorId", professorId))
                .and(FiltrosUnidade.equal("unidadeId", unidadeId))
                .and(FiltrosUnidade.equal("status", status)),
            PaginacaoService.check(
                pageable,
                "id",
                "nome",
                "professorId",
                "unidadeId",
                "cargaHoraria",
                "status",
                "createdAt",
                "updatedAt"))
        .map(TurmaRespostaDto::from);
  }

  private void professorAtivo(Long professorId, String code) {
    var professor =
        professores
            .findById(professorId)
            .orElseThrow(() -> new NaoEncontradoException("Professor", professorId));
    check(professor.getStatus() == ProfessorStatus.ATIVO, code, "Professor deve estar ativo.");
  }

  private void nomeUnico(String nome, Long unidadeId, Long id) {
    check(
        !repository.existsByNomeIgnoreCaseAndUnidadeIdAndIdNot(nome, unidadeId, id),
        "RG0402",
        "Nome da turma já utilizado nesta unidade.");
  }

  @Transactional
  public TurmaRespostaDto criar(TurmaCriarDto request) {
    professorAtivo(request.professorId(), "RG0400");
    unidades
        .findById(request.unidadeId())
        .orElseThrow(() -> new NaoEncontradoException("Unidade", request.unidadeId()));
    nomeUnico(request.nome(), request.unidadeId(), -1L);
    Turma turma =
        Turma.builder()
            .nome(request.nome())
            .professorId(request.professorId())
            .unidadeId(request.unidadeId())
            .cargaHoraria(request.cargaHoraria())
            .status(TurmaStatus.ATIVA)
            .build();
    return TurmaRespostaDto.from(repository.saveAndFlush(turma));
  }

  @Transactional
  public TurmaRespostaDto atualizar(Long id, TurmaAtualizarDto request) {
    Turma turma = get(id);
    check(
        request.unidadeId() == null || request.unidadeId().equals(turma.getUnidadeId()),
        "RG0409",
        "Unidade da turma é imutável.");
    check(
        turma.getStatus() != TurmaStatus.CONCLUIDA,
        "RG0404",
        "Turma concluída não permite alterações.");
    nomeUnico(request.nome(), turma.getUnidadeId(), id);
    if (!turma.getProfessorId().equals(request.professorId())) professorAtivo(request.professorId(), "RG0404");
    turma.setNome(request.nome());
    turma.setProfessorId(request.professorId());
    turma.setCargaHoraria(request.cargaHoraria());
    return TurmaRespostaDto.from(repository.saveAndFlush(turma));
  }

  @Transactional
  public TurmaRespostaDto inativar(Long id) {
    Turma turma = get(id);
    if (turma.getStatus() == TurmaStatus.INATIVA) return TurmaRespostaDto.from(turma);
    check(turma.getStatus() == TurmaStatus.ATIVA, "RG0405", "Turma concluída é um estado final.");
    turma.setStatus(TurmaStatus.INATIVA);
    return TurmaRespostaDto.from(repository.saveAndFlush(turma));
  }
}
