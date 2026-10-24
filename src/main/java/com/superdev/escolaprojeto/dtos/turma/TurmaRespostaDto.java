package com.superdev.escolaprojeto.dtos.turma;

import com.superdev.escolaprojeto.enums.TurmaStatus;
import com.superdev.escolaprojeto.models.Turma;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public record TurmaRespostaDto(
    Long id,
    String nome,
    Long professorId,
    Long unidadeId,
    Integer cargaHoraria,
    TurmaStatus status,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {
  public static TurmaRespostaDto from(Turma entity) {
    return new TurmaRespostaDto(
        entity.getId(),
        entity.getNome(),
        entity.getProfessorId(),
        entity.getUnidadeId(),
        entity.getCargaHoraria(),
        entity.getStatus(),
        entity.getCreatedAt() == null ? null : entity.getCreatedAt().atOffset(ZoneOffset.UTC),
        entity.getUpdatedAt() == null ? null : entity.getUpdatedAt().atOffset(ZoneOffset.UTC));
  }
}
