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

import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class TeamJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = -948309699404935164L;

  public TeamJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(Team team)
  {
    if (team.getPlayerList() == null)
    {
      team.setPlayerList(new ArrayList<>());
    }
    if (team.getMatchHasTeamList() == null)
    {
      team.setMatchHasTeamList(new ArrayList<>());
    }
    if (team.getTournamentHasTeamList() == null)
    {
      team.setTournamentHasTeamList(new ArrayList<>());
    }
    if (team.getTeamHasFormatRecordList() == null)
    {
      team.setTeamHasFormatRecordList(new ArrayList<>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      List<Player> attachedPlayerList = new ArrayList<>();
      for (Player playerListPlayerToAttach : team.getPlayerList())
      {
        playerListPlayerToAttach = em.getReference(playerListPlayerToAttach.getClass(), playerListPlayerToAttach.getId());
        attachedPlayerList.add(playerListPlayerToAttach);
      }
      team.setPlayerList(attachedPlayerList);
      List<MatchHasTeam> attachedMatchHasTeamList = new ArrayList<>();
      for (MatchHasTeam matchHasTeamListMatchHasTeamToAttach : team.getMatchHasTeamList())
      {
        matchHasTeamListMatchHasTeamToAttach = em.getReference(matchHasTeamListMatchHasTeamToAttach.getClass(), matchHasTeamListMatchHasTeamToAttach.getMatchHasTeamPK());
        attachedMatchHasTeamList.add(matchHasTeamListMatchHasTeamToAttach);
      }
      team.setMatchHasTeamList(attachedMatchHasTeamList);
      List<TournamentHasTeam> attachedTournamentHasTeamList = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeamToAttach : team.getTournamentHasTeamList())
      {
        tournamentHasTeamListTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListTournamentHasTeamToAttach.getClass(), tournamentHasTeamListTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamList.add(tournamentHasTeamListTournamentHasTeamToAttach);
      }
      team.setTournamentHasTeamList(attachedTournamentHasTeamList);
      List<TeamHasFormatRecord> attachedTeamHasFormatRecordList = new ArrayList<>();
      for (TeamHasFormatRecord teamHasFormatRecordListTeamHasFormatRecordToAttach : team.getTeamHasFormatRecordList())
      {
        teamHasFormatRecordListTeamHasFormatRecordToAttach = em.getReference(teamHasFormatRecordListTeamHasFormatRecordToAttach.getClass(), teamHasFormatRecordListTeamHasFormatRecordToAttach.getTeamHasFormatRecordPK());
        attachedTeamHasFormatRecordList.add(teamHasFormatRecordListTeamHasFormatRecordToAttach);
      }
      team.setTeamHasFormatRecordList(attachedTeamHasFormatRecordList);
      em.persist(team);
      for (Player playerListPlayer : team.getPlayerList())
      {
        playerListPlayer.getTeamList().add(team);
        playerListPlayer = em.merge(playerListPlayer);
      }
      for (MatchHasTeam matchHasTeamListMatchHasTeam : team.getMatchHasTeamList())
      {
        Team oldTeamOfMatchHasTeamListMatchHasTeam = matchHasTeamListMatchHasTeam.getTeam();
        matchHasTeamListMatchHasTeam.setTeam(team);
        matchHasTeamListMatchHasTeam = em.merge(matchHasTeamListMatchHasTeam);
        if (oldTeamOfMatchHasTeamListMatchHasTeam != null)
        {
          oldTeamOfMatchHasTeamListMatchHasTeam.getMatchHasTeamList().remove(matchHasTeamListMatchHasTeam);
          oldTeamOfMatchHasTeamListMatchHasTeam = em.merge(oldTeamOfMatchHasTeamListMatchHasTeam);
        }
      }
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeam : team.getTournamentHasTeamList())
      {
        Team oldTeamOfTournamentHasTeamListTournamentHasTeam = tournamentHasTeamListTournamentHasTeam.getTeam();
        tournamentHasTeamListTournamentHasTeam.setTeam(team);
        tournamentHasTeamListTournamentHasTeam = em.merge(tournamentHasTeamListTournamentHasTeam);
        if (oldTeamOfTournamentHasTeamListTournamentHasTeam != null)
        {
          oldTeamOfTournamentHasTeamListTournamentHasTeam.getTournamentHasTeamList().remove(tournamentHasTeamListTournamentHasTeam);
          oldTeamOfTournamentHasTeamListTournamentHasTeam = em.merge(oldTeamOfTournamentHasTeamListTournamentHasTeam);
        }
      }
      for (TeamHasFormatRecord teamHasFormatRecordListTeamHasFormatRecord : team.getTeamHasFormatRecordList())
      {
        Team oldTeamOfTeamHasFormatRecordListTeamHasFormatRecord = teamHasFormatRecordListTeamHasFormatRecord.getTeam();
        teamHasFormatRecordListTeamHasFormatRecord.setTeam(team);
        teamHasFormatRecordListTeamHasFormatRecord = em.merge(teamHasFormatRecordListTeamHasFormatRecord);
        if (oldTeamOfTeamHasFormatRecordListTeamHasFormatRecord != null)
        {
          oldTeamOfTeamHasFormatRecordListTeamHasFormatRecord.getTeamHasFormatRecordList().remove(teamHasFormatRecordListTeamHasFormatRecord);
          oldTeamOfTeamHasFormatRecordListTeamHasFormatRecord = em.merge(oldTeamOfTeamHasFormatRecordListTeamHasFormatRecord);
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

  public void edit(Team team) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Team persistentTeam = em.find(Team.class, team.getId());
      List<Player> playerListOld = persistentTeam.getPlayerList();
      List<Player> playerListNew = team.getPlayerList();
      List<MatchHasTeam> matchHasTeamListOld = persistentTeam.getMatchHasTeamList();
      List<MatchHasTeam> matchHasTeamListNew = team.getMatchHasTeamList();
      List<TournamentHasTeam> tournamentHasTeamListOld = persistentTeam.getTournamentHasTeamList();
      List<TournamentHasTeam> tournamentHasTeamListNew = team.getTournamentHasTeamList();
      List<TeamHasFormatRecord> teamHasFormatRecordListOld = persistentTeam.getTeamHasFormatRecordList();
      List<TeamHasFormatRecord> teamHasFormatRecordListNew = team.getTeamHasFormatRecordList();
      List<String> illegalOrphanMessages = null;
      for (MatchHasTeam matchHasTeamListOldMatchHasTeam : matchHasTeamListOld)
      {
        if (!matchHasTeamListNew.contains(matchHasTeamListOldMatchHasTeam))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain MatchHasTeam " + matchHasTeamListOldMatchHasTeam + " since its team field is not nullable.");
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
          illegalOrphanMessages.add("You must retain TournamentHasTeam " + tournamentHasTeamListOldTournamentHasTeam + " since its team field is not nullable.");
        }
      }
      for (TeamHasFormatRecord teamHasFormatRecordListOldTeamHasFormatRecord : teamHasFormatRecordListOld)
      {
        if (!teamHasFormatRecordListNew.contains(teamHasFormatRecordListOldTeamHasFormatRecord))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain TeamHasFormatRecord " + teamHasFormatRecordListOldTeamHasFormatRecord + " since its team field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      List<Player> attachedPlayerListNew = new ArrayList<>();
      for (Player playerListNewPlayerToAttach : playerListNew)
      {
        playerListNewPlayerToAttach = em.getReference(playerListNewPlayerToAttach.getClass(), playerListNewPlayerToAttach.getId());
        attachedPlayerListNew.add(playerListNewPlayerToAttach);
      }
      playerListNew = attachedPlayerListNew;
      team.setPlayerList(playerListNew);
      List<MatchHasTeam> attachedMatchHasTeamListNew = new ArrayList<>();
      for (MatchHasTeam matchHasTeamListNewMatchHasTeamToAttach : matchHasTeamListNew)
      {
        matchHasTeamListNewMatchHasTeamToAttach = em.getReference(matchHasTeamListNewMatchHasTeamToAttach.getClass(), matchHasTeamListNewMatchHasTeamToAttach.getMatchHasTeamPK());
        attachedMatchHasTeamListNew.add(matchHasTeamListNewMatchHasTeamToAttach);
      }
      matchHasTeamListNew = attachedMatchHasTeamListNew;
      team.setMatchHasTeamList(matchHasTeamListNew);
      List<TournamentHasTeam> attachedTournamentHasTeamListNew = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeamToAttach : tournamentHasTeamListNew)
      {
        tournamentHasTeamListNewTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListNewTournamentHasTeamToAttach.getClass(), tournamentHasTeamListNewTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamListNew.add(tournamentHasTeamListNewTournamentHasTeamToAttach);
      }
      tournamentHasTeamListNew = attachedTournamentHasTeamListNew;
      team.setTournamentHasTeamList(tournamentHasTeamListNew);
      List<TeamHasFormatRecord> attachedTeamHasFormatRecordListNew = new ArrayList<>();
      for (TeamHasFormatRecord teamHasFormatRecordListNewTeamHasFormatRecordToAttach : teamHasFormatRecordListNew)
      {
        teamHasFormatRecordListNewTeamHasFormatRecordToAttach = em.getReference(teamHasFormatRecordListNewTeamHasFormatRecordToAttach.getClass(), teamHasFormatRecordListNewTeamHasFormatRecordToAttach.getTeamHasFormatRecordPK());
        attachedTeamHasFormatRecordListNew.add(teamHasFormatRecordListNewTeamHasFormatRecordToAttach);
      }
      teamHasFormatRecordListNew = attachedTeamHasFormatRecordListNew;
      team.setTeamHasFormatRecordList(teamHasFormatRecordListNew);
      team = em.merge(team);
      for (Player playerListOldPlayer : playerListOld)
      {
        if (!playerListNew.contains(playerListOldPlayer))
        {
          playerListOldPlayer.getTeamList().remove(team);
          playerListOldPlayer = em.merge(playerListOldPlayer);
        }
      }
      for (Player playerListNewPlayer : playerListNew)
      {
        if (!playerListOld.contains(playerListNewPlayer))
        {
          playerListNewPlayer.getTeamList().add(team);
          playerListNewPlayer = em.merge(playerListNewPlayer);
        }
      }
      for (MatchHasTeam matchHasTeamListNewMatchHasTeam : matchHasTeamListNew)
      {
        if (!matchHasTeamListOld.contains(matchHasTeamListNewMatchHasTeam))
        {
          Team oldTeamOfMatchHasTeamListNewMatchHasTeam = matchHasTeamListNewMatchHasTeam.getTeam();
          matchHasTeamListNewMatchHasTeam.setTeam(team);
          matchHasTeamListNewMatchHasTeam = em.merge(matchHasTeamListNewMatchHasTeam);
          if (oldTeamOfMatchHasTeamListNewMatchHasTeam != null && !oldTeamOfMatchHasTeamListNewMatchHasTeam.equals(team))
          {
            oldTeamOfMatchHasTeamListNewMatchHasTeam.getMatchHasTeamList().remove(matchHasTeamListNewMatchHasTeam);
            oldTeamOfMatchHasTeamListNewMatchHasTeam = em.merge(oldTeamOfMatchHasTeamListNewMatchHasTeam);
          }
        }
      }
      for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeam : tournamentHasTeamListNew)
      {
        if (!tournamentHasTeamListOld.contains(tournamentHasTeamListNewTournamentHasTeam))
        {
          Team oldTeamOfTournamentHasTeamListNewTournamentHasTeam = tournamentHasTeamListNewTournamentHasTeam.getTeam();
          tournamentHasTeamListNewTournamentHasTeam.setTeam(team);
          tournamentHasTeamListNewTournamentHasTeam = em.merge(tournamentHasTeamListNewTournamentHasTeam);
          if (oldTeamOfTournamentHasTeamListNewTournamentHasTeam != null && !oldTeamOfTournamentHasTeamListNewTournamentHasTeam.equals(team))
          {
            oldTeamOfTournamentHasTeamListNewTournamentHasTeam.getTournamentHasTeamList().remove(tournamentHasTeamListNewTournamentHasTeam);
            oldTeamOfTournamentHasTeamListNewTournamentHasTeam = em.merge(oldTeamOfTournamentHasTeamListNewTournamentHasTeam);
          }
        }
      }
      for (TeamHasFormatRecord teamHasFormatRecordListNewTeamHasFormatRecord : teamHasFormatRecordListNew)
      {
        if (!teamHasFormatRecordListOld.contains(teamHasFormatRecordListNewTeamHasFormatRecord))
        {
          Team oldTeamOfTeamHasFormatRecordListNewTeamHasFormatRecord = teamHasFormatRecordListNewTeamHasFormatRecord.getTeam();
          teamHasFormatRecordListNewTeamHasFormatRecord.setTeam(team);
          teamHasFormatRecordListNewTeamHasFormatRecord = em.merge(teamHasFormatRecordListNewTeamHasFormatRecord);
          if (oldTeamOfTeamHasFormatRecordListNewTeamHasFormatRecord != null && !oldTeamOfTeamHasFormatRecordListNewTeamHasFormatRecord.equals(team))
          {
            oldTeamOfTeamHasFormatRecordListNewTeamHasFormatRecord.getTeamHasFormatRecordList().remove(teamHasFormatRecordListNewTeamHasFormatRecord);
            oldTeamOfTeamHasFormatRecordListNewTeamHasFormatRecord = em.merge(oldTeamOfTeamHasFormatRecordListNewTeamHasFormatRecord);
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
        Integer id = team.getId();
        if (findTeam(id) == null)
        {
          throw new NonexistentEntityException("The team with id " + id + " no longer exists.");
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
      Team team;
      try
      {
        team = em.getReference(Team.class, id);
        team.getId();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The team with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<MatchHasTeam> matchHasTeamListOrphanCheck = team.getMatchHasTeamList();
      for (MatchHasTeam matchHasTeamListOrphanCheckMatchHasTeam : matchHasTeamListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Team (" + team + ") cannot be destroyed since the MatchHasTeam " + matchHasTeamListOrphanCheckMatchHasTeam + " in its matchHasTeamList field has a non-nullable team field.");
      }
      List<TournamentHasTeam> tournamentHasTeamListOrphanCheck = team.getTournamentHasTeamList();
      for (TournamentHasTeam tournamentHasTeamListOrphanCheckTournamentHasTeam : tournamentHasTeamListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Team (" + team + ") cannot be destroyed since the TournamentHasTeam " + tournamentHasTeamListOrphanCheckTournamentHasTeam + " in its tournamentHasTeamList field has a non-nullable team field.");
      }
      List<TeamHasFormatRecord> teamHasFormatRecordListOrphanCheck = team.getTeamHasFormatRecordList();
      for (TeamHasFormatRecord teamHasFormatRecordListOrphanCheckTeamHasFormatRecord : teamHasFormatRecordListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Team (" + team + ") cannot be destroyed since the TeamHasFormatRecord " + teamHasFormatRecordListOrphanCheckTeamHasFormatRecord + " in its teamHasFormatRecordList field has a non-nullable team field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      List<Player> playerList = team.getPlayerList();
      for (Player playerListPlayer : playerList)
      {
        playerListPlayer.getTeamList().remove(team);
        playerListPlayer = em.merge(playerListPlayer);
      }
      em.remove(team);
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

  public List<Team> findTeamEntities()
  {
    return findTeamEntities(true, -1, -1);
  }

  public List<Team> findTeamEntities(int maxResults, int firstResult)
  {
    return findTeamEntities(false, maxResults, firstResult);
  }

  private List<Team> findTeamEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Team.class));
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

  public Team findTeam(Integer id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(Team.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getTeamCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Team> rt = cq.from(Team.class);
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
