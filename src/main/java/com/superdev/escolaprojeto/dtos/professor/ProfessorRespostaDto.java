package com.superdev.escolaprojeto.dtos.professor;

import com.superdev.escolaprojeto.enums.ProfessorStatus;
import com.superdev.escolaprojeto.models.Professor;

public record ProfessorRespostaDto(
    Long id,
    String nome,
    ProfessorStatus status,
    java.time.OffsetDateTime createdAt,
    java.time.OffsetDateTime updatedAt) {
  public static ProfessorRespostaDto from(Professor e) {
    return new ProfessorRespostaDto(
        e.getId(),
        e.getNome(),
        e.getStatus(),
        e.getCreatedAt() == null ? null : e.getCreatedAt().atOffset(java.time.ZoneOffset.UTC),
        e.getUpdatedAt() == null ? null : e.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC));
  }
}
