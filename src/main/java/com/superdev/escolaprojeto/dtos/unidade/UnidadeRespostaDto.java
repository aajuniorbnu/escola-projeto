package com.superdev.escolaprojeto.dtos.unidade;

import com.superdev.escolaprojeto.models.Unidade;

public record UnidadeRespostaDto(
    Long id,
    String nome,
    String sigla,
    java.time.OffsetDateTime createdAt,
    java.time.OffsetDateTime updatedAt) {
  public static UnidadeRespostaDto from(Unidade e) {
    return new UnidadeRespostaDto(
        e.getId(),
        e.getNome(),
        e.getSigla(),
        e.getCreatedAt() == null ? null : e.getCreatedAt().atOffset(java.time.ZoneOffset.UTC),
        e.getUpdatedAt() == null ? null : e.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC));
  }
}
