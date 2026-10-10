package com.superdev.escolaprojeto.service;

import java.util.Set;
import org.springframework.data.domain.*;

public final class PaginacaoService {
  private PaginacaoService() {}

  public static Pageable check(Pageable p, String... fields) {
    var allowed = Set.of(fields);
    if (p.getPageNumber() < 0 || p.getPageSize() < 1 || p.getPageSize() > 100)
      throw new IllegalArgumentException("Paginação inválida; size deve estar entre 1 e 100.");
    for (var order : p.getSort())
      if (!allowed.contains(order.getProperty()))
        throw new IllegalArgumentException("Ordenação inválida: " + order.getProperty());
    var sort = p.getSort().isSorted() ? p.getSort() : Sort.by("id");
    if (sort.getOrderFor("id") == null) sort = sort.and(Sort.by("id"));
    return PageRequest.of(p.getPageNumber(), p.getPageSize(), sort);
  }
}
