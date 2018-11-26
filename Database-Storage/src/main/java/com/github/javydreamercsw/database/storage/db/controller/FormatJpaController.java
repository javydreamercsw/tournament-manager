/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.FormatPK;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class FormatJpaController implements Serializable
{
  public FormatJpaController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager()
  {
    return emf.createEntityManager();
  }

  public void create(Format format) throws PreexistingEntityException, Exception
  {
    if (format.getFormatPK() == null)
    {
      format.setFormatPK(new FormatPK());
    }
    format.getFormatPK().setGameId(format.getGame().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Game game = format.getGame();
      if (game != null)
      {
        game = em.getReference(game.getClass(), game.getId());
        format.setGame(game);
      }
      em.persist(format);
      if (game != null)
      {
        game.getFormatList().add(format);
        game = em.merge(game);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findFormat(format.getFormatPK()) != null)
      {
        throw new PreexistingEntityException("Format " + format + " already exists.", ex);
      }
      throw ex;
    }
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }
  }

  public void edit(Format format) throws NonexistentEntityException, Exception
  {
    format.getFormatPK().setGameId(format.getGame().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Format persistentFormat = em.find(Format.class, format.getFormatPK());
      Game gameOld = persistentFormat.getGame();
      Game gameNew = format.getGame();
      if (gameNew != null)
      {
        gameNew = em.getReference(gameNew.getClass(), gameNew.getId());
        format.setGame(gameNew);
      }
      format = em.merge(format);
      if (gameOld != null && !gameOld.equals(gameNew))
      {
        gameOld.getFormatList().remove(format);
        gameOld = em.merge(gameOld);
      }
      if (gameNew != null && !gameNew.equals(gameOld))
      {
        gameNew.getFormatList().add(format);
        gameNew = em.merge(gameNew);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        FormatPK id = format.getFormatPK();
        if (findFormat(id) == null)
        {
          throw new NonexistentEntityException("The format with id " + id + " no longer exists.");
        }
      }
      throw ex;
    }
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }
  }

  public void destroy(FormatPK id) throws NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Format format;
      try
      {
        format = em.getReference(Format.class, id);
        format.getFormatPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The format with id " + id + " no longer exists.", enfe);
      }
      Game game = format.getGame();
      if (game != null)
      {
        game.getFormatList().remove(format);
        game = em.merge(game);
      }
      em.remove(format);
      em.getTransaction().commit();
    }
    finally
    {
      if (em != null)
      {
        em.close();
      }
    }
  }

  public List<Format> findFormatEntities()
  {
    return findFormatEntities(true, -1, -1);
  }

  public List<Format> findFormatEntities(int maxResults, int firstResult)
  {
    return findFormatEntities(false, maxResults, firstResult);
  }

  private List<Format> findFormatEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Format.class));
      Query q = em.createQuery(cq);
      if (!all)
      {
        q.setMaxResults(maxResults);
        q.setFirstResult(firstResult);
      }
      return q.getResultList();
    }
    finally
    {
      em.close();
    }
  }

  public Format findFormat(FormatPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(Format.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getFormatCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Format> rt = cq.from(Format.class);
      cq.select(em.getCriteriaBuilder().count(rt));
      Query q = em.createQuery(cq);
      return ((Long) q.getSingleResult()).intValue();
    }
    finally
    {
      em.close();
    }
  }
  
}
