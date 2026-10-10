package com.superdev.escolaprojeto.models;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Getter
@Setter
@MappedSuperclass
public abstract class ModeloBase {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @PrePersist
  protected void create() {
    createdAt = Instant.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  protected void update() {
    updatedAt = Instant.now();
  }
}
