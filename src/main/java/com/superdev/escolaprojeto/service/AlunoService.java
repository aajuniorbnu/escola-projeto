package com.superdev.escolaprojeto.service;

import static com.superdev.escolaprojeto.exceptions.RegraNegocio.check;

import com.superdev.escolaprojeto.common.CpfValidator;
import com.superdev.escolaprojeto.dtos.alunos.AlunoAtualizarDto;
import com.superdev.escolaprojeto.dtos.alunos.AlunoCriarDto;
import com.superdev.escolaprojeto.dtos.alunos.AlunoRespostaDto;
import com.superdev.escolaprojeto.exceptions.NaoEncontradoException;
import com.superdev.escolaprojeto.models.Aluno;
import com.superdev.escolaprojeto.repositories.AlunoRepository;
import com.superdev.escolaprojeto.repositories.FiltrosUnidade;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlunoService {
  private final AlunoRepository repository;

  public AlunoService(AlunoRepository repository) {
    this.repository = repository;
  }

  public Aluno get(Long id) {
    return repository.findById(id).orElseThrow(() -> new NaoEncontradoException("Aluno", id));
  }

  public AlunoRespostaDto obterPorId(Long id) {
    return AlunoRespostaDto.from(get(id));
  }

  public Page<AlunoRespostaDto> listar(String nome, String cpf, Pageable pageable) {
    return repository
        .findAll(
            FiltrosUnidade.<Aluno>contains("nome", nome)
                .and(FiltrosUnidade.equal("cpf", CpfValidator.normalize(cpf))),
            PaginacaoService.check(
                pageable, "id", "nome", "cpf", "dataNascimento", "createdAt", "updatedAt"))
        .map(AlunoRespostaDto::from);
  }

  private void validarNascimento(LocalDate dataNascimento, boolean cadastro) {
    LocalDate hoje = LocalDate.now();
    check(
        !dataNascimento.isAfter(hoje) && !dataNascimento.isBefore(hoje.minusYears(120)),
        "RG0303",
        "Nascimento fora do intervalo permitido.");
    if (cadastro)
      check(
          !dataNascimento.isAfter(hoje.minusYears(16)),
          "RG0304",
          "Aluno deve ter pelo menos 16 anos completos.");
  }

  @Transactional
  public AlunoRespostaDto criar(AlunoCriarDto request) {
    validarNascimento(request.dataNascimento(), true);
    String cpf = CpfValidator.normalize(request.cpf());
    check(!repository.existsByCpf(cpf), "RG0300", "CPF já cadastrado.");
    Aluno aluno =
        Aluno.builder()
            .nome(request.nome())
            .cpf(cpf)
            .dataNascimento(request.dataNascimento())
            .build();
    return AlunoRespostaDto.from(repository.saveAndFlush(aluno));
  }

  @Transactional
  public AlunoRespostaDto atualizar(Long id, AlunoAtualizarDto request) {
    Aluno aluno = get(id);
    check(
        request.cpf() == null || aluno.getCpf().equals(CpfValidator.normalize(request.cpf())),
        "RG0302",
        "O CPF não pode ser alterado.");
    validarNascimento(request.dataNascimento(), false);
    aluno.setNome(request.nome());
    aluno.setDataNascimento(request.dataNascimento());
    return AlunoRespostaDto.from(repository.saveAndFlush(aluno));
  }
}
