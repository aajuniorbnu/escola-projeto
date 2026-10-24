package com.superdev.escolaprojeto.exceptions;

import java.net.URI;
import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class TratadorDeErros extends ResponseEntityExceptionHandler {
  private ProblemDetail problem(HttpStatus status, String code, String message) {
    var p = ProblemDetail.forStatusAndDetail(status, message);
    p.setType(URI.create("urn:sup08:erro:" + code));
    p.setTitle(status.getReasonPhrase());
    p.setProperty("codigo", code);
    p.setProperty("mensagem", message);
    p.setProperty("campos", List.of());
    return p;
  }

  @ExceptionHandler(RegraNegocio.class)
  public ProblemDetail business(RegraNegocio e) {
    return problem(HttpStatus.CONFLICT, e.getCodigo(), e.getMessage());
  }

  @ExceptionHandler(NaoEncontradoException.class)
  public ProblemDetail notFound(NaoEncontradoException e) {
    return problem(HttpStatus.NOT_FOUND, "NAO_ENCONTRADO", e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail invalid(IllegalArgumentException e) {
    return problem(HttpStatus.BAD_REQUEST, "VALIDACAO", e.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    var p = problem(HttpStatus.BAD_REQUEST, "VALIDACAO", "Verifique os campos informados.");
    p.setProperty(
        "campos",
        e.getBindingResult().getFieldErrors().stream()
            .map(
                f ->
                    Map.of(
                        "campo",
                        f.getField(),
                        "mensagem",
                        Objects.toString(f.getDefaultMessage(), "Inválido")))
            .toList());
    return handleExceptionInternal(e, p, headers, status, request);
  }

  @Override
  protected ResponseEntity<Object> handleExceptionInternal(
      Exception e, Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    if (!(body instanceof ProblemDetail p
        && p.getProperties() != null
        && p.getProperties().containsKey("codigo"))) {
      body =
          problem(
              HttpStatus.valueOf(status.value()),
              status.value() == 404 ? "NAO_ENCONTRADO" : "REQUISICAO_INVALIDA",
              switch (status.value()) {
                case 400 ->
                    "JSON, parâmetros ou formato inválidos. Campos desconhecidos não são aceitos.";
                case 404 -> "Recurso não encontrado.";
                case 405 -> "Operação não disponível para este recurso.";
                default -> "Não foi possível processar a requisição.";
              });
    }
    return super.handleExceptionInternal(e, body, headers, status, request);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail integrity(DataIntegrityViolationException e) {
    var message = Objects.toString(e.getMostSpecificCause().getMessage(), "");
    var codes =
        Map.ofEntries(
            Map.entry("uk_unidades_sigla", "RG0100"),
            Map.entry("uk_unidades_nome", "RG0103"),
            Map.entry("uk_alunos_cpf", "RG0300"),
            Map.entry("uk_turmas_nome", "RG0402"));
    var code =
        codes.entrySet().stream()
            .filter(v -> message.contains(v.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .orElse("INTEGRIDADE");
    return problem(HttpStatus.CONFLICT, code, "Operação viola uma restrição de integridade.");
  }
}
