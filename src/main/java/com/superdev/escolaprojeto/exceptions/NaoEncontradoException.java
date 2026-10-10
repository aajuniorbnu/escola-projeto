package com.superdev.escolaprojeto.exceptions;

public class NaoEncontradoException extends RuntimeException {
  public NaoEncontradoException(String tipo, Long id) {
    super(tipo + " não encontrado: " + id);
  }
}
