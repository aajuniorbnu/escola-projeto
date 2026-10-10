package com.superdev.escolaprojeto.dtos;

import java.util.List;
import org.springframework.data.domain.Page;

public record PaginaRespostaDto<T>(
    List<T> content, int page, int size, long totalElements, int totalPages) {
  public static <T> PaginaRespostaDto<T> of(Page<T> page) {
    return new PaginaRespostaDto<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages());
  }
}
