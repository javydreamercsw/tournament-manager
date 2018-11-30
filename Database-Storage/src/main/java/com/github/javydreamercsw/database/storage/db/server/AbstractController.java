package com.github.javydreamercsw.database.storage.db.server;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * This avoids synchronization issues where each service had their own
 * EntityManager.
 */
public class AbstractController
{
  private EntityManagerFactory emf = null;

  public AbstractController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }

  public final EntityManager getEntityManager()
  {
    return emf.createEntityManager();
  }
}
