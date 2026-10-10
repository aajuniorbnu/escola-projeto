package com.superdev.escolaprojeto.exceptions;

public class RegraNegocio extends RuntimeException {
  private final String codigo;

  public String getCodigo() {
    return codigo;
  }

  public RegraNegocio(String codigo, String mensagem) {
    super(mensagem);
    this.codigo = codigo;
  }

  public static void check(boolean condition, String codigo, String mensagem) {
    if (!condition) throw new RegraNegocio(codigo, mensagem);
  }
}
