package com.superdev.escolaprojeto.dtos.unidade;

import jakarta.validation.constraints.*;
import java.time.*;

public record UnidadeCriarDto(
    @NotBlank @Size(min = 3, max = 120) String nome,
    @NotBlank @Pattern(regexp = "[A-Za-z0-9]{2,10}") String sigla) {
  public UnidadeCriarDto {
    nome = nome == null ? null : nome.strip();
    sigla = sigla == null ? null : sigla.strip();
  }
}
