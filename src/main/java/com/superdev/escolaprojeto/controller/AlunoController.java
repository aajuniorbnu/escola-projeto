package com.superdev.escolaprojeto.controller;

import com.superdev.escolaprojeto.dtos.PaginaRespostaDto;
import com.superdev.escolaprojeto.dtos.alunos.AlunoAtualizarDto;
import com.superdev.escolaprojeto.dtos.alunos.AlunoCriarDto;
import com.superdev.escolaprojeto.dtos.alunos.AlunoRespostaDto;
import com.superdev.escolaprojeto.service.AlunoService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alunos")
public class AlunoController {
  private final AlunoService service;

  public AlunoController(AlunoService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<AlunoRespostaDto> criar(@Valid @RequestBody AlunoCriarDto request) {
    AlunoRespostaDto result = service.criar(request);
    return ResponseEntity.created(URI.create("/api/v1/alunos/" + result.id())).body(result);
  }

  @PutMapping("/{id}")
  public AlunoRespostaDto atualizar(
      @PathVariable Long id, @Valid @RequestBody AlunoAtualizarDto request) {
    return service.atualizar(id, request);
  }

  @GetMapping("/{id}")
  public AlunoRespostaDto obterPorId(@PathVariable Long id) {
    return service.obterPorId(id);
  }

  @GetMapping
  public PaginaRespostaDto<AlunoRespostaDto> listar(
      @RequestParam(required = false) String nome,
      @RequestParam(required = false) String cpf,
      @PageableDefault(size = 20) Pageable pageable) {
    return PaginaRespostaDto.of(service.listar(nome, cpf, pageable));
  }
}
