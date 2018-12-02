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

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class GameJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = -3638967753299400612L;
  public GameJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(Game game)
  {
    if (game.getFormatList() == null)
    {
      game.setFormatList(new ArrayList<>());
    }
    if (game.getRecordList() == null)
    {
      game.setRecordList(new ArrayList<>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      List<Format> attachedFormatList = new ArrayList<>();
      for (Format formatListFormatToAttach : game.getFormatList())
      {
        formatListFormatToAttach = em.getReference(formatListFormatToAttach.getClass(), formatListFormatToAttach.getFormatPK());
        attachedFormatList.add(formatListFormatToAttach);
      }
      game.setFormatList(attachedFormatList);
      List<Record> attachedRecordList = new ArrayList<>();
      for (Record recordListRecordToAttach : game.getRecordList())
      {
        recordListRecordToAttach = em.getReference(recordListRecordToAttach.getClass(), recordListRecordToAttach.getRecordPK());
        attachedRecordList.add(recordListRecordToAttach);
      }
      game.setRecordList(attachedRecordList);
      em.persist(game);
      for (Format formatListFormat : game.getFormatList())
      {
        Game oldGameOfFormatListFormat = formatListFormat.getGame();
        formatListFormat.setGame(game);
        formatListFormat = em.merge(formatListFormat);
        if (oldGameOfFormatListFormat != null)
        {
          oldGameOfFormatListFormat.getFormatList().remove(formatListFormat);
          oldGameOfFormatListFormat = em.merge(oldGameOfFormatListFormat);
        }
      }
      for (Record recordListRecord : game.getRecordList())
      {
        Game oldGameOfRecordListRecord = recordListRecord.getGame();
        recordListRecord.setGame(game);
        recordListRecord = em.merge(recordListRecord);
        if (oldGameOfRecordListRecord != null)
        {
          oldGameOfRecordListRecord.getRecordList().remove(recordListRecord);
          oldGameOfRecordListRecord = em.merge(oldGameOfRecordListRecord);
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

  public void edit(Game game) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Game persistentGame = em.find(Game.class, game.getId());
      List<Format> formatListOld = persistentGame.getFormatList();
      List<Format> formatListNew = game.getFormatList();
      List<Record> recordListOld = persistentGame.getRecordList();
      List<Record> recordListNew = game.getRecordList();
      List<String> illegalOrphanMessages = null;
      for (Format formatListOldFormat : formatListOld)
      {
        if (!formatListNew.contains(formatListOldFormat))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain Format " + formatListOldFormat + " since its game field is not nullable.");
        }
      }
      for (Record recordListOldRecord : recordListOld)
      {
        if (!recordListNew.contains(recordListOldRecord))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain Record " + recordListOldRecord + " since its game field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      List<Format> attachedFormatListNew = new ArrayList<>();
      for (Format formatListNewFormatToAttach : formatListNew)
      {
        formatListNewFormatToAttach = em.getReference(formatListNewFormatToAttach.getClass(), formatListNewFormatToAttach.getFormatPK());
        attachedFormatListNew.add(formatListNewFormatToAttach);
      }
      formatListNew = attachedFormatListNew;
      game.setFormatList(formatListNew);
      List<Record> attachedRecordListNew = new ArrayList<>();
      for (Record recordListNewRecordToAttach : recordListNew)
      {
        recordListNewRecordToAttach = em.getReference(recordListNewRecordToAttach.getClass(), recordListNewRecordToAttach.getRecordPK());
        attachedRecordListNew.add(recordListNewRecordToAttach);
      }
      recordListNew = attachedRecordListNew;
      game.setRecordList(recordListNew);
      game = em.merge(game);
      for (Format formatListNewFormat : formatListNew)
      {
        if (!formatListOld.contains(formatListNewFormat))
        {
          Game oldGameOfFormatListNewFormat = formatListNewFormat.getGame();
          formatListNewFormat.setGame(game);
          formatListNewFormat = em.merge(formatListNewFormat);
          if (oldGameOfFormatListNewFormat != null && !oldGameOfFormatListNewFormat.equals(game))
          {
            oldGameOfFormatListNewFormat.getFormatList().remove(formatListNewFormat);
            oldGameOfFormatListNewFormat = em.merge(oldGameOfFormatListNewFormat);
          }
        }
      }
      for (Record recordListNewRecord : recordListNew)
      {
        if (!recordListOld.contains(recordListNewRecord))
        {
          Game oldGameOfRecordListNewRecord = recordListNewRecord.getGame();
          recordListNewRecord.setGame(game);
          recordListNewRecord = em.merge(recordListNewRecord);
          if (oldGameOfRecordListNewRecord != null && !oldGameOfRecordListNewRecord.equals(game))
          {
            oldGameOfRecordListNewRecord.getRecordList().remove(recordListNewRecord);
            oldGameOfRecordListNewRecord = em.merge(oldGameOfRecordListNewRecord);
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
        Integer id = game.getId();
        if (findGame(id) == null)
        {
          throw new NonexistentEntityException("The game with id " + id + " no longer exists.");
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
      Game game;
      try
      {
        game = em.getReference(Game.class, id);
        game.getId();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The game with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<Format> formatListOrphanCheck = game.getFormatList();
      for (Format formatListOrphanCheckFormat : formatListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Game (" + game + ") cannot be destroyed since the Format " + formatListOrphanCheckFormat + " in its formatList field has a non-nullable game field.");
      }
      List<Record> recordListOrphanCheck = game.getRecordList();
      for (Record recordListOrphanCheckRecord : recordListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Game (" + game + ") cannot be destroyed since the Record " + recordListOrphanCheckRecord + " in its recordList field has a non-nullable game field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      em.remove(game);
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

  public List<Game> findGameEntities()
  {
    return findGameEntities(true, -1, -1);
  }

  public List<Game> findGameEntities(int maxResults, int firstResult)
  {
    return findGameEntities(false, maxResults, firstResult);
  }

  private List<Game> findGameEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Game.class));
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

  public Game findGame(Integer id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(Game.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getGameCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Game> rt = cq.from(Game.class);
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
