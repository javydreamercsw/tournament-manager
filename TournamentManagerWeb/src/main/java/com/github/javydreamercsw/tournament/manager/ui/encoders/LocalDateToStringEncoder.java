package com.github.javydreamercsw.tournament.manager.ui.encoders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.templatemodel.ModelEncoder;

/**
 * Converts between DateTime-objects and their String-representations
 *
 */
public class LocalDateToStringEncoder
        implements ModelEncoder<LocalDate, String>
{

  public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
          .ofPattern("MM/dd/yyyy");
  private static final long serialVersionUID = 7844897064123933288L;

  @Override
  public LocalDate decode(String presentationValue)
  {
    return LocalDate.parse(presentationValue, DATE_FORMAT);
  }

  @Override
  public String encode(LocalDate modelValue)
  {
    return modelValue == null ? null : modelValue.format(DATE_FORMAT);
  }
}
