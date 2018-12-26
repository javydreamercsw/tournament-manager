package com.github.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecordPK;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;
import com.github.javydreamercsw.database.storage.db.server.AbstractController;

public class TeamHasFormatRecordJpaController extends AbstractController implements Serializable
{
  private static final long serialVersionUID = 2465839189233391174L;
  public TeamHasFormatRecordJpaController(EntityManagerFactory emf)
  {
    super(emf);
  }

  public void create(TeamHasFormatRecord teamHasFormatRecord) throws PreexistingEntityException, Exception
  {
    if (teamHasFormatRecord.getTeamHasFormatRecordPK() == null)
    {
      teamHasFormatRecord.setTeamHasFormatRecordPK(new TeamHasFormatRecordPK());
    }
    teamHasFormatRecord.getTeamHasFormatRecordPK().setFormatGameId(teamHasFormatRecord.getFormat().getFormatPK().getGameId());
    teamHasFormatRecord.getTeamHasFormatRecordPK().setFormatId(teamHasFormatRecord.getFormat().getFormatPK().getId());
    teamHasFormatRecord.getTeamHasFormatRecordPK().setTeamId(teamHasFormatRecord.getTeam().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      Format format = teamHasFormatRecord.getFormat();
      if (format != null)
      {
        format = em.getReference(format.getClass(), format.getFormatPK());
        teamHasFormatRecord.setFormat(format);
      }
      Team team = teamHasFormatRecord.getTeam();
      if (team != null)
      {
        team = em.getReference(team.getClass(), team.getId());
        teamHasFormatRecord.setTeam(team);
      }
      em.persist(teamHasFormatRecord);
      if (format != null)
      {
        format.getTeamHasFormatRecordList().add(teamHasFormatRecord);
        format = em.merge(format);
      }
      if (team != null)
      {
        team.getTeamHasFormatRecordList().add(teamHasFormatRecord);
        team = em.merge(team);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      if (findTeamHasFormatRecord(teamHasFormatRecord.getTeamHasFormatRecordPK()) != null)
      {
        throw new PreexistingEntityException("TeamHasFormatRecord " + teamHasFormatRecord + " already exists.", ex);
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

  public void edit(TeamHasFormatRecord teamHasFormatRecord) throws NonexistentEntityException, Exception
  {
    teamHasFormatRecord.getTeamHasFormatRecordPK().setFormatGameId(teamHasFormatRecord.getFormat().getFormatPK().getGameId());
    teamHasFormatRecord.getTeamHasFormatRecordPK().setFormatId(teamHasFormatRecord.getFormat().getFormatPK().getId());
    teamHasFormatRecord.getTeamHasFormatRecordPK().setTeamId(teamHasFormatRecord.getTeam().getId());
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      TeamHasFormatRecord persistentTeamHasFormatRecord = em.find(TeamHasFormatRecord.class, teamHasFormatRecord.getTeamHasFormatRecordPK());
      Format formatOld = persistentTeamHasFormatRecord.getFormat();
      Format formatNew = teamHasFormatRecord.getFormat();
      Team teamOld = persistentTeamHasFormatRecord.getTeam();
      Team teamNew = teamHasFormatRecord.getTeam();
      if (formatNew != null)
      {
        formatNew = em.getReference(formatNew.getClass(), formatNew.getFormatPK());
        teamHasFormatRecord.setFormat(formatNew);
      }
      if (teamNew != null)
      {
        teamNew = em.getReference(teamNew.getClass(), teamNew.getId());
        teamHasFormatRecord.setTeam(teamNew);
      }
      teamHasFormatRecord = em.merge(teamHasFormatRecord);
      if (formatOld != null && !formatOld.equals(formatNew))
      {
        formatOld.getTeamHasFormatRecordList().remove(teamHasFormatRecord);
        formatOld = em.merge(formatOld);
      }
      if (formatNew != null && !formatNew.equals(formatOld))
      {
        formatNew.getTeamHasFormatRecordList().add(teamHasFormatRecord);
        formatNew = em.merge(formatNew);
      }
      if (teamOld != null && !teamOld.equals(teamNew))
      {
        teamOld.getTeamHasFormatRecordList().remove(teamHasFormatRecord);
        teamOld = em.merge(teamOld);
      }
      if (teamNew != null && !teamNew.equals(teamOld))
      {
        teamNew.getTeamHasFormatRecordList().add(teamHasFormatRecord);
        teamNew = em.merge(teamNew);
      }
      em.getTransaction().commit();
    }
    catch (Exception ex)
    {
      String msg = ex.getLocalizedMessage();
      if (msg == null || msg.length() == 0)
      {
        TeamHasFormatRecordPK id = teamHasFormatRecord.getTeamHasFormatRecordPK();
        if (findTeamHasFormatRecord(id) == null)
        {
          throw new NonexistentEntityException("The teamHasFormatRecord with id " + id + " no longer exists.");
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

  public void destroy(TeamHasFormatRecordPK id) throws NonexistentEntityException
  {
    EntityManager em = null;
    try
    {
      em = getEntityManager();
      em.getTransaction().begin();
      TeamHasFormatRecord teamHasFormatRecord;
      try
      {
        teamHasFormatRecord = em.getReference(TeamHasFormatRecord.class, id);
        teamHasFormatRecord.getTeamHasFormatRecordPK();
      }
      catch (EntityNotFoundException enfe)
      {
        throw new NonexistentEntityException("The teamHasFormatRecord with id " + id + " no longer exists.", enfe);
      }
      Format format = teamHasFormatRecord.getFormat();
      if (format != null)
      {
        format.getTeamHasFormatRecordList().remove(teamHasFormatRecord);
        format = em.merge(format);
      }
      Team team = teamHasFormatRecord.getTeam();
      if (team != null)
      {
        team.getTeamHasFormatRecordList().remove(teamHasFormatRecord);
        team = em.merge(team);
      }
      em.remove(teamHasFormatRecord);
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

  public List<TeamHasFormatRecord> findTeamHasFormatRecordEntities()
  {
    return findTeamHasFormatRecordEntities(true, -1, -1);
  }

  public List<TeamHasFormatRecord> findTeamHasFormatRecordEntities(int maxResults, int firstResult)
  {
    return findTeamHasFormatRecordEntities(false, maxResults, firstResult);
  }

  private List<TeamHasFormatRecord> findTeamHasFormatRecordEntities(boolean all, int maxResults, int firstResult)
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      cq.select(cq.from(TeamHasFormatRecord.class));
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

  public TeamHasFormatRecord findTeamHasFormatRecord(TeamHasFormatRecordPK id)
  {
    EntityManager em = getEntityManager();
    try
    {
      return em.find(TeamHasFormatRecord.class, id);
    }
    finally
    {
      em.close();
    }
  }

  public int getTeamHasFormatRecordCount()
  {
    EntityManager em = getEntityManager();
    try
    {
      CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
      Root<TeamHasFormatRecord> rt = cq.from(TeamHasFormatRecord.class);
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
