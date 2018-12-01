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
import com.github.javydreamercsw.database.storage.db.FormatPK;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class FormatJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = 3999624860758827889L;
  public FormatJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(Format format) throws PreexistingEntityException, Exception
  {
    if (format.getFormatPK() == null)
    {
      format.setFormatPK(new FormatPK());
    }
    if (format.getMatchEntryList() == null)
    {
      format.setMatchEntryList(new ArrayList<>());
    }
    format.getFormatPK().setGameId(format.getGame().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Game game = format.getGame();
      if (game != null)
      {
        game = em.getReference(game.getClass(), game.getId());
        format.setGame(game);
      }
      List<MatchEntry> attachedMatchEntryList = new ArrayList<>();
      for (MatchEntry matchEntryListMatchEntryToAttach : format.getMatchEntryList())
      {
        matchEntryListMatchEntryToAttach = em.getReference(matchEntryListMatchEntryToAttach.getClass(), matchEntryListMatchEntryToAttach.getMatchEntryPK());
        attachedMatchEntryList.add(matchEntryListMatchEntryToAttach);
      }
      format.setMatchEntryList(attachedMatchEntryList);
      em.persist(format);
      if (game != null)
      {
        game.getFormatList().add(format);
        game = em.merge(game);
      }
      for (MatchEntry matchEntryListMatchEntry : format.getMatchEntryList())
      {
        Format oldFormatOfMatchEntryListMatchEntry = matchEntryListMatchEntry.getFormat();
        matchEntryListMatchEntry.setFormat(format);
        matchEntryListMatchEntry = em.merge(matchEntryListMatchEntry);
        if (oldFormatOfMatchEntryListMatchEntry != null)
        {
          oldFormatOfMatchEntryListMatchEntry.getMatchEntryList().remove(matchEntryListMatchEntry);
          oldFormatOfMatchEntryListMatchEntry = em.merge(oldFormatOfMatchEntryListMatchEntry);
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findFormat(format.getFormatPK()) != null)
      {
        throw new PreexistingEntityException("Format " + format + " already exists.", ex);
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

  public void edit(Format format) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    format.getFormatPK().setGameId(format.getGame().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Format persistentFormat = em.find(Format.class, format.getFormatPK());
      Game gameOld = persistentFormat.getGame();
      Game gameNew = format.getGame();
      List<MatchEntry> matchEntryListOld = persistentFormat.getMatchEntryList();
      List<MatchEntry> matchEntryListNew = format.getMatchEntryList();
      List<String> illegalOrphanMessages = null;
      for (MatchEntry matchEntryListOldMatchEntry : matchEntryListOld)
      {
        if (!matchEntryListNew.contains(matchEntryListOldMatchEntry))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain MatchEntry " + matchEntryListOldMatchEntry + " since its format field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      if (gameNew != null)
      {
        gameNew = em.getReference(gameNew.getClass(), gameNew.getId());
        format.setGame(gameNew);
      }
      List<MatchEntry> attachedMatchEntryListNew = new ArrayList<>();
      for (MatchEntry matchEntryListNewMatchEntryToAttach : matchEntryListNew)
      {
        matchEntryListNewMatchEntryToAttach = em.getReference(matchEntryListNewMatchEntryToAttach.getClass(), matchEntryListNewMatchEntryToAttach.getMatchEntryPK());
        attachedMatchEntryListNew.add(matchEntryListNewMatchEntryToAttach);
      }
      matchEntryListNew = attachedMatchEntryListNew;
      format.setMatchEntryList(matchEntryListNew);
      format = em.merge(format);
      if (gameOld != null && !gameOld.equals(gameNew))
      {
        gameOld.getFormatList().remove(format);
        gameOld = em.merge(gameOld);
      }
      if (gameNew != null && !gameNew.equals(gameOld))
      {
        gameNew.getFormatList().add(format);
        gameNew = em.merge(gameNew);
      }
      for (MatchEntry matchEntryListNewMatchEntry : matchEntryListNew)
      {
        if (!matchEntryListOld.contains(matchEntryListNewMatchEntry))
        {
          Format oldFormatOfMatchEntryListNewMatchEntry = matchEntryListNewMatchEntry.getFormat();
          matchEntryListNewMatchEntry.setFormat(format);
          matchEntryListNewMatchEntry = em.merge(matchEntryListNewMatchEntry);
          if (oldFormatOfMatchEntryListNewMatchEntry != null && !oldFormatOfMatchEntryListNewMatchEntry.equals(format))
          {
            oldFormatOfMatchEntryListNewMatchEntry.getMatchEntryList().remove(matchEntryListNewMatchEntry);
            oldFormatOfMatchEntryListNewMatchEntry = em.merge(oldFormatOfMatchEntryListNewMatchEntry);
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
        FormatPK id = format.getFormatPK();
        if (findFormat(id) == null)
        {
          throw new NonexistentEntityException("The format with id " + id + " no longer exists.");
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

  public void destroy(FormatPK id) throws IllegalOrphanException, NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Format format;
      try
      {
        format = em.getReference(Format.class, id);
        format.getFormatPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The format with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<MatchEntry> matchEntryListOrphanCheck = format.getMatchEntryList();
      for (MatchEntry matchEntryListOrphanCheckMatchEntry : matchEntryListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This Format (" + format + ") cannot be destroyed since the MatchEntry " + matchEntryListOrphanCheckMatchEntry + " in its matchEntryList field has a non-nullable format field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      Game game = format.getGame();
      if (game != null)
      {
        game.getFormatList().remove(format);
        game = em.merge(game);
      }
      em.remove(format);
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

  public List<Format> findFormatEntities()
  {
    return findFormatEntities(true, -1, -1);
  }

  public List<Format> findFormatEntities(int maxResults, int firstResult)
  {
    return findFormatEntities(false, maxResults, firstResult);
  }

  private List<Format> findFormatEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(Format.class));
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

  public Format findFormat(FormatPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(Format.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getFormatCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<Format> rt = cq.from(Format.class);
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
