package com.superdev.escolaprojeto.controller;

import com.superdev.escolaprojeto.dtos.PaginaRespostaDto;
import com.superdev.escolaprojeto.dtos.professor.ProfessorAtualizarDto;
import com.superdev.escolaprojeto.dtos.professor.ProfessorCriarDto;
import com.superdev.escolaprojeto.dtos.professor.ProfessorRespostaDto;
import com.superdev.escolaprojeto.service.ProfessorService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/professores")
public class ProfessorController {
  private final ProfessorService service;

  public ProfessorController(ProfessorService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<ProfessorRespostaDto> criar(@Valid @RequestBody ProfessorCriarDto r) {
    var result = service.criar(r);
    return ResponseEntity.created(URI.create("/api/v1/professores/" + result.id())).body(result);
  }

  @PutMapping("/{id}")
  public ProfessorRespostaDto atualizar(
      @PathVariable Long id, @Valid @RequestBody ProfessorAtualizarDto r) {
    return service.atualizar(id, r);
  }

  @GetMapping("/{id}")
  public ProfessorRespostaDto obterPorId(@PathVariable Long id) {
    return service.obterPorId(id);
  }

  @GetMapping
  public PaginaRespostaDto<ProfessorRespostaDto> listar(
      @RequestParam(required = false) String nome,
      @RequestParam(required = false) com.superdev.escolaprojeto.enums.ProfessorStatus status,
      @PageableDefault(size = 20) Pageable p) {
    return PaginaRespostaDto.of(service.listar(nome, status, p));
  }

  @PatchMapping("/{id}/inativar")
  public ProfessorRespostaDto inativar(@PathVariable Long id) {
    return service.status(id, com.superdev.escolaprojeto.enums.ProfessorStatus.INATIVO);
  }

  @PatchMapping("/{id}/reativar")
  public ProfessorRespostaDto reativar(@PathVariable Long id) {
    return service.status(id, com.superdev.escolaprojeto.enums.ProfessorStatus.ATIVO);
  }

}
