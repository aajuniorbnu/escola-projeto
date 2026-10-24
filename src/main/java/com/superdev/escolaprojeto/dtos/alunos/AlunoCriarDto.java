package com.superdev.escolaprojeto.dtos.alunos;

import com.superdev.escolaprojeto.common.Cpf;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlunoCriarDto(
    @NotBlank @Size(min = 3, max = 150) String nome,
    @NotBlank @Cpf String cpf,
    @NotNull @PastOrPresent LocalDate dataNascimento) {
  public AlunoCriarDto {
    nome = nome == null ? null : nome.strip();
    cpf = cpf == null ? null : cpf.strip();
  }
}
