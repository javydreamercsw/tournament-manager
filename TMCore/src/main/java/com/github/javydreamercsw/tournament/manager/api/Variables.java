package com.github.javydreamercsw.tournament.manager.api;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public enum Variables {
  PLAYER_NAME("Player Name"),
  WINS("Wins"),
  LOSSES("Losses"),
  DRAWS("Draws");
  private final String name;

  Variables(String name) {
    this.name = name;
  }

  public String getDisplayName() {
    return name;
  }
}
