package com.superdev.escolaprojeto.models;

import com.superdev.escolaprojeto.models.ModeloBase;
import com.superdev.escolaprojeto.enums.ProfessorStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "professores")
public class Professor extends ModeloBase {
  @Column(nullable = false, length = 150)
  private String nome;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProfessorStatus status;
}
