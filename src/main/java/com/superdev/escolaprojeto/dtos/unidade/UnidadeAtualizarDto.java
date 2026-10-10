package com.superdev.escolaprojeto.dtos.unidade;

import jakarta.validation.constraints.*;
import java.time.*;

public record UnidadeAtualizarDto(@NotBlank @Size(min = 3, max = 120) String nome, String sigla) {
  public UnidadeAtualizarDto {
    nome = nome == null ? null : nome.strip();
    sigla = sigla == null ? null : sigla.strip();
  }
}
