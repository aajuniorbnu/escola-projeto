package com.superdev.escolaprojeto.repositories;

import com.superdev.escolaprojeto.enums.TurmaStatus;
import com.superdev.escolaprojeto.models.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TurmaRepository
    extends JpaRepository<Turma, Long>, JpaSpecificationExecutor<Turma> {
  boolean existsByNomeIgnoreCaseAndUnidadeIdAndIdNot(String nome, Long unidadeId, Long id);

  boolean existsByProfessorIdAndStatus(Long professorId, TurmaStatus status);
}
