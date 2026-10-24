package com.superdev.escolaprojeto.controller;

import com.superdev.escolaprojeto.dtos.PaginaRespostaDto;
import com.superdev.escolaprojeto.dtos.turma.TurmaAtualizarDto;
import com.superdev.escolaprojeto.dtos.turma.TurmaCriarDto;
import com.superdev.escolaprojeto.dtos.turma.TurmaRespostaDto;
import com.superdev.escolaprojeto.enums.TurmaStatus;
import com.superdev.escolaprojeto.service.TurmaService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/turmas")
public class TurmaController {
  private final TurmaService service;

  public TurmaController(TurmaService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<TurmaRespostaDto> criar(@Valid @RequestBody TurmaCriarDto request) {
    TurmaRespostaDto result = service.criar(request);
    return ResponseEntity.created(URI.create("/api/v1/turmas/" + result.id())).body(result);
  }

  @PutMapping("/{id}")
  public TurmaRespostaDto atualizar(
      @PathVariable Long id, @Valid @RequestBody TurmaAtualizarDto request) {
    return service.atualizar(id, request);
  }

  @GetMapping("/{id}")
  public TurmaRespostaDto obterPorId(@PathVariable Long id) {
    return service.obterPorId(id);
  }

  @GetMapping
  public PaginaRespostaDto<TurmaRespostaDto> listar(
      @RequestParam(required = false) String nome,
      @RequestParam(required = false) Long professorId,
      @RequestParam(required = false) Long unidadeId,
      @RequestParam(required = false) TurmaStatus status,
      @PageableDefault(size = 20) Pageable pageable) {
    return PaginaRespostaDto.of(service.listar(nome, professorId, unidadeId, status, pageable));
  }

  @PatchMapping("/{id}/inativar")
  public TurmaRespostaDto inativar(@PathVariable Long id) {
    return service.inativar(id);
  }
}
