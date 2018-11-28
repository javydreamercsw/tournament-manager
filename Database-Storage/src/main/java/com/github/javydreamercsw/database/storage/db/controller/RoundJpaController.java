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

import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.MatchEntry;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.RoundPK;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class RoundJpaController implements Serializable
{
  public RoundJpaController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager()
  {
    return emf.createEntityManager();
  }

  public void create(Round round) throws PreexistingEntityException, Exception
  {
    if (round.getRoundPK() == null)
    {
      round.setRoundPK(new RoundPK());
    }
    if (round.getMatchEntryList() == null)
    {
      round.setMatchEntryList(new ArrayList<MatchEntry>());
    }
    round.getRoundPK().setTournamentId(round.getTournament().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Tournament tournament = round.getTournament();
      if (tournament != null)
      {
        tournament = em.getReference(tournament.getClass(), tournament.getId());
        round.setTournament(tournament);
      }
      List<MatchEntry> attachedMatchEntryList = new ArrayList<MatchEntry>();
      for (MatchEntry matchEntryListMatchEntryToAttach : round.getMatchEntryList())
      {
        matchEntryListMatchEntryToAttach = em.getReference(matchEntryListMatchEntryToAttach.getClass(), matchEntryListMatchEntryToAttach.getMatchEntryPK());
        attachedMatchEntryList.add(matchEntryListMatchEntryToAttach);
      }
      round.setMatchEntryList(attachedMatchEntryList);
      em.persist(round);
      if (tournament != null)
      {
        tournament.getRoundList().add(round);
        tournament = em.merge(tournament);
      }
      for (MatchEntry matchEntryListMatchEntry : round.getMatchEntryList())
      {
        Round oldRoundOfMatchEntryListMatchEntry = matchEntryListMatchEntry.getRound();
        matchEntryListMatchEntry.setRound(round);
        matchEntryListMatchEntry = em.merge(matchEntryListMatchEntry);
        if (oldRoundOfMatchEntryListMatchEntry != null)
        {
          oldRoundOfMatchEntryListMatchEntry.getMatchEntryList().remove(matchEntryListMatchEntry);
          oldRoundOfMatchEntryListMatchEntry = em.merge(oldRoundOfMatchEntryListMatchEntry);
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findRound(round.getRoundPK()) != null)
      {
        throw new PreexistingEntityException("Round " + round + " already exists.", ex);
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

  public void edit(Round round) throws NonexistentEntityException, Exception
  {
    round.getRoundPK().setTournamentId(round.getTournament().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Round persistentRound = em.find(Round.class, round.getRoundPK());
      Tournament tournamentOld = persistentRound.getTournament();
      Tournament tournamentNew = round.getTournament();
      List<MatchEntry> matchEntryListOld = persistentRound.getMatchEntryList();
      List<MatchEntry> matchEntryListNew = round.getMatchEntryList();
      if (tournamentNew != null)
      {
        tournamentNew = em.getReference(tournamentNew.getClass(), tournamentNew.getId());
        round.setTournament(tournamentNew);
      }
      List<MatchEntry> attachedMatchEntryListNew = new ArrayList<MatchEntry>();
      for (MatchEntry matchEntryListNewMatchEntryToAttach : matchEntryListNew)
      {
        matchEntryListNewMatchEntryToAttach = em.getReference(matchEntryListNewMatchEntryToAttach.getClass(), matchEntryListNewMatchEntryToAttach.getMatchEntryPK());
        attachedMatchEntryListNew.add(matchEntryListNewMatchEntryToAttach);
      }
      matchEntryListNew = attachedMatchEntryListNew;
      round.setMatchEntryList(matchEntryListNew);
      round = em.merge(round);
      if (tournamentOld != null && !tournamentOld.equals(tournamentNew))
      {
        tournamentOld.getRoundList().remove(round);
        tournamentOld = em.merge(tournamentOld);
      }
      if (tournamentNew != null && !tournamentNew.equals(tournamentOld))
      {
        tournamentNew.getRoundList().add(round);
        tournamentNew = em.merge(tournamentNew);
      }
      for (MatchEntry matchEntryListOldMatchEntry : matchEntryListOld)
      {
        if (!matchEntryListNew.contains(matchEntryListOldMatchEntry))
        {
          matchEntryListOldMatchEntry.setRound(null);
          matchEntryListOldMatchEntry = em.merge(matchEntryListOldMatchEntry);
        }
      }
      for (MatchEntry matchEntryListNewMatchEntry : matchEntryListNew)
      {
        if (!matchEntryListOld.contains(matchEntryListNewMatchEntry))
        {
          Round oldRoundOfMatchEntryListNewMatchEntry = matchEntryListNewMatchEntry.getRound();
          matchEntryListNewMatchEntry.setRound(round);
          matchEntryListNewMatchEntry = em.merge(matchEntryListNewMatchEntry);
          if (oldRoundOfMatchEntryListNewMatchEntry != null && !oldRoundOfMatchEntryListNewMatchEntry.equals(round))
          {
            oldRoundOfMatchEntryListNewMatchEntry.getMatchEntryList().remove(matchEntryListNewMatchEntry);
            oldRoundOfMatchEntryListNewMatchEntry = em.merge(oldRoundOfMatchEntryListNewMatchEntry);
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
        RoundPK id = round.getRoundPK();
        if (findRound(id) == null)
        {
          throw new NonexistentEntityException("The round with id " + id + " no longer exists.");
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

  public void destroy(RoundPK id) throws NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Round round;
      try
      {
        round = em.getReference(Round.class, id);
        round.getRoundPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The round with id " + id + " no longer exists.", enfe);
      }
      Tournament tournament = round.getTournament();
      if (tournament != null)
      {
        tournament.getRoundList().remove(round);
        tournament = em.merge(tournament);
      }
      List<MatchEntry> matchEntryList = round.getMatchEntryList();
      for (MatchEntry matchEntryListMatchEntry : matchEntryList)
      {
        matchEntryListMatchEntry.setRound(null);
        matchEntryListMatchEntry = em.merge(matchEntryListMatchEntry);
      }
      em.remove(round);
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

  public List<Round> findRoundEntities()
  {
    return findRoundEntities(true, -1, -1);
  }

  public List<Round> findRoundEntities(int maxResults, int firstResult)
  {
    return findRoundEntities(false, maxResults, firstResult);
  }

  private List<Round> findRoundEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Round.class));
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

  public Round findRound(RoundPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(Round.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getRoundCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Round> rt = cq.from(Round.class);
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