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

import com.github.javydreamercsw.database.storage.db.MatchResult;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class MatchResultTypeJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = -8326151340449588898L;

  public MatchResultTypeJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(MatchResultType matchResultType)
  {
    if (matchResultType.getMatchResultList() == null)
    {
      matchResultType.setMatchResultList(new ArrayList<>());
    }
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      List<MatchResult> attachedMatchResultList = new ArrayList<>();
      for (MatchResult matchResultListMatchResultToAttach : matchResultType.getMatchResultList())
      {
        matchResultListMatchResultToAttach = em.getReference(matchResultListMatchResultToAttach.getClass(), matchResultListMatchResultToAttach.getMatchResultPK());
        attachedMatchResultList.add(matchResultListMatchResultToAttach);
      }
      matchResultType.setMatchResultList(attachedMatchResultList);
      em.persist(matchResultType);
      for (MatchResult matchResultListMatchResult : matchResultType.getMatchResultList())
      {
        MatchResultType oldMatchResultTypeOfMatchResultListMatchResult = matchResultListMatchResult.getMatchResultType();
        matchResultListMatchResult.setMatchResultType(matchResultType);
        matchResultListMatchResult = em.merge(matchResultListMatchResult);
        if (oldMatchResultTypeOfMatchResultListMatchResult != null)
        {
          oldMatchResultTypeOfMatchResultListMatchResult.getMatchResultList().remove(matchResultListMatchResult);
          oldMatchResultTypeOfMatchResultListMatchResult = em.merge(oldMatchResultTypeOfMatchResultListMatchResult);
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

  public void edit(MatchResultType matchResultType) throws IllegalOrphanException, NonexistentEntityException, Exception
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      MatchResultType persistentMatchResultType = em.find(MatchResultType.class, matchResultType.getId());
      List<MatchResult> matchResultListOld = persistentMatchResultType.getMatchResultList();
      List<MatchResult> matchResultListNew = matchResultType.getMatchResultList();
      List<String> illegalOrphanMessages = null;
      for (MatchResult matchResultListOldMatchResult : matchResultListOld)
      {
        if (!matchResultListNew.contains(matchResultListOldMatchResult))
        {
          if (illegalOrphanMessages == null)
          {
            illegalOrphanMessages = new ArrayList<>();
          }
          illegalOrphanMessages.add("You must retain MatchResult " + matchResultListOldMatchResult + " since its matchResultType field is not nullable.");
        }
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      List<MatchResult> attachedMatchResultListNew = new ArrayList<>();
      for (MatchResult matchResultListNewMatchResultToAttach : matchResultListNew)
      {
        matchResultListNewMatchResultToAttach = em.getReference(matchResultListNewMatchResultToAttach.getClass(), matchResultListNewMatchResultToAttach.getMatchResultPK());
        attachedMatchResultListNew.add(matchResultListNewMatchResultToAttach);
      }
      matchResultListNew = attachedMatchResultListNew;
      matchResultType.setMatchResultList(matchResultListNew);
      matchResultType = em.merge(matchResultType);
      for (MatchResult matchResultListNewMatchResult : matchResultListNew)
      {
        if (!matchResultListOld.contains(matchResultListNewMatchResult))
        {
          MatchResultType oldMatchResultTypeOfMatchResultListNewMatchResult = matchResultListNewMatchResult.getMatchResultType();
          matchResultListNewMatchResult.setMatchResultType(matchResultType);
          matchResultListNewMatchResult = em.merge(matchResultListNewMatchResult);
          if (oldMatchResultTypeOfMatchResultListNewMatchResult != null && !oldMatchResultTypeOfMatchResultListNewMatchResult.equals(matchResultType))
          {
            oldMatchResultTypeOfMatchResultListNewMatchResult.getMatchResultList().remove(matchResultListNewMatchResult);
            oldMatchResultTypeOfMatchResultListNewMatchResult = em.merge(oldMatchResultTypeOfMatchResultListNewMatchResult);
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
        Integer id = matchResultType.getId();
        if (findMatchResultType(id) == null)
        {
          throw new NonexistentEntityException("The matchResultType with id " + id + " no longer exists.");
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
      MatchResultType matchResultType;
      try
      {
        matchResultType = em.getReference(MatchResultType.class, id);
        matchResultType.getId();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The matchResultType with id " + id + " no longer exists.", enfe);
      }
      List<String> illegalOrphanMessages = null;
      List<MatchResult> matchResultListOrphanCheck = matchResultType.getMatchResultList();
      for (MatchResult matchResultListOrphanCheckMatchResult : matchResultListOrphanCheck)
      {
        if (illegalOrphanMessages == null)
        {
          illegalOrphanMessages = new ArrayList<>();
        }
        illegalOrphanMessages.add("This MatchResultType (" + matchResultType + ") cannot be destroyed since the MatchResult " + matchResultListOrphanCheckMatchResult + " in its matchResultList field has a non-nullable matchResultType field.");
      }
      if (illegalOrphanMessages != null)
      {
        throw new IllegalOrphanException(illegalOrphanMessages);
      }
      em.remove(matchResultType);
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

  public List<MatchResultType> findMatchResultTypeEntities()
  {
    return findMatchResultTypeEntities(true, -1, -1);
  }

  public List<MatchResultType> findMatchResultTypeEntities(int maxResults, int firstResult)
  {
    return findMatchResultTypeEntities(false, maxResults, firstResult);
  }

  private List<MatchResultType> findMatchResultTypeEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(MatchResultType.class));
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

  public MatchResultType findMatchResultType(Integer id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(MatchResultType.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getMatchResultTypeCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<MatchResultType> rt = cq.from(MatchResultType.class);
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
