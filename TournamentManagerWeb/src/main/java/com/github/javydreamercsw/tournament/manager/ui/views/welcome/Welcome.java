package com.github.javydreamercsw.tournament.manager.ui.views.welcome;

import org.vaadin.maxime.MarkdownArea;

import com.github.javydreamercsw.tournament.manager.ui.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Tournament List")
public class Welcome extends VerticalLayout
{
  private static final long serialVersionUID = 1252548231807630022L;

  public Welcome()
  {
    addClassName("welcome-list");
    setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
    MarkdownArea mda = new MarkdownArea("Hello world !");
    add(mda);
  }
}
