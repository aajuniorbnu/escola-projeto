package com.superdev.escolaprojeto.dtos.professor;

import com.superdev.escolaprojeto.enums.ProfessorStatus;
import jakarta.validation.constraints.*;
import java.time.*;

public record ProfessorAtualizarDto(
    @NotBlank @Size(min = 3, max = 150) String nome, ProfessorStatus status) {
  public ProfessorAtualizarDto {
    nome = nome == null ? null : nome.strip();
  }
}
