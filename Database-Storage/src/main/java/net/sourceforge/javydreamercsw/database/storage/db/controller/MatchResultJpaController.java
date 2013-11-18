/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.sourceforge.javydreamercsw.database.storage.db.MatchResult;
import net.sourceforge.javydreamercsw.database.storage.db.MatchResultPK;
import net.sourceforge.javydreamercsw.database.storage.db.MatchResultType;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MatchResultJpaController implements Serializable {

    public MatchResultJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(MatchResult matchResult) throws PreexistingEntityException, Exception {
        if (matchResult.getMatchResultPK() == null) {
            matchResult.setMatchResultPK(new MatchResultPK());
        }
        matchResult.getMatchResultPK().setMatchResultTypeId(matchResult.getMatchResultType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MatchResultType matchResultType = matchResult.getMatchResultType();
            if (matchResultType != null) {
                matchResultType = em.getReference(matchResultType.getClass(), matchResultType.getId());
                matchResult.setMatchResultType(matchResultType);
            }
            em.persist(matchResult);
            if (matchResultType != null) {
                matchResultType.getMatchResultList().add(matchResult);
                matchResultType = em.merge(matchResultType);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMatchResult(matchResult.getMatchResultPK()) != null) {
                throw new PreexistingEntityException("MatchResult " + matchResult + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(MatchResult matchResult) throws NonexistentEntityException, Exception {
        matchResult.getMatchResultPK().setMatchResultTypeId(matchResult.getMatchResultType().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MatchResult persistentMatchResult = em.find(MatchResult.class, matchResult.getMatchResultPK());
            MatchResultType matchResultTypeOld = persistentMatchResult.getMatchResultType();
            MatchResultType matchResultTypeNew = matchResult.getMatchResultType();
            if (matchResultTypeNew != null) {
                matchResultTypeNew = em.getReference(matchResultTypeNew.getClass(), matchResultTypeNew.getId());
                matchResult.setMatchResultType(matchResultTypeNew);
            }
            matchResult = em.merge(matchResult);
            if (matchResultTypeOld != null && !matchResultTypeOld.equals(matchResultTypeNew)) {
                matchResultTypeOld.getMatchResultList().remove(matchResult);
                matchResultTypeOld = em.merge(matchResultTypeOld);
            }
            if (matchResultTypeNew != null && !matchResultTypeNew.equals(matchResultTypeOld)) {
                matchResultTypeNew.getMatchResultList().add(matchResult);
                matchResultTypeNew = em.merge(matchResultTypeNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                MatchResultPK id = matchResult.getMatchResultPK();
                if (findMatchResult(id) == null) {
                    throw new NonexistentEntityException("The matchResult with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(MatchResultPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            MatchResult matchResult;
            try {
                matchResult = em.getReference(MatchResult.class, id);
                matchResult.getMatchResultPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The matchResult with id " + id + " no longer exists.", enfe);
            }
            MatchResultType matchResultType = matchResult.getMatchResultType();
            if (matchResultType != null) {
                matchResultType.getMatchResultList().remove(matchResult);
                matchResultType = em.merge(matchResultType);
            }
            em.remove(matchResult);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<MatchResult> findMatchResultEntities() {
        return findMatchResultEntities(true, -1, -1);
    }

    public List<MatchResult> findMatchResultEntities(int maxResults, int firstResult) {
        return findMatchResultEntities(false, maxResults, firstResult);
    }

    private List<MatchResult> findMatchResultEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(MatchResult.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public MatchResult findMatchResult(MatchResultPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(MatchResult.class, id);
        } finally {
            em.close();
        }
    }

    public int getMatchResultCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<MatchResult> rt = cq.from(MatchResult.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
