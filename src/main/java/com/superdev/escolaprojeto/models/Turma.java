package com.superdev.escolaprojeto.models;

import com.superdev.escolaprojeto.enums.TurmaStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "turmas")
public class Turma extends ModeloBase {
    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "id_professor", nullable = false)
    private Long professorId;

    @Column(name = "id_unidade", nullable = false, updatable = false)
    private Long unidadeId;

    @Column(name = "carga_horaria", nullable = false)
    private Integer cargaHoraria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TurmaStatus status;
}
