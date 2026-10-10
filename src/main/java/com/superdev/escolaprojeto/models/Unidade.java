package com.superdev.escolaprojeto.models;

import com.superdev.escolaprojeto.models.ModeloBase;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "unidades")
public class Unidade extends ModeloBase {
  @Column(nullable = false, length = 120)
  private String nome;

  @Column(nullable = false, length = 10)
  private String sigla;
}
