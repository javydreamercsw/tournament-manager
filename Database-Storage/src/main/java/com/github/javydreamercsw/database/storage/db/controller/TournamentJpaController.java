package com.github.javydreamercsw.database.storage.db.controller;

import com.github.javydreamercsw.database.storage.db.server.AbstractController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

public class TournamentJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = -3241408395907726172L;
  public TournamentJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(Tournament tournament)
  {
    if (tournament.getRoundList() == null)
    {
      tournament.setRoundList(new ArrayList<>());
    }
    if (tournament.getTournamentHasTeamList() == null)
    {
      tournament.setTournamentHasTeamList(new ArrayList<>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      List<Round> attachedRoundList = new ArrayList<>();
      for (Round roundListRoundToAttach : tournament.getRoundList())
      {
        roundListRoundToAttach = em.getReference(roundListRoundToAttach.getClass(), 
                roundListRoundToAttach.getRoundPK());
        attachedRoundList.add(roundListRoundToAttach);
      }
      tournament.setRoundList(attachedRoundList);
      List<TournamentHasTeam> attachedTournamentHasTeamList = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeamToAttach : tournament.getTournamentHasTeamList())
      {
        tournamentHasTeamListTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListTournamentHasTeamToAttach.getClass(), tournamentHasTeamListTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamList.add(tournamentHasTeamListTournamentHasTeamToAttach);
      }
      tournament.setTournamentHasTeamList(attachedTournamentHasTeamList);
      em.persist(tournament);
      for (Round roundListRound : tournament.getRoundList())
      {
        Tournament oldTournamentOfRoundListRound = roundListRound.getTournament();
        roundListRound.setTournament(tournament);
        roundListRound = em.merge(roundListRound);
        if (oldTournamentOfRoundListRound != null)
        {
          oldTournamentOfRoundListRound.getRoundList().remove(roundListRound);
          oldTournamentOfRoundListRound = em.merge(oldTournamentOfRoundListRound);
        }
      }
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeam : tournament.getTournamentHasTeamList())
      {
        Tournament oldTournamentOfTournamentHasTeamListTournamentHasTeam = tournamentHasTeamListTournamentHasTeam.getTournament();
        tournamentHasTeamListTournamentHasTeam.setTournament(tournament);
        tournamentHasTeamListTournamentHasTeam = em.merge(tournamentHasTeamListTournamentHasTeam);
        if (oldTournamentOfTournamentHasTeamListTournamentHasTeam != null)
        {
          oldTournamentOfTournamentHasTeamListTournamentHasTeam.getTournamentHasTeamList().remove(tournamentHasTeamListTournamentHasTeam);
          oldTournamentOfTournamentHasTeamListTournamentHasTeam = em.merge(oldTournamentOfTournamentHasTeamListTournamentHasTeam);
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

  public void edit(Tournament tournament) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Tournament persistentTournament = em.find(Tournament.class, tournament.getId());
      List<Round> roundListOld = persistentTournament.getRoundList();
      List<Round> roundListNew = tournament.getRoundList();
      List<TournamentHasTeam> tournamentHasTeamListOld = persistentTournament.getTournamentHasTeamList();
      List<TournamentHasTeam> tournamentHasTeamListNew = tournament.getTournamentHasTeamList();
      List<String> illegalOrphanMessages = null;
      for (Round roundListOldRound : roundListOld)
      {
        if (!roundListNew.contains(roundListOldRound))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain Round " + roundListOldRound + " since its tournament field is not nullable.");
        }
      }
      for (TournamentHasTeam tournamentHasTeamListOldTournamentHasTeam : tournamentHasTeamListOld)
      {
        if (!tournamentHasTeamListNew.contains(tournamentHasTeamListOldTournamentHasTeam))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain TournamentHasTeam " + tournamentHasTeamListOldTournamentHasTeam + " since its tournament field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      List<Round> attachedRoundListNew = new ArrayList<>();
      for (Round roundListNewRoundToAttach : roundListNew)
      {
        roundListNewRoundToAttach = em.getReference(roundListNewRoundToAttach.getClass(), roundListNewRoundToAttach.getRoundPK());
        attachedRoundListNew.add(roundListNewRoundToAttach);
      }
      roundListNew = attachedRoundListNew;
      tournament.setRoundList(roundListNew);
      List<TournamentHasTeam> attachedTournamentHasTeamListNew = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeamToAttach : tournamentHasTeamListNew)
      {
        tournamentHasTeamListNewTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListNewTournamentHasTeamToAttach.getClass(), tournamentHasTeamListNewTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamListNew.add(tournamentHasTeamListNewTournamentHasTeamToAttach);
      }
      tournamentHasTeamListNew = attachedTournamentHasTeamListNew;
      tournament.setTournamentHasTeamList(tournamentHasTeamListNew);
      tournament = em.merge(tournament);
      for (Round roundListNewRound : roundListNew)
      {
        if (!roundListOld.contains(roundListNewRound))
        {
          Tournament oldTournamentOfRoundListNewRound = roundListNewRound.getTournament();
          roundListNewRound.setTournament(tournament);
          roundListNewRound = em.merge(roundListNewRound);
          if (oldTournamentOfRoundListNewRound != null && !oldTournamentOfRoundListNewRound.equals(tournament))
          {
            oldTournamentOfRoundListNewRound.getRoundList().remove(roundListNewRound);
            oldTournamentOfRoundListNewRound = em.merge(oldTournamentOfRoundListNewRound);
          }
        }
      }
      for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeam : tournamentHasTeamListNew)
      {
        if (!tournamentHasTeamListOld.contains(tournamentHasTeamListNewTournamentHasTeam))
        {
          Tournament oldTournamentOfTournamentHasTeamListNewTournamentHasTeam = tournamentHasTeamListNewTournamentHasTeam.getTournament();
          tournamentHasTeamListNewTournamentHasTeam.setTournament(tournament);
          tournamentHasTeamListNewTournamentHasTeam = em.merge(tournamentHasTeamListNewTournamentHasTeam);
          if (oldTournamentOfTournamentHasTeamListNewTournamentHasTeam != null && !oldTournamentOfTournamentHasTeamListNewTournamentHasTeam.equals(tournament))
          {
            oldTournamentOfTournamentHasTeamListNewTournamentHasTeam.getTournamentHasTeamList().remove(tournamentHasTeamListNewTournamentHasTeam);
            oldTournamentOfTournamentHasTeamListNewTournamentHasTeam = em.merge(oldTournamentOfTournamentHasTeamListNewTournamentHasTeam);
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
        Integer id = tournament.getId();
        if (findTournament(id) == null)
        {
          throw new NonexistentEntityException("The tournament with id " + id + " no longer exists.");
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
      Tournament tournament;
      try
      {
        tournament = em.getReference(Tournament.class, id);
        tournament.getId();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The tournament with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<Round> roundListOrphanCheck = tournament.getRoundList();
      for (Round roundListOrphanCheckRound : roundListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Tournament (" + tournament + ") cannot be destroyed since the Round " + roundListOrphanCheckRound + " in its roundList field has a non-nullable tournament field.");
      }
      List<TournamentHasTeam> tournamentHasTeamListOrphanCheck = tournament.getTournamentHasTeamList();
      for (TournamentHasTeam tournamentHasTeamListOrphanCheckTournamentHasTeam : tournamentHasTeamListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Tournament (" + tournament + ") cannot be destroyed since the TournamentHasTeam " + tournamentHasTeamListOrphanCheckTournamentHasTeam + " in its tournamentHasTeamList field has a non-nullable tournament field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      em.remove(tournament);
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

  public List<Tournament> findTournamentEntities()
  {
    return findTournamentEntities(true, -1, -1);
  }

  public List<Tournament> findTournamentEntities(int maxResults, int firstResult)
  {
    return findTournamentEntities(false, maxResults, firstResult);
  }

  private List<Tournament> findTournamentEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Tournament.class));
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

  public Tournament findTournament(Integer id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(Tournament.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getTournamentCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Tournament> rt = cq.from(Tournament.class);
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
