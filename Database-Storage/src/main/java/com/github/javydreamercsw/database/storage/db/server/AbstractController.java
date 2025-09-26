package com.github.javydreamercsw.database.storage.db.server;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/** This avoids synchronization issues where each service had their own EntityManager. */
public class AbstractController {
  private EntityManagerFactory emf = null;

  public AbstractController(EntityManagerFactory emf) {
    this.emf = emf;
  }

  public final EntityManager getEntityManager() {
    return emf.createEntityManager();
  }
}
