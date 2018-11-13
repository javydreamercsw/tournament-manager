/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;

import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import net.sourceforge.javydreamercsw.database.storage.db.MatchEntry;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import net.sourceforge.javydreamercsw.database.storage.db.Format;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

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

  public void create(Format format)
  {
    if (format.getMatchEntryList() == null)
    {
      format.setMatchEntryList(new ArrayList<MatchEntry>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      List<MatchEntry> attachedMatchEntryList = new ArrayList<MatchEntry>();
      for (MatchEntry matchEntryListMatchEntryToAttach : format.getMatchEntryList())
      {
        matchEntryListMatchEntryToAttach = em.getReference(matchEntryListMatchEntryToAttach.getClass(), matchEntryListMatchEntryToAttach.getMatchEntryPK());
        attachedMatchEntryList.add(matchEntryListMatchEntryToAttach);
      }
      format.setMatchEntryList(attachedMatchEntryList);
      em.persist(format);
      for (MatchEntry matchEntryListMatchEntry : format.getMatchEntryList())
      {
        Format oldFormatOfMatchEntryListMatchEntry = matchEntryListMatchEntry.getFormat();
        matchEntryListMatchEntry.setFormat(format);
        matchEntryListMatchEntry = em.merge(matchEntryListMatchEntry);
        if (oldFormatOfMatchEntryListMatchEntry != null)
        {
          oldFormatOfMatchEntryListMatchEntry.getMatchEntryList().remove(matchEntryListMatchEntry);
          oldFormatOfMatchEntryListMatchEntry = em.merge(oldFormatOfMatchEntryListMatchEntry);
        }
      }
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

  public void edit(Format format) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Format persistentFormat = em.find(Format.class, format.getId());
      List<MatchEntry> matchEntryListOld = persistentFormat.getMatchEntryList();
      List<MatchEntry> matchEntryListNew = format.getMatchEntryList();
      List<String> illegalOrphanMessages = null;
      for (MatchEntry matchEntryListOldMatchEntry : matchEntryListOld)
      {
        if (!matchEntryListNew.contains(matchEntryListOldMatchEntry))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<String>();
          }
          illegalOrphanMessages.add("You must retain MatchEntry " + matchEntryListOldMatchEntry + " since its format field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      List<MatchEntry> attachedMatchEntryListNew = new ArrayList<MatchEntry>();
      for (MatchEntry matchEntryListNewMatchEntryToAttach : matchEntryListNew)
      {
        matchEntryListNewMatchEntryToAttach = em.getReference(matchEntryListNewMatchEntryToAttach.getClass(), matchEntryListNewMatchEntryToAttach.getMatchEntryPK());
        attachedMatchEntryListNew.add(matchEntryListNewMatchEntryToAttach);
      }
      matchEntryListNew = attachedMatchEntryListNew;
      format.setMatchEntryList(matchEntryListNew);
      format = em.merge(format);
      for (MatchEntry matchEntryListNewMatchEntry : matchEntryListNew)
      {
        if (!matchEntryListOld.contains(matchEntryListNewMatchEntry))
        {
          Format oldFormatOfMatchEntryListNewMatchEntry = matchEntryListNewMatchEntry.getFormat();
          matchEntryListNewMatchEntry.setFormat(format);
          matchEntryListNewMatchEntry = em.merge(matchEntryListNewMatchEntry);
          if (oldFormatOfMatchEntryListNewMatchEntry != null && !oldFormatOfMatchEntryListNewMatchEntry.equals(format))
          {
            oldFormatOfMatchEntryListNewMatchEntry.getMatchEntryList().remove(matchEntryListNewMatchEntry);
            oldFormatOfMatchEntryListNewMatchEntry = em.merge(oldFormatOfMatchEntryListNewMatchEntry);
          }
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        Integer id = format.getId();
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

  public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException
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
        format.getId();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The format with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<MatchEntry> matchEntryListOrphanCheck = format.getMatchEntryList();
      for (MatchEntry matchEntryListOrphanCheckMatchEntry : matchEntryListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<String>();
        }
        illegalOrphanMessages.add("This Format (" + format + ") cannot be destroyed since the MatchEntry " + matchEntryListOrphanCheckMatchEntry + " in its matchEntryList field has a non-nullable format field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
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

  public Format findFormat(Integer id)
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
