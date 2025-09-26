package com.github.javydreamercsw.tournament.manager.ui.encoders;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Converts between DateTime-objects and their String-representations */
public class LocalDateToStringEncoder implements Converter<String, LocalDate> {

  public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
  private static final long serialVersionUID = 7844897064123933288L;

  @Override
  public Result<LocalDate> convertToModel(String presentationValue, ValueContext context) {
    return Result.ok(LocalDate.parse(presentationValue, DATE_FORMAT));
  }

  @Override
  public String convertToPresentation(LocalDate modelValue, ValueContext context) {
    return modelValue == null ? null : modelValue.format(DATE_FORMAT);
  }
}
