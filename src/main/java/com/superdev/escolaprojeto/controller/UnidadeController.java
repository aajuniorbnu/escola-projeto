package com.superdev.escolaprojeto.controller;

import com.superdev.escolaprojeto.dtos.PaginaRespostaDto;
import com.superdev.escolaprojeto.dtos.unidade.UnidadeAtualizarDto;
import com.superdev.escolaprojeto.dtos.unidade.UnidadeCriarDto;
import com.superdev.escolaprojeto.dtos.unidade.UnidadeRespostaDto;
import com.superdev.escolaprojeto.service.UnidadeService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/unidades")
public class UnidadeController {
  private final UnidadeService service;

  public UnidadeController(UnidadeService service) {
    this.service = service;
  }

  @PostMapping
  public ResponseEntity<UnidadeRespostaDto> criar(@Valid @RequestBody UnidadeCriarDto r) {
    var result = service.criar(r);
    return ResponseEntity.created(URI.create("/api/v1/unidades/" + result.id())).body(result);
  }

  @PutMapping("/{id}")
  public UnidadeRespostaDto atualizar(
      @PathVariable Long id, @Valid @RequestBody UnidadeAtualizarDto r) {
    return service.atualizar(id, r);
  }

  @GetMapping("/{id}")
  public UnidadeRespostaDto obterPorId(@PathVariable Long id) {
    return service.obterPorId(id);
  }

  @GetMapping
  public PaginaRespostaDto<UnidadeRespostaDto> listar(
      @RequestParam(required = false) String nome,
      @RequestParam(required = false) String sigla,
      @PageableDefault(size = 20) Pageable p) {
    return PaginaRespostaDto.of(service.listar(nome, sigla, p));
  }
}
