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
  private EntityManager em = null;

  public AbstractController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }

  public EntityManager getEntityManager()
  {
    if (em == null || !em.isOpen())
    {
      em = emf.createEntityManager();
    }
    return em;
  }
}
