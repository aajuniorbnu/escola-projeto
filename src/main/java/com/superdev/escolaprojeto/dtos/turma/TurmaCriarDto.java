package com.superdev.escolaprojeto.dtos.turma;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TurmaCriarDto(
    @NotBlank @Size(min = 3, max = 120) String nome,
    @NotNull @Positive Long professorId,
    @NotNull @Positive Long unidadeId,
    @NotNull @Min(1) @Max(9999) Integer cargaHoraria) {
  public TurmaCriarDto {
    nome = nome == null ? null : nome.strip();
  }
}
