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
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.Match;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.database.storage.db.Round;
import net.sourceforge.javydreamercsw.database.storage.db.RoundPK;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.PreexistingEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RoundJpaController implements Serializable {

    public RoundJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Round round) throws PreexistingEntityException, Exception {
        if (round.getRoundPK() == null) {
            round.setRoundPK(new RoundPK());
        }
        if (round.getMatchList() == null) {
            round.setMatchList(new ArrayList<Match>());
        }
        round.getRoundPK().setTournamentId(round.getTournament().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Tournament tournament = round.getTournament();
            if (tournament != null) {
                tournament = em.getReference(tournament.getClass(), tournament.getId());
                round.setTournament(tournament);
            }
            List<Match> attachedMatchList = new ArrayList<Match>();
            for (Match matchListMatchToAttach : round.getMatchList()) {
                matchListMatchToAttach = em.getReference(matchListMatchToAttach.getClass(), matchListMatchToAttach.getMatchPK());
                attachedMatchList.add(matchListMatchToAttach);
            }
            round.setMatchList(attachedMatchList);
            em.persist(round);
            if (tournament != null) {
                tournament.getRoundList().add(round);
                tournament = em.merge(tournament);
            }
            for (Match matchListMatch : round.getMatchList()) {
                Round oldRoundOfMatchListMatch = matchListMatch.getRound();
                matchListMatch.setRound(round);
                matchListMatch = em.merge(matchListMatch);
                if (oldRoundOfMatchListMatch != null) {
                    oldRoundOfMatchListMatch.getMatchList().remove(matchListMatch);
                    oldRoundOfMatchListMatch = em.merge(oldRoundOfMatchListMatch);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRound(round.getRoundPK()) != null) {
                throw new PreexistingEntityException("Round " + round + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Round round) throws IllegalOrphanException, NonexistentEntityException, Exception {
        round.getRoundPK().setTournamentId(round.getTournament().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Round persistentRound = em.find(Round.class, round.getRoundPK());
            Tournament tournamentOld = persistentRound.getTournament();
            Tournament tournamentNew = round.getTournament();
            List<Match> matchListOld = persistentRound.getMatchList();
            List<Match> matchListNew = round.getMatchList();
            List<String> illegalOrphanMessages = null;
            for (Match matchListOldMatch : matchListOld) {
                if (!matchListNew.contains(matchListOldMatch)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Match " + matchListOldMatch + " since its round field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (tournamentNew != null) {
                tournamentNew = em.getReference(tournamentNew.getClass(), tournamentNew.getId());
                round.setTournament(tournamentNew);
            }
            List<Match> attachedMatchListNew = new ArrayList<Match>();
            for (Match matchListNewMatchToAttach : matchListNew) {
                matchListNewMatchToAttach = em.getReference(matchListNewMatchToAttach.getClass(), matchListNewMatchToAttach.getMatchPK());
                attachedMatchListNew.add(matchListNewMatchToAttach);
            }
            matchListNew = attachedMatchListNew;
            round.setMatchList(matchListNew);
            round = em.merge(round);
            if (tournamentOld != null && !tournamentOld.equals(tournamentNew)) {
                tournamentOld.getRoundList().remove(round);
                tournamentOld = em.merge(tournamentOld);
            }
            if (tournamentNew != null && !tournamentNew.equals(tournamentOld)) {
                tournamentNew.getRoundList().add(round);
                tournamentNew = em.merge(tournamentNew);
            }
            for (Match matchListNewMatch : matchListNew) {
                if (!matchListOld.contains(matchListNewMatch)) {
                    Round oldRoundOfMatchListNewMatch = matchListNewMatch.getRound();
                    matchListNewMatch.setRound(round);
                    matchListNewMatch = em.merge(matchListNewMatch);
                    if (oldRoundOfMatchListNewMatch != null && !oldRoundOfMatchListNewMatch.equals(round)) {
                        oldRoundOfMatchListNewMatch.getMatchList().remove(matchListNewMatch);
                        oldRoundOfMatchListNewMatch = em.merge(oldRoundOfMatchListNewMatch);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RoundPK id = round.getRoundPK();
                if (findRound(id) == null) {
                    throw new NonexistentEntityException("The round with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RoundPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Round round;
            try {
                round = em.getReference(Round.class, id);
                round.getRoundPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The round with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Match> matchListOrphanCheck = round.getMatchList();
            for (Match matchListOrphanCheckMatch : matchListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Round (" + round + ") cannot be destroyed since the Match " + matchListOrphanCheckMatch + " in its matchList field has a non-nullable round field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Tournament tournament = round.getTournament();
            if (tournament != null) {
                tournament.getRoundList().remove(round);
                tournament = em.merge(tournament);
            }
            em.remove(round);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Round> findRoundEntities() {
        return findRoundEntities(true, -1, -1);
    }

    public List<Round> findRoundEntities(int maxResults, int firstResult) {
        return findRoundEntities(false, maxResults, firstResult);
    }

    private List<Round> findRoundEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Round.class));
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

    public Round findRound(RoundPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Round.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoundCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Round> rt = cq.from(Round.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
