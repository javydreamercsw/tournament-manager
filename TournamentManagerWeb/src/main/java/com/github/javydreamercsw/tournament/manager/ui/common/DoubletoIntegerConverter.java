package com.github.javydreamercsw.tournament.manager.ui.common;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class DoubletoIntegerConverter implements Converter<Double, Integer>
{
  public DoubletoIntegerConverter()
  {
  }
  private static final long serialVersionUID = 1620961827821087009L;

  @Override
  public Result<Integer> convertToModel(Double prsntn, ValueContext vc)
  {
    return Result.ok(prsntn.intValue());
  }

  @Override
  public Double convertToPresentation(Integer model, ValueContext vc)
  {
    return Double.valueOf(model);
  }
}
