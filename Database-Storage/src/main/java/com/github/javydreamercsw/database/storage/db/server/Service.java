package com.github.javydreamercsw.database.storage.db.server;

import java.util.List;

public abstract class Service<T>
{
  /**
   * Get all entries from this service, unfiltered.
   * @return all entries.
   */
  public abstract List<T> getAll();
}
