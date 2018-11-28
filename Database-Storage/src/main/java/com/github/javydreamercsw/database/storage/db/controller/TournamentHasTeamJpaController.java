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

import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.Record;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeamPK;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class TournamentHasTeamJpaController implements Serializable
{
  public TournamentHasTeamJpaController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager()
  {
    return emf.createEntityManager();
  }

  public void create(TournamentHasTeam tournamentHasTeam) throws PreexistingEntityException, Exception
  {
    if (tournamentHasTeam.getTournamentHasTeamPK() == null)
    {
      tournamentHasTeam.setTournamentHasTeamPK(new TournamentHasTeamPK());
    }
    if (tournamentHasTeam.getRecordList() == null)
    {
      tournamentHasTeam.setRecordList(new ArrayList<Record>());
    }
    tournamentHasTeam.getTournamentHasTeamPK().setTournamentId(tournamentHasTeam.getTournament().getId());
    tournamentHasTeam.getTournamentHasTeamPK().setTeamId(tournamentHasTeam.getTeam().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Team team = tournamentHasTeam.getTeam();
      if (team != null)
      {
        team = em.getReference(team.getClass(), team.getId());
        tournamentHasTeam.setTeam(team);
      }
      Tournament tournament = tournamentHasTeam.getTournament();
      if (tournament != null)
      {
        tournament = em.getReference(tournament.getClass(), tournament.getId());
        tournamentHasTeam.setTournament(tournament);
      }
      List<Record> attachedRecordList = new ArrayList<Record>();
      for (Record recordListRecordToAttach : tournamentHasTeam.getRecordList())
      {
        recordListRecordToAttach = em.getReference(recordListRecordToAttach.getClass(), recordListRecordToAttach.getId());
        attachedRecordList.add(recordListRecordToAttach);
      }
      tournamentHasTeam.setRecordList(attachedRecordList);
      em.persist(tournamentHasTeam);
      if (team != null)
      {
        team.getTournamentHasTeamList().add(tournamentHasTeam);
        team = em.merge(team);
      }
      if (tournament != null)
      {
        tournament.getTournamentHasTeamList().add(tournamentHasTeam);
        tournament = em.merge(tournament);
      }
      for (Record recordListRecord : tournamentHasTeam.getRecordList())
      {
        recordListRecord.getTournamentHasTeamList().add(tournamentHasTeam);
        recordListRecord = em.merge(recordListRecord);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findTournamentHasTeam(tournamentHasTeam.getTournamentHasTeamPK()) != null)
      {
        throw new PreexistingEntityException("TournamentHasTeam " + tournamentHasTeam + " already exists.", ex);
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

  public void edit(TournamentHasTeam tournamentHasTeam) throws NonexistentEntityException, Exception
  {
    tournamentHasTeam.getTournamentHasTeamPK().setTournamentId(tournamentHasTeam.getTournament().getId());
    tournamentHasTeam.getTournamentHasTeamPK().setTeamId(tournamentHasTeam.getTeam().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      TournamentHasTeam persistentTournamentHasTeam = em.find(TournamentHasTeam.class, tournamentHasTeam.getTournamentHasTeamPK());
      Team teamOld = persistentTournamentHasTeam.getTeam();
      Team teamNew = tournamentHasTeam.getTeam();
      Tournament tournamentOld = persistentTournamentHasTeam.getTournament();
      Tournament tournamentNew = tournamentHasTeam.getTournament();
      List<Record> recordListOld = persistentTournamentHasTeam.getRecordList();
      List<Record> recordListNew = tournamentHasTeam.getRecordList();
      if (teamNew != null)
      {
        teamNew = em.getReference(teamNew.getClass(), teamNew.getId());
        tournamentHasTeam.setTeam(teamNew);
      }
      if (tournamentNew != null)
      {
        tournamentNew = em.getReference(tournamentNew.getClass(), tournamentNew.getId());
        tournamentHasTeam.setTournament(tournamentNew);
      }
      List<Record> attachedRecordListNew = new ArrayList<Record>();
      for (Record recordListNewRecordToAttach : recordListNew)
      {
        recordListNewRecordToAttach = em.getReference(recordListNewRecordToAttach.getClass(), recordListNewRecordToAttach.getId());
        attachedRecordListNew.add(recordListNewRecordToAttach);
      }
      recordListNew = attachedRecordListNew;
      tournamentHasTeam.setRecordList(recordListNew);
      tournamentHasTeam = em.merge(tournamentHasTeam);
      if (teamOld != null && !teamOld.equals(teamNew))
      {
        teamOld.getTournamentHasTeamList().remove(tournamentHasTeam);
        teamOld = em.merge(teamOld);
      }
      if (teamNew != null && !teamNew.equals(teamOld))
      {
        teamNew.getTournamentHasTeamList().add(tournamentHasTeam);
        teamNew = em.merge(teamNew);
      }
      if (tournamentOld != null && !tournamentOld.equals(tournamentNew))
      {
        tournamentOld.getTournamentHasTeamList().remove(tournamentHasTeam);
        tournamentOld = em.merge(tournamentOld);
      }
      if (tournamentNew != null && !tournamentNew.equals(tournamentOld))
      {
        tournamentNew.getTournamentHasTeamList().add(tournamentHasTeam);
        tournamentNew = em.merge(tournamentNew);
      }
      for (Record recordListOldRecord : recordListOld)
      {
        if (!recordListNew.contains(recordListOldRecord))
        {
          recordListOldRecord.getTournamentHasTeamList().remove(tournamentHasTeam);
          recordListOldRecord = em.merge(recordListOldRecord);
        }
      }
      for (Record recordListNewRecord : recordListNew)
      {
        if (!recordListOld.contains(recordListNewRecord))
        {
          recordListNewRecord.getTournamentHasTeamList().add(tournamentHasTeam);
          recordListNewRecord = em.merge(recordListNewRecord);
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        TournamentHasTeamPK id = tournamentHasTeam.getTournamentHasTeamPK();
        if (findTournamentHasTeam(id) == null)
        {
          throw new NonexistentEntityException("The tournamentHasTeam with id " + id + " no longer exists.");
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

  public void destroy(TournamentHasTeamPK id) throws NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      TournamentHasTeam tournamentHasTeam;
      try
      {
        tournamentHasTeam = em.getReference(TournamentHasTeam.class, id);
        tournamentHasTeam.getTournamentHasTeamPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The tournamentHasTeam with id " + id + " no longer exists.", enfe);
      }
      Team team = tournamentHasTeam.getTeam();
      if (team != null)
      {
        team.getTournamentHasTeamList().remove(tournamentHasTeam);
        team = em.merge(team);
      }
      Tournament tournament = tournamentHasTeam.getTournament();
      if (tournament != null)
      {
        tournament.getTournamentHasTeamList().remove(tournamentHasTeam);
        tournament = em.merge(tournament);
      }
      List<Record> recordList = tournamentHasTeam.getRecordList();
      for (Record recordListRecord : recordList)
      {
        recordListRecord.getTournamentHasTeamList().remove(tournamentHasTeam);
        recordListRecord = em.merge(recordListRecord);
      }
      em.remove(tournamentHasTeam);
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

  public List<TournamentHasTeam> findTournamentHasTeamEntities()
  {
    return findTournamentHasTeamEntities(true, -1, -1);
  }

  public List<TournamentHasTeam> findTournamentHasTeamEntities(int maxResults, int firstResult)
  {
    return findTournamentHasTeamEntities(false, maxResults, firstResult);
  }

  private List<TournamentHasTeam> findTournamentHasTeamEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(TournamentHasTeam.class));
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

  public TournamentHasTeam findTournamentHasTeam(TournamentHasTeamPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(TournamentHasTeam.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getTournamentHasTeamCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<TournamentHasTeam> rt = cq.from(TournamentHasTeam.class);
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