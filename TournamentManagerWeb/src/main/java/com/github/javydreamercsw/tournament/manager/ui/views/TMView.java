package com.github.javydreamercsw.tournament.manager.ui.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.server.VaadinService;

public abstract class TMView extends VerticalLayout
        implements AfterNavigationObserver
{
  private static final long serialVersionUID = -877132739053009292L;
  public static final String CURRENT_GAME = "Current Game";

  public TMView()
  {
  }

  @Override
  public void afterNavigation(AfterNavigationEvent ane)
  {
    updateView();
  }

  /**
   * Update the view.
   */
  public abstract void updateView();

  protected final void saveValue(String key, String value)
  {
    VaadinService.getCurrentRequest().getWrappedSession()
            .setAttribute(key, value);
}

  protected final Object getValue(String key)
  {
    return VaadinService.getCurrentRequest().getWrappedSession()
            .getAttribute(key);
  }

  protected final void removeValue(String key)
  {
    VaadinService.getCurrentRequest().getWrappedSession().removeAttribute(key);
  }
}
