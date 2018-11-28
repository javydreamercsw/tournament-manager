/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;

import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchEntryPK;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class MatchEntryJpaController implements Serializable
{
  public MatchEntryJpaController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager()
  {
    return emf.createEntityManager();
  }

  public void create(MatchEntry matchEntry) throws PreexistingEntityException, Exception
  {
    if (matchEntry.getMatchEntryPK() == null)
    {
      matchEntry.setMatchEntryPK(new MatchEntryPK());
    }
    if (matchEntry.getMatchHasTeamList() == null)
    {
      matchEntry.setMatchHasTeamList(new ArrayList<MatchHasTeam>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Round round = matchEntry.getRound();
      if (round != null)
      {
        round = em.getReference(round.getClass(), round.getRoundPK());
        matchEntry.setRound(round);
      }
      List<MatchHasTeam> attachedMatchHasTeamList = new ArrayList<MatchHasTeam>();
      for (MatchHasTeam matchHasTeamListMatchHasTeamToAttach : matchEntry.getMatchHasTeamList())
      {
        matchHasTeamListMatchHasTeamToAttach = em.getReference(matchHasTeamListMatchHasTeamToAttach.getClass(), matchHasTeamListMatchHasTeamToAttach.getMatchHasTeamPK());
        attachedMatchHasTeamList.add(matchHasTeamListMatchHasTeamToAttach);
      }
      matchEntry.setMatchHasTeamList(attachedMatchHasTeamList);
      em.persist(matchEntry);
      if (round != null)
      {
        round.getMatchEntryList().add(matchEntry);
        round = em.merge(round);
      }
      for (MatchHasTeam matchHasTeamListMatchHasTeam : matchEntry.getMatchHasTeamList())
      {
        MatchEntry oldMatchEntryOfMatchHasTeamListMatchHasTeam = matchHasTeamListMatchHasTeam.getMatchEntry();
        matchHasTeamListMatchHasTeam.setMatchEntry(matchEntry);
        matchHasTeamListMatchHasTeam = em.merge(matchHasTeamListMatchHasTeam);
        if (oldMatchEntryOfMatchHasTeamListMatchHasTeam != null)
        {
          oldMatchEntryOfMatchHasTeamListMatchHasTeam.getMatchHasTeamList().remove(matchHasTeamListMatchHasTeam);
          oldMatchEntryOfMatchHasTeamListMatchHasTeam = em.merge(oldMatchEntryOfMatchHasTeamListMatchHasTeam);
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findMatchEntry(matchEntry.getMatchEntryPK()) != null)
      {
        throw new PreexistingEntityException("MatchEntry " + matchEntry + " already exists.", ex);
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

  public void edit(MatchEntry matchEntry) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      MatchEntry persistentMatchEntry = em.find(MatchEntry.class, matchEntry.getMatchEntryPK());
      Round roundOld = persistentMatchEntry.getRound();
      Round roundNew = matchEntry.getRound();
      List<MatchHasTeam> matchHasTeamListOld = persistentMatchEntry.getMatchHasTeamList();
      List<MatchHasTeam> matchHasTeamListNew = matchEntry.getMatchHasTeamList();
      List<String> illegalOrphanMessages = null;
      for (MatchHasTeam matchHasTeamListOldMatchHasTeam : matchHasTeamListOld)
      {
        if (!matchHasTeamListNew.contains(matchHasTeamListOldMatchHasTeam))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<String>();
          }
          illegalOrphanMessages.add("You must retain MatchHasTeam " + matchHasTeamListOldMatchHasTeam + " since its matchEntry field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      if (roundNew != null)
      {
        roundNew = em.getReference(roundNew.getClass(), roundNew.getRoundPK());
        matchEntry.setRound(roundNew);
      }
      List<MatchHasTeam> attachedMatchHasTeamListNew = new ArrayList<MatchHasTeam>();
      for (MatchHasTeam matchHasTeamListNewMatchHasTeamToAttach : matchHasTeamListNew)
      {
        matchHasTeamListNewMatchHasTeamToAttach = em.getReference(matchHasTeamListNewMatchHasTeamToAttach.getClass(), matchHasTeamListNewMatchHasTeamToAttach.getMatchHasTeamPK());
        attachedMatchHasTeamListNew.add(matchHasTeamListNewMatchHasTeamToAttach);
      }
      matchHasTeamListNew = attachedMatchHasTeamListNew;
      matchEntry.setMatchHasTeamList(matchHasTeamListNew);
      matchEntry = em.merge(matchEntry);
      if (roundOld != null && !roundOld.equals(roundNew))
      {
        roundOld.getMatchEntryList().remove(matchEntry);
        roundOld = em.merge(roundOld);
      }
      if (roundNew != null && !roundNew.equals(roundOld))
      {
        roundNew.getMatchEntryList().add(matchEntry);
        roundNew = em.merge(roundNew);
      }
      for (MatchHasTeam matchHasTeamListNewMatchHasTeam : matchHasTeamListNew)
      {
        if (!matchHasTeamListOld.contains(matchHasTeamListNewMatchHasTeam))
        {
          MatchEntry oldMatchEntryOfMatchHasTeamListNewMatchHasTeam = matchHasTeamListNewMatchHasTeam.getMatchEntry();
          matchHasTeamListNewMatchHasTeam.setMatchEntry(matchEntry);
          matchHasTeamListNewMatchHasTeam = em.merge(matchHasTeamListNewMatchHasTeam);
          if (oldMatchEntryOfMatchHasTeamListNewMatchHasTeam != null && !oldMatchEntryOfMatchHasTeamListNewMatchHasTeam.equals(matchEntry))
          {
            oldMatchEntryOfMatchHasTeamListNewMatchHasTeam.getMatchHasTeamList().remove(matchHasTeamListNewMatchHasTeam);
            oldMatchEntryOfMatchHasTeamListNewMatchHasTeam = em.merge(oldMatchEntryOfMatchHasTeamListNewMatchHasTeam);
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
        MatchEntryPK id = matchEntry.getMatchEntryPK();
        if (findMatchEntry(id) == null)
        {
          throw new NonexistentEntityException("The matchEntry with id " + id + " no longer exists.");
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

  public void destroy(MatchEntryPK id) throws IllegalOrphanException, NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      MatchEntry matchEntry;
      try
      {
        matchEntry = em.getReference(MatchEntry.class, id);
        matchEntry.getMatchEntryPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The matchEntry with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<MatchHasTeam> matchHasTeamListOrphanCheck = matchEntry.getMatchHasTeamList();
      for (MatchHasTeam matchHasTeamListOrphanCheckMatchHasTeam : matchHasTeamListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<String>();
        }
        illegalOrphanMessages.add("This MatchEntry (" + matchEntry + ") cannot be destroyed since the MatchHasTeam " + matchHasTeamListOrphanCheckMatchHasTeam + " in its matchHasTeamList field has a non-nullable matchEntry field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      Round round = matchEntry.getRound();
      if (round != null)
      {
        round.getMatchEntryList().remove(matchEntry);
        round = em.merge(round);
      }
      em.remove(matchEntry);
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

  public List<MatchEntry> findMatchEntryEntities()
  {
    return findMatchEntryEntities(true, -1, -1);
  }

  public List<MatchEntry> findMatchEntryEntities(int maxResults, int firstResult)
  {
    return findMatchEntryEntities(false, maxResults, firstResult);
  }

  private List<MatchEntry> findMatchEntryEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(MatchEntry.class));
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

  public MatchEntry findMatchEntry(MatchEntryPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(MatchEntry.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getMatchEntryCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<MatchEntry> rt = cq.from(MatchEntry.class);
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