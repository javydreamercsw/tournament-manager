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

import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class RecordJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = -5982649688889824490L;
  
  public RecordJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(Record record)
  {
    if (record.getTournamentHasTeamList() == null)
    {
      record.setTournamentHasTeamList(new ArrayList<>());
    }
    if (record.getTeamList() == null)
    {
      record.setTeamList(new ArrayList<>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Game game = record.getGame();
      if (game != null)
      {
        game = em.getReference(game.getClass(), game.getId());
        record.setGame(game);
      }
      List<TournamentHasTeam> attachedTournamentHasTeamList = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeamToAttach : record.getTournamentHasTeamList())
      {
        tournamentHasTeamListTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListTournamentHasTeamToAttach.getClass(), tournamentHasTeamListTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamList.add(tournamentHasTeamListTournamentHasTeamToAttach);
      }
      record.setTournamentHasTeamList(attachedTournamentHasTeamList);
      List<Team> attachedTeamList = new ArrayList<>();
      for (Team teamListTeamToAttach : record.getTeamList())
      {
        teamListTeamToAttach = em.getReference(teamListTeamToAttach.getClass(), teamListTeamToAttach.getId());
        attachedTeamList.add(teamListTeamToAttach);
      }
      record.setTeamList(attachedTeamList);
      em.persist(record);
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeam : record.getTournamentHasTeamList())
      {
        tournamentHasTeamListTournamentHasTeam.getRecordList().add(record);
        tournamentHasTeamListTournamentHasTeam = em.merge(tournamentHasTeamListTournamentHasTeam);
      }
      for (Team teamListTeam : record.getTeamList())
      {
        teamListTeam.getRecordList().add(record);
        teamListTeam = em.merge(teamListTeam);
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

  public void edit(Record record) throws NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Record persistentRecord = em.find(Record.class, record.getId());
      List<TournamentHasTeam> tournamentHasTeamListOld = persistentRecord.getTournamentHasTeamList();
      List<TournamentHasTeam> tournamentHasTeamListNew = record.getTournamentHasTeamList();
      List<Team> teamListOld = persistentRecord.getTeamList();
      List<Team> teamListNew = record.getTeamList();
      List<TournamentHasTeam> attachedTournamentHasTeamListNew = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeamToAttach : tournamentHasTeamListNew)
      {
        tournamentHasTeamListNewTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListNewTournamentHasTeamToAttach.getClass(), tournamentHasTeamListNewTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamListNew.add(tournamentHasTeamListNewTournamentHasTeamToAttach);
      }
      tournamentHasTeamListNew = attachedTournamentHasTeamListNew;
      record.setTournamentHasTeamList(tournamentHasTeamListNew);
      List<Team> attachedTeamListNew = new ArrayList<>();
      for (Team teamListNewTeamToAttach : teamListNew)
      {
        teamListNewTeamToAttach = em.getReference(teamListNewTeamToAttach.getClass(), teamListNewTeamToAttach.getId());
        attachedTeamListNew.add(teamListNewTeamToAttach);
      }
      teamListNew = attachedTeamListNew;
      record.setTeamList(teamListNew);
      record = em.merge(record);
      for (TournamentHasTeam tournamentHasTeamListOldTournamentHasTeam : tournamentHasTeamListOld)
      {
        if (!tournamentHasTeamListNew.contains(tournamentHasTeamListOldTournamentHasTeam))
        {
          tournamentHasTeamListOldTournamentHasTeam.getRecordList().remove(record);
          tournamentHasTeamListOldTournamentHasTeam = em.merge(tournamentHasTeamListOldTournamentHasTeam);
        }
      }
      for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeam : tournamentHasTeamListNew)
      {
        if (!tournamentHasTeamListOld.contains(tournamentHasTeamListNewTournamentHasTeam))
        {
          tournamentHasTeamListNewTournamentHasTeam.getRecordList().add(record);
          tournamentHasTeamListNewTournamentHasTeam = em.merge(tournamentHasTeamListNewTournamentHasTeam);
        }
      }
      for (Team teamListOldTeam : teamListOld)
      {
        if (!teamListNew.contains(teamListOldTeam))
        {
          teamListOldTeam.getRecordList().remove(record);
          teamListOldTeam = em.merge(teamListOldTeam);
        }
      }
      for (Team teamListNewTeam : teamListNew)
      {
        if (!teamListOld.contains(teamListNewTeam))
        {
          teamListNewTeam.getRecordList().add(record);
          teamListNewTeam = em.merge(teamListNewTeam);
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        Integer id = record.getId();
        if (findRecord(id) == null)
        {
          throw new NonexistentEntityException("The record with id " + id + " no longer exists.");
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

  public void destroy(Integer id) throws NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Record record;
      try
      {
        record = em.getReference(Record.class, id);
        record.getId();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The record with id " + id + " no longer exists.", enfe);
      }
      List<TournamentHasTeam> tournamentHasTeamList = record.getTournamentHasTeamList();
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeam : tournamentHasTeamList)
      {
        tournamentHasTeamListTournamentHasTeam.getRecordList().remove(record);
        tournamentHasTeamListTournamentHasTeam = em.merge(tournamentHasTeamListTournamentHasTeam);
      }
      List<Team> teamList = record.getTeamList();
      for (Team teamListTeam : teamList)
      {
        teamListTeam.getRecordList().remove(record);
        teamListTeam = em.merge(teamListTeam);
      }
      em.remove(record);
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

  public List<Record> findRecordEntities()
  {
    return findRecordEntities(true, -1, -1);
  }

  public List<Record> findRecordEntities(int maxResults, int firstResult)
  {
    return findRecordEntities(false, maxResults, firstResult);
  }

  private List<Record> findRecordEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Record.class));
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

  public Record findRecord(Integer id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(Record.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getRecordCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Record> rt = cq.from(Record.class);
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
