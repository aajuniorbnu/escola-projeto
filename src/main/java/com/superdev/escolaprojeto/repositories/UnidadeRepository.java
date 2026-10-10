package com.superdev.escolaprojeto.repositories;

import com.superdev.escolaprojeto.models.Unidade;
import org.springframework.data.jpa.repository.*;

public interface UnidadeRepository
    extends JpaRepository<Unidade, Long>, JpaSpecificationExecutor<Unidade> {
  boolean existsBySiglaIgnoreCase(String sigla);

  boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
