package com.github.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.RoundPK;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class RoundJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = -6387501653143947641L;

  public RoundJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(Round round) throws PreexistingEntityException, Exception
  {
    if (round.getRoundPK() == null)
    {
      round.setRoundPK(new RoundPK());
    }
    if (round.getMatchEntryList() == null)
    {
      round.setMatchEntryList(new ArrayList<>());
    }
    round.getRoundPK().setTournamentId(round.getTournament().getTournamentPK().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Tournament tournament = round.getTournament();
      if (tournament != null)
      {
        tournament = em.getReference(tournament.getClass(), tournament.getTournamentPK());
        round.setTournament(tournament);
      }
      List<MatchEntry> attachedMatchEntryList = new ArrayList<>();
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

  public void edit(Round round) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    round.getRoundPK().setTournamentId(round.getTournament().getTournamentPK().getId());
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
      List<String> illegalOrphanMessages = null;
      for (MatchEntry matchEntryListOldMatchEntry : matchEntryListOld)
      {
        if (!matchEntryListNew.contains(matchEntryListOldMatchEntry))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain MatchEntry " + matchEntryListOldMatchEntry + " since its round field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      if (tournamentNew != null)
      {
        tournamentNew = em.getReference(tournamentNew.getClass(), tournamentNew.getTournamentPK());
        round.setTournament(tournamentNew);
      }
      List<MatchEntry> attachedMatchEntryListNew = new ArrayList<>();
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

  public void destroy(RoundPK id) throws IllegalOrphanException, NonexistentEntityException
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
      List<String> illegalOrphanMessages = null;
      List<MatchEntry> matchEntryListOrphanCheck = round.getMatchEntryList();
      for (MatchEntry matchEntryListOrphanCheckMatchEntry : matchEntryListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Round (" + round + ") cannot be destroyed since the MatchEntry " + matchEntryListOrphanCheckMatchEntry + " in its matchEntryList field has a non-nullable round field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      Tournament tournament = round.getTournament();
      if (tournament != null)
      {
        tournament.getRoundList().remove(round);
        tournament = em.merge(tournament);
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
