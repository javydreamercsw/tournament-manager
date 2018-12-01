package com.github.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchHasTeamPK;
import com.github.javydreamercsw.database.storage.db.MatchResult;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class MatchHasTeamJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = -2927053439134199404L;

  public MatchHasTeamJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(MatchHasTeam matchHasTeam) throws PreexistingEntityException, Exception
  {
    if (matchHasTeam.getMatchHasTeamPK() == null)
    {
      matchHasTeam.setMatchHasTeamPK(new MatchHasTeamPK());
    }
    matchHasTeam.getMatchHasTeamPK().setMatchEntryFormatId(matchHasTeam.getMatchEntry().getMatchEntryPK().getFormatId());
    matchHasTeam.getMatchHasTeamPK().setTeamId(matchHasTeam.getTeam().getId());
    matchHasTeam.getMatchHasTeamPK().setMatchEntryId(matchHasTeam.getMatchEntry().getMatchEntryPK().getId());
    matchHasTeam.getMatchHasTeamPK().setMatchEntryFormatGameId(matchHasTeam.getMatchEntry().getMatchEntryPK().getFormatGameId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      MatchEntry matchEntry = matchHasTeam.getMatchEntry();
      if (matchEntry != null)
      {
        matchEntry = em.getReference(matchEntry.getClass(), matchEntry.getMatchEntryPK());
        matchHasTeam.setMatchEntry(matchEntry);
      }
      MatchResult matchResult = matchHasTeam.getMatchResult();
      if (matchResult != null)
      {
        matchResult = em.getReference(matchResult.getClass(), matchResult.getMatchResultPK());
        matchHasTeam.setMatchResult(matchResult);
      }
      Team team = matchHasTeam.getTeam();
      if (team != null)
      {
        team = em.getReference(team.getClass(), team.getId());
        matchHasTeam.setTeam(team);
      }
      em.persist(matchHasTeam);
      if (matchEntry != null)
      {
        matchEntry.getMatchHasTeamList().add(matchHasTeam);
        matchEntry = em.merge(matchEntry);
      }
      if (matchResult != null)
      {
        matchResult.getMatchHasTeamList().add(matchHasTeam);
        matchResult = em.merge(matchResult);
      }
      if (team != null)
      {
        team.getMatchHasTeamList().add(matchHasTeam);
        team = em.merge(team);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findMatchHasTeam(matchHasTeam.getMatchHasTeamPK()) != null)
      {
        throw new PreexistingEntityException("MatchHasTeam " + matchHasTeam + " already exists.", ex);
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

  public void edit(MatchHasTeam matchHasTeam) throws NonexistentEntityException, Exception
  {
    matchHasTeam.getMatchHasTeamPK().setMatchEntryFormatId(matchHasTeam.getMatchEntry().getMatchEntryPK().getFormatId());
    matchHasTeam.getMatchHasTeamPK().setTeamId(matchHasTeam.getTeam().getId());
    matchHasTeam.getMatchHasTeamPK().setMatchEntryId(matchHasTeam.getMatchEntry().getMatchEntryPK().getId());
    matchHasTeam.getMatchHasTeamPK().setMatchEntryFormatGameId(matchHasTeam.getMatchEntry().getMatchEntryPK().getFormatGameId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      MatchHasTeam persistentMatchHasTeam = em.find(MatchHasTeam.class, matchHasTeam.getMatchHasTeamPK());
      MatchEntry matchEntryOld = persistentMatchHasTeam.getMatchEntry();
      MatchEntry matchEntryNew = matchHasTeam.getMatchEntry();
      MatchResult matchResultOld = persistentMatchHasTeam.getMatchResult();
      MatchResult matchResultNew = matchHasTeam.getMatchResult();
      Team teamOld = persistentMatchHasTeam.getTeam();
      Team teamNew = matchHasTeam.getTeam();
      if (matchEntryNew != null)
      {
        matchEntryNew = em.getReference(matchEntryNew.getClass(), matchEntryNew.getMatchEntryPK());
        matchHasTeam.setMatchEntry(matchEntryNew);
      }
      if (matchResultNew != null)
      {
        matchResultNew = em.getReference(matchResultNew.getClass(), matchResultNew.getMatchResultPK());
        matchHasTeam.setMatchResult(matchResultNew);
      }
      if (teamNew != null)
      {
        teamNew = em.getReference(teamNew.getClass(), teamNew.getId());
        matchHasTeam.setTeam(teamNew);
      }
      matchHasTeam = em.merge(matchHasTeam);
      if (matchEntryOld != null && !matchEntryOld.equals(matchEntryNew))
      {
        matchEntryOld.getMatchHasTeamList().remove(matchHasTeam);
        matchEntryOld = em.merge(matchEntryOld);
      }
      if (matchEntryNew != null && !matchEntryNew.equals(matchEntryOld))
      {
        matchEntryNew.getMatchHasTeamList().add(matchHasTeam);
        matchEntryNew = em.merge(matchEntryNew);
      }
      if (matchResultOld != null && !matchResultOld.equals(matchResultNew))
      {
        matchResultOld.getMatchHasTeamList().remove(matchHasTeam);
        matchResultOld = em.merge(matchResultOld);
      }
      if (matchResultNew != null && !matchResultNew.equals(matchResultOld))
      {
        matchResultNew.getMatchHasTeamList().add(matchHasTeam);
        matchResultNew = em.merge(matchResultNew);
      }
      if (teamOld != null && !teamOld.equals(teamNew))
      {
        teamOld.getMatchHasTeamList().remove(matchHasTeam);
        teamOld = em.merge(teamOld);
      }
      if (teamNew != null && !teamNew.equals(teamOld))
      {
        teamNew.getMatchHasTeamList().add(matchHasTeam);
        teamNew = em.merge(teamNew);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        MatchHasTeamPK id = matchHasTeam.getMatchHasTeamPK();
        if (findMatchHasTeam(id) == null)
        {
          throw new NonexistentEntityException("The matchHasTeam with id " + id + " no longer exists.");
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

  public void destroy(MatchHasTeamPK id) throws NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      MatchHasTeam matchHasTeam;
      try
      {
        matchHasTeam = em.getReference(MatchHasTeam.class, id);
        matchHasTeam.getMatchHasTeamPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The matchHasTeam with id " + id + " no longer exists.", enfe);
      }
      MatchEntry matchEntry = matchHasTeam.getMatchEntry();
      if (matchEntry != null)
      {
        matchEntry.getMatchHasTeamList().remove(matchHasTeam);
        matchEntry = em.merge(matchEntry);
      }
      MatchResult matchResult = matchHasTeam.getMatchResult();
      if (matchResult != null)
      {
        matchResult.getMatchHasTeamList().remove(matchHasTeam);
        matchResult = em.merge(matchResult);
      }
      Team team = matchHasTeam.getTeam();
      if (team != null)
      {
        team.getMatchHasTeamList().remove(matchHasTeam);
        team = em.merge(team);
      }
      em.remove(matchHasTeam);
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

  public List<MatchHasTeam> findMatchHasTeamEntities()
  {
    return findMatchHasTeamEntities(true, -1, -1);
  }

  public List<MatchHasTeam> findMatchHasTeamEntities(int maxResults, int firstResult)
  {
    return findMatchHasTeamEntities(false, maxResults, firstResult);
  }

  private List<MatchHasTeam> findMatchHasTeamEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(MatchHasTeam.class));
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

  public MatchHasTeam findMatchHasTeam(MatchHasTeamPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(MatchHasTeam.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getMatchHasTeamCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<MatchHasTeam> rt = cq.from(MatchHasTeam.class);
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
