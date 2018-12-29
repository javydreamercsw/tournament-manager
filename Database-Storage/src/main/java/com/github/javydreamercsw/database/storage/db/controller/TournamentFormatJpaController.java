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

import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentFormat;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class TournamentFormatJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = 8124092964263662804L;

  public TournamentFormatJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(TournamentFormat tournamentFormat) throws PreexistingEntityException, Exception
  {
    if (tournamentFormat.getTournamentList() == null)
    {
      tournamentFormat.setTournamentList(new ArrayList<>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      List<Tournament> attachedTournamentList = new ArrayList<>();
      for (Tournament tournamentListTournamentToAttach : tournamentFormat.getTournamentList())
      {
        tournamentListTournamentToAttach = em.getReference(tournamentListTournamentToAttach.getClass(), tournamentListTournamentToAttach.getTournamentPK());
        attachedTournamentList.add(tournamentListTournamentToAttach);
      }
      tournamentFormat.setTournamentList(attachedTournamentList);
      em.persist(tournamentFormat);
      for (Tournament tournamentListTournament : tournamentFormat.getTournamentList())
      {
        TournamentFormat oldTournamentFormatOfTournamentListTournament = tournamentListTournament.getTournamentFormat();
        tournamentListTournament.setTournamentFormat(tournamentFormat);
        tournamentListTournament = em.merge(tournamentListTournament);
        if (oldTournamentFormatOfTournamentListTournament != null)
        {
          oldTournamentFormatOfTournamentListTournament.getTournamentList().remove(tournamentListTournament);
          oldTournamentFormatOfTournamentListTournament = em.merge(oldTournamentFormatOfTournamentListTournament);
        }
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (tournamentFormat.getId() != null
              && findTournamentFormat(tournamentFormat.getId()) != null)
      {
        throw new PreexistingEntityException("TournamentFormat " + tournamentFormat + " already exists.", ex);
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

  public void edit(TournamentFormat tournamentFormat) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      TournamentFormat persistentTournamentFormat = em.find(TournamentFormat.class, tournamentFormat.getId());
      List<Tournament> tournamentListOld = persistentTournamentFormat.getTournamentList();
      List<Tournament> tournamentListNew = tournamentFormat.getTournamentList();
      List<String> illegalOrphanMessages = null;
      for (Tournament tournamentListOldTournament : tournamentListOld)
      {
        if (!tournamentListNew.contains(tournamentListOldTournament))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain Tournament " + tournamentListOldTournament + " since its tournamentFormat field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      List<Tournament> attachedTournamentListNew = new ArrayList<>();
      for (Tournament tournamentListNewTournamentToAttach : tournamentListNew)
      {
        tournamentListNewTournamentToAttach = em.getReference(tournamentListNewTournamentToAttach.getClass(), tournamentListNewTournamentToAttach.getTournamentPK());
        attachedTournamentListNew.add(tournamentListNewTournamentToAttach);
      }
      tournamentListNew = attachedTournamentListNew;
      tournamentFormat.setTournamentList(tournamentListNew);
      tournamentFormat = em.merge(tournamentFormat);
      for (Tournament tournamentListNewTournament : tournamentListNew)
      {
        if (!tournamentListOld.contains(tournamentListNewTournament))
        {
          TournamentFormat oldTournamentFormatOfTournamentListNewTournament = tournamentListNewTournament.getTournamentFormat();
          tournamentListNewTournament.setTournamentFormat(tournamentFormat);
          tournamentListNewTournament = em.merge(tournamentListNewTournament);
          if (oldTournamentFormatOfTournamentListNewTournament != null && !oldTournamentFormatOfTournamentListNewTournament.equals(tournamentFormat))
          {
            oldTournamentFormatOfTournamentListNewTournament.getTournamentList().remove(tournamentListNewTournament);
            oldTournamentFormatOfTournamentListNewTournament = em.merge(oldTournamentFormatOfTournamentListNewTournament);
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
        Integer id = tournamentFormat.getId();
        if (findTournamentFormat(id) == null)
        {
          throw new NonexistentEntityException("The tournamentFormat with id " + id + " no longer exists.");
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
      TournamentFormat tournamentFormat;
      try
      {
        tournamentFormat = em.getReference(TournamentFormat.class, id);
        tournamentFormat.getId();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The tournamentFormat with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<Tournament> tournamentListOrphanCheck = tournamentFormat.getTournamentList();
      for (Tournament tournamentListOrphanCheckTournament : tournamentListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This TournamentFormat (" + tournamentFormat + ") cannot be destroyed since the Tournament " + tournamentListOrphanCheckTournament + " in its tournamentList field has a non-nullable tournamentFormat field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      em.remove(tournamentFormat);
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

  public List<TournamentFormat> findTournamentFormatEntities()
  {
    return findTournamentFormatEntities(true, -1, -1);
  }

  public List<TournamentFormat> findTournamentFormatEntities(int maxResults, int firstResult)
  {
    return findTournamentFormatEntities(false, maxResults, firstResult);
  }

  private List<TournamentFormat> findTournamentFormatEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(TournamentFormat.class));
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

  public TournamentFormat findTournamentFormat(Integer id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(TournamentFormat.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getTournamentFormatCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<TournamentFormat> rt = cq.from(TournamentFormat.class);
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
