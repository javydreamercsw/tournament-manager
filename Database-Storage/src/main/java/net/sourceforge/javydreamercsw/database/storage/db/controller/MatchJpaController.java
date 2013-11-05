/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.javydreamercsw.database.storage.db.controller;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import net.sourceforge.javydreamercsw.database.storage.db.Round;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.database.storage.db.Match;
import net.sourceforge.javydreamercsw.database.storage.db.MatchPK;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MatchJpaController implements Serializable {

    public MatchJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Match match) throws PreexistingEntityException, Exception {
        if (match.getMatchPK() == null) {
            match.setMatchPK(new MatchPK());
        }
        if (match.getTeamList() == null) {
            match.setTeamList(new ArrayList<Team>());
        }
        match.getMatchPK().setRoundId(match.getRound().getRoundPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Round round = match.getRound();
            if (round != null) {
                round = em.getReference(round.getClass(), round.getRoundPK());
                match.setRound(round);
            }
            List<Team> attachedTeamList = new ArrayList<Team>();
            for (Team teamListTeamToAttach : match.getTeamList()) {
                teamListTeamToAttach = em.getReference(teamListTeamToAttach.getClass(), teamListTeamToAttach.getId());
                attachedTeamList.add(teamListTeamToAttach);
            }
            match.setTeamList(attachedTeamList);
            em.persist(match);
            if (round != null) {
                round.getMatchList().add(match);
                round = em.merge(round);
            }
            for (Team teamListTeam : match.getTeamList()) {
                teamListTeam.getMatchList().add(match);
                teamListTeam = em.merge(teamListTeam);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findMatch(match.getMatchPK()) != null) {
                throw new PreexistingEntityException("Match " + match + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Match match) throws NonexistentEntityException, Exception {
        match.getMatchPK().setRoundId(match.getRound().getRoundPK().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Match persistentMatch = em.find(Match.class, match.getMatchPK());
            Round roundOld = persistentMatch.getRound();
            Round roundNew = match.getRound();
            List<Team> teamListOld = persistentMatch.getTeamList();
            List<Team> teamListNew = match.getTeamList();
            if (roundNew != null) {
                roundNew = em.getReference(roundNew.getClass(), roundNew.getRoundPK());
                match.setRound(roundNew);
            }
            List<Team> attachedTeamListNew = new ArrayList<Team>();
            for (Team teamListNewTeamToAttach : teamListNew) {
                teamListNewTeamToAttach = em.getReference(teamListNewTeamToAttach.getClass(), teamListNewTeamToAttach.getId());
                attachedTeamListNew.add(teamListNewTeamToAttach);
            }
            teamListNew = attachedTeamListNew;
            match.setTeamList(teamListNew);
            match = em.merge(match);
            if (roundOld != null && !roundOld.equals(roundNew)) {
                roundOld.getMatchList().remove(match);
                roundOld = em.merge(roundOld);
            }
            if (roundNew != null && !roundNew.equals(roundOld)) {
                roundNew.getMatchList().add(match);
                roundNew = em.merge(roundNew);
            }
            for (Team teamListOldTeam : teamListOld) {
                if (!teamListNew.contains(teamListOldTeam)) {
                    teamListOldTeam.getMatchList().remove(match);
                    teamListOldTeam = em.merge(teamListOldTeam);
                }
            }
            for (Team teamListNewTeam : teamListNew) {
                if (!teamListOld.contains(teamListNewTeam)) {
                    teamListNewTeam.getMatchList().add(match);
                    teamListNewTeam = em.merge(teamListNewTeam);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                MatchPK id = match.getMatchPK();
                if (findMatch(id) == null) {
                    throw new NonexistentEntityException("The match with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(MatchPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Match match;
            try {
                match = em.getReference(Match.class, id);
                match.getMatchPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The match with id " + id + " no longer exists.", enfe);
            }
            Round round = match.getRound();
            if (round != null) {
                round.getMatchList().remove(match);
                round = em.merge(round);
            }
            List<Team> teamList = match.getTeamList();
            for (Team teamListTeam : teamList) {
                teamListTeam.getMatchList().remove(match);
                teamListTeam = em.merge(teamListTeam);
            }
            em.remove(match);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Match> findMatchEntities() {
        return findMatchEntities(true, -1, -1);
    }

    public List<Match> findMatchEntities(int maxResults, int firstResult) {
        return findMatchEntities(false, maxResults, firstResult);
    }

    private List<Match> findMatchEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Match.class));
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

    public Match findMatch(MatchPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Match.class, id);
        } finally {
            em.close();
        }
    }

    public int getMatchCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Match> rt = cq.from(Match.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
