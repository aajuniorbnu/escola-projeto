package com.superdev.escolaprojeto.dtos.alunos;

import com.superdev.escolaprojeto.models.Aluno;

public record AlunoRespostaDto(
        Long id,
        String nome,
        String cpf,
        java.time.LocalDate dataNascimento,
        java.time.OffsetDateTime createdAt,
        java.time.OffsetDateTime updatedAt) {
    public static AlunoRespostaDto from(Aluno e) {
        return new AlunoRespostaDto(
                e.getId(),
                e.getNome(),
                e.getCpf(),
                e.getDataNascimento(),
                e.getCreatedAt() == null ? null : e.getCreatedAt().atOffset(java.time.ZoneOffset.UTC),
                e.getUpdatedAt() == null ? null : e.getUpdatedAt().atOffset(java.time.ZoneOffset.UTC));
    }
}
