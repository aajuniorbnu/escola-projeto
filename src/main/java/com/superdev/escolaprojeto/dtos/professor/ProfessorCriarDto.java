package com.superdev.escolaprojeto.dtos.professor;

import jakarta.validation.constraints.*;
import java.time.*;

public record ProfessorCriarDto(@NotBlank @Size(min = 3, max = 150) String nome) {
  public ProfessorCriarDto {
    nome = nome == null ? null : nome.strip();
  }
}
