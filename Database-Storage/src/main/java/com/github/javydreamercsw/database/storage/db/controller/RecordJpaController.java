/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.RecordPK;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class RecordJpaController implements Serializable
{
  private static final long serialVersionUID = 7601923040598485026L;
  public RecordJpaController(EntityManagerFactory emf)
  {
    this.emf = emf;
  }
  private EntityManagerFactory emf = null;

  public EntityManager getEntityManager()
  {
    return emf.createEntityManager();
  }

  public void create(Record record) throws PreexistingEntityException, Exception
  {
    if (record.getRecordPK() == null)
    {
      record.setRecordPK(new RecordPK());
    }
    if (record.getPlayerList() == null)
    {
      record.setPlayerList(new ArrayList<>());
    }
    if (record.getTournamentHasTeamList() == null)
    {
      record.setTournamentHasTeamList(new ArrayList<>());
    }
    record.getRecordPK().setGameId(record.getGame().getId());
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
      List<Player> attachedPlayerList = new ArrayList<>();
      for (Player playerListPlayerToAttach : record.getPlayerList())
      {
        playerListPlayerToAttach = em.getReference(playerListPlayerToAttach.getClass(), playerListPlayerToAttach.getId());
        attachedPlayerList.add(playerListPlayerToAttach);
      }
      record.setPlayerList(attachedPlayerList);
      List<TournamentHasTeam> attachedTournamentHasTeamList = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeamToAttach : record.getTournamentHasTeamList())
      {
        tournamentHasTeamListTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListTournamentHasTeamToAttach.getClass(), tournamentHasTeamListTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamList.add(tournamentHasTeamListTournamentHasTeamToAttach);
      }
      record.setTournamentHasTeamList(attachedTournamentHasTeamList);
      em.persist(record);
      if (game != null)
      {
        game.getRecordList().add(record);
        game = em.merge(game);
      }
      for (Player playerListPlayer : record.getPlayerList())
      {
        playerListPlayer.getRecordList().add(record);
        playerListPlayer = em.merge(playerListPlayer);
      }
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeam : record.getTournamentHasTeamList())
      {
        tournamentHasTeamListTournamentHasTeam.getRecordList().add(record);
        tournamentHasTeamListTournamentHasTeam = em.merge(tournamentHasTeamListTournamentHasTeam);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findRecord(record.getRecordPK()) != null)
      {
        throw new PreexistingEntityException("Record " + record + " already exists.", ex);
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

  public void edit(Record record) throws NonexistentEntityException, Exception
  {
    record.getRecordPK().setGameId(record.getGame().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Record persistentRecord = em.find(Record.class, record.getRecordPK());
      Game gameOld = persistentRecord.getGame();
      Game gameNew = record.getGame();
      List<Player> playerListOld = persistentRecord.getPlayerList();
      List<Player> playerListNew = record.getPlayerList();
      List<TournamentHasTeam> tournamentHasTeamListOld = persistentRecord.getTournamentHasTeamList();
      List<TournamentHasTeam> tournamentHasTeamListNew = record.getTournamentHasTeamList();
      if (gameNew != null)
      {
        gameNew = em.getReference(gameNew.getClass(), gameNew.getId());
        record.setGame(gameNew);
      }
      List<Player> attachedPlayerListNew = new ArrayList<>();
      for (Player playerListNewPlayerToAttach : playerListNew)
      {
        playerListNewPlayerToAttach = em.getReference(playerListNewPlayerToAttach.getClass(), playerListNewPlayerToAttach.getId());
        attachedPlayerListNew.add(playerListNewPlayerToAttach);
      }
      playerListNew = attachedPlayerListNew;
      record.setPlayerList(playerListNew);
      List<TournamentHasTeam> attachedTournamentHasTeamListNew = new ArrayList<>();
      for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeamToAttach : tournamentHasTeamListNew)
      {
        tournamentHasTeamListNewTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListNewTournamentHasTeamToAttach.getClass(), tournamentHasTeamListNewTournamentHasTeamToAttach.getTournamentHasTeamPK());
        attachedTournamentHasTeamListNew.add(tournamentHasTeamListNewTournamentHasTeamToAttach);
      }
      tournamentHasTeamListNew = attachedTournamentHasTeamListNew;
      record.setTournamentHasTeamList(tournamentHasTeamListNew);
      record = em.merge(record);
      if (gameOld != null && !gameOld.equals(gameNew))
      {
        gameOld.getRecordList().remove(record);
        gameOld = em.merge(gameOld);
      }
      if (gameNew != null && !gameNew.equals(gameOld))
      {
        gameNew.getRecordList().add(record);
        gameNew = em.merge(gameNew);
      }
      for (Player playerListOldPlayer : playerListOld)
      {
        if (!playerListNew.contains(playerListOldPlayer))
        {
          playerListOldPlayer.getRecordList().remove(record);
          playerListOldPlayer = em.merge(playerListOldPlayer);
        }
      }
      for (Player playerListNewPlayer : playerListNew)
      {
        if (!playerListOld.contains(playerListNewPlayer))
        {
          playerListNewPlayer.getRecordList().add(record);
          playerListNewPlayer = em.merge(playerListNewPlayer);
        }
      }
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
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        RecordPK id = record.getRecordPK();
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

  public void destroy(RecordPK id) throws NonexistentEntityException
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
        record.getRecordPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The record with id " + id + " no longer exists.", enfe);
      }
      Game game = record.getGame();
      if (game != null)
      {
        game.getRecordList().remove(record);
        game = em.merge(game);
      }
      List<Player> playerList = record.getPlayerList();
      for (Player playerListPlayer : playerList)
      {
        playerListPlayer.getRecordList().remove(record);
        playerListPlayer = em.merge(playerListPlayer);
      }
      List<TournamentHasTeam> tournamentHasTeamList = record.getTournamentHasTeamList();
      for (TournamentHasTeam tournamentHasTeamListTournamentHasTeam : tournamentHasTeamList)
      {
        tournamentHasTeamListTournamentHasTeam.getRecordList().remove(record);
        tournamentHasTeamListTournamentHasTeam = em.merge(tournamentHasTeamListTournamentHasTeam);
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

  public Record findRecord(RecordPK id)
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
