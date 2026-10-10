package com.superdev.escolaprojeto.repositories;

import com.superdev.escolaprojeto.models.Professor;
import org.springframework.data.jpa.repository.*;

public interface ProfessorRepository
    extends JpaRepository<Professor, Long>, JpaSpecificationExecutor<Professor> {}
