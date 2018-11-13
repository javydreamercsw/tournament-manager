package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

class StaticData
{
  static final List<String> FORMATS = new ArrayList<>();

  static
  {
    Stream.of("Commander",
            "Beatdown",
            "Legacy",
            "Standard",
            "Draft")
            .forEach(name -> FORMATS.add(name));
  }

  /**
   * This class is not meant to be instantiated.
   */
  private StaticData()
  {
  }
}
