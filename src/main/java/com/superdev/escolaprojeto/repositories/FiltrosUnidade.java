package com.superdev.escolaprojeto.repositories;

import org.springframework.data.jpa.domain.Specification;

public final class FiltrosUnidade {
  private FiltrosUnidade() {}

  public static <T> Specification<T> equal(String field, Object value) {
    return (root, q, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(field), value);
  }

  public static <T> Specification<T> contains(String field, String value) {
    return (root, q, cb) ->
        value == null
            ? cb.conjunction()
            : cb.like(
                cb.lower(root.get(field)),
                "%"
                    + value
                        .strip()
                        .toLowerCase(java.util.Locale.ROOT)
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_")
                    + "%",
                '\\');
  }

  public static <T> Specification<T> in(String field, java.util.Collection<Long> values) {
    return (r, q, c) -> r.get(field).in(values);
  }
}
