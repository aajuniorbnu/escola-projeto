package com.superdev.escolaprojeto.common;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<Cpf, String> {
  public static String normalize(String value) {
    return value == null ? null : value.replaceAll("\\D", "");
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    String cpf = normalize(value);
    if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) return false;
    return digit(cpf, 9, 10) == cpf.charAt(9) - '0' && digit(cpf, 10, 11) == cpf.charAt(10) - '0';
  }

  private int digit(String cpf, int length, int weight) {
    int sum = 0;
    for (int i = 0; i < length; i++) sum += (cpf.charAt(i) - '0') * (weight - i);
    int remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  }
}
