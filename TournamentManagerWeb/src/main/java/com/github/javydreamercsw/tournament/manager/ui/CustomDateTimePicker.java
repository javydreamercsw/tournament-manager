package com.github.javydreamercsw.tournament.manager.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.timepicker.TimePicker;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class CustomDateTimePicker extends CustomField<LocalDateTime>
{
  private static final long serialVersionUID = 2372640494766739387L;
  private final DatePicker datePicker = new DatePicker();
  private final TimePicker timePicker = new TimePicker();

  public CustomDateTimePicker()
  {
    setLabel("Start datetime");
    add(datePicker, timePicker);
  }

  @Override
  protected LocalDateTime generateModelValue()
  {
    final LocalDate date = datePicker.getValue();
    final LocalTime time = timePicker.getValue();
    return date != null && time != null
            ? LocalDateTime.of(date, time)
            : null;
  }

  @Override
  protected void setPresentationValue(
          LocalDateTime newPresentationValue)
  {
    datePicker.setValue(newPresentationValue != null
            ? newPresentationValue.toLocalDate()
            : null);
    timePicker.setValue(newPresentationValue != null
            ? newPresentationValue.toLocalTime()
            : null);

  }

  public void setMin(LocalDate minDate)
  {
    datePicker.setMin(minDate);
  }
}
