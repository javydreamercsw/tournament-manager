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
import net.sourceforge.javydreamercsw.database.storage.db.Player;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.database.storage.db.Match;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.database.storage.db.TournamentHasTeam;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TeamJpaController implements Serializable {

    public TeamJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Team team) {
        if (team.getPlayerList() == null) {
            team.setPlayerList(new ArrayList<Player>());
        }
        if (team.getMatchList() == null) {
            team.setMatchList(new ArrayList<Match>());
        }
        if (team.getTournamentHasTeamList() == null) {
            team.setTournamentHasTeamList(new ArrayList<TournamentHasTeam>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Player> attachedPlayerList = new ArrayList<Player>();
            for (Player playerListPlayerToAttach : team.getPlayerList()) {
                playerListPlayerToAttach = em.getReference(playerListPlayerToAttach.getClass(), playerListPlayerToAttach.getId());
                attachedPlayerList.add(playerListPlayerToAttach);
            }
            team.setPlayerList(attachedPlayerList);
            List<Match> attachedMatchList = new ArrayList<Match>();
            for (Match matchListMatchToAttach : team.getMatchList()) {
                matchListMatchToAttach = em.getReference(matchListMatchToAttach.getClass(), matchListMatchToAttach.getMatchPK());
                attachedMatchList.add(matchListMatchToAttach);
            }
            team.setMatchList(attachedMatchList);
            List<TournamentHasTeam> attachedTournamentHasTeamList = new ArrayList<TournamentHasTeam>();
            for (TournamentHasTeam tournamentHasTeamListTournamentHasTeamToAttach : team.getTournamentHasTeamList()) {
                tournamentHasTeamListTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListTournamentHasTeamToAttach.getClass(), tournamentHasTeamListTournamentHasTeamToAttach.getTournamentHasTeamPK());
                attachedTournamentHasTeamList.add(tournamentHasTeamListTournamentHasTeamToAttach);
            }
            team.setTournamentHasTeamList(attachedTournamentHasTeamList);
            em.persist(team);
            for (Player playerListPlayer : team.getPlayerList()) {
                playerListPlayer.getTeamList().add(team);
                playerListPlayer = em.merge(playerListPlayer);
            }
            for (Match matchListMatch : team.getMatchList()) {
                matchListMatch.getTeamList().add(team);
                matchListMatch = em.merge(matchListMatch);
            }
            for (TournamentHasTeam tournamentHasTeamListTournamentHasTeam : team.getTournamentHasTeamList()) {
                Team oldTeamOfTournamentHasTeamListTournamentHasTeam = tournamentHasTeamListTournamentHasTeam.getTeam();
                tournamentHasTeamListTournamentHasTeam.setTeam(team);
                tournamentHasTeamListTournamentHasTeam = em.merge(tournamentHasTeamListTournamentHasTeam);
                if (oldTeamOfTournamentHasTeamListTournamentHasTeam != null) {
                    oldTeamOfTournamentHasTeamListTournamentHasTeam.getTournamentHasTeamList().remove(tournamentHasTeamListTournamentHasTeam);
                    oldTeamOfTournamentHasTeamListTournamentHasTeam = em.merge(oldTeamOfTournamentHasTeamListTournamentHasTeam);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Team team) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Team persistentTeam = em.find(Team.class, team.getId());
            List<Player> playerListOld = persistentTeam.getPlayerList();
            List<Player> playerListNew = team.getPlayerList();
            List<Match> matchListOld = persistentTeam.getMatchList();
            List<Match> matchListNew = team.getMatchList();
            List<TournamentHasTeam> tournamentHasTeamListOld = persistentTeam.getTournamentHasTeamList();
            List<TournamentHasTeam> tournamentHasTeamListNew = team.getTournamentHasTeamList();
            List<String> illegalOrphanMessages = null;
            for (TournamentHasTeam tournamentHasTeamListOldTournamentHasTeam : tournamentHasTeamListOld) {
                if (!tournamentHasTeamListNew.contains(tournamentHasTeamListOldTournamentHasTeam)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain TournamentHasTeam " + tournamentHasTeamListOldTournamentHasTeam + " since its team field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Player> attachedPlayerListNew = new ArrayList<Player>();
            for (Player playerListNewPlayerToAttach : playerListNew) {
                playerListNewPlayerToAttach = em.getReference(playerListNewPlayerToAttach.getClass(), playerListNewPlayerToAttach.getId());
                attachedPlayerListNew.add(playerListNewPlayerToAttach);
            }
            playerListNew = attachedPlayerListNew;
            team.setPlayerList(playerListNew);
            List<Match> attachedMatchListNew = new ArrayList<Match>();
            for (Match matchListNewMatchToAttach : matchListNew) {
                matchListNewMatchToAttach = em.getReference(matchListNewMatchToAttach.getClass(), matchListNewMatchToAttach.getMatchPK());
                attachedMatchListNew.add(matchListNewMatchToAttach);
            }
            matchListNew = attachedMatchListNew;
            team.setMatchList(matchListNew);
            List<TournamentHasTeam> attachedTournamentHasTeamListNew = new ArrayList<TournamentHasTeam>();
            for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeamToAttach : tournamentHasTeamListNew) {
                tournamentHasTeamListNewTournamentHasTeamToAttach = em.getReference(tournamentHasTeamListNewTournamentHasTeamToAttach.getClass(), tournamentHasTeamListNewTournamentHasTeamToAttach.getTournamentHasTeamPK());
                attachedTournamentHasTeamListNew.add(tournamentHasTeamListNewTournamentHasTeamToAttach);
            }
            tournamentHasTeamListNew = attachedTournamentHasTeamListNew;
            team.setTournamentHasTeamList(tournamentHasTeamListNew);
            team = em.merge(team);
            for (Player playerListOldPlayer : playerListOld) {
                if (!playerListNew.contains(playerListOldPlayer)) {
                    playerListOldPlayer.getTeamList().remove(team);
                    playerListOldPlayer = em.merge(playerListOldPlayer);
                }
            }
            for (Player playerListNewPlayer : playerListNew) {
                if (!playerListOld.contains(playerListNewPlayer)) {
                    playerListNewPlayer.getTeamList().add(team);
                    playerListNewPlayer = em.merge(playerListNewPlayer);
                }
            }
            for (Match matchListOldMatch : matchListOld) {
                if (!matchListNew.contains(matchListOldMatch)) {
                    matchListOldMatch.getTeamList().remove(team);
                    matchListOldMatch = em.merge(matchListOldMatch);
                }
            }
            for (Match matchListNewMatch : matchListNew) {
                if (!matchListOld.contains(matchListNewMatch)) {
                    matchListNewMatch.getTeamList().add(team);
                    matchListNewMatch = em.merge(matchListNewMatch);
                }
            }
            for (TournamentHasTeam tournamentHasTeamListNewTournamentHasTeam : tournamentHasTeamListNew) {
                if (!tournamentHasTeamListOld.contains(tournamentHasTeamListNewTournamentHasTeam)) {
                    Team oldTeamOfTournamentHasTeamListNewTournamentHasTeam = tournamentHasTeamListNewTournamentHasTeam.getTeam();
                    tournamentHasTeamListNewTournamentHasTeam.setTeam(team);
                    tournamentHasTeamListNewTournamentHasTeam = em.merge(tournamentHasTeamListNewTournamentHasTeam);
                    if (oldTeamOfTournamentHasTeamListNewTournamentHasTeam != null && !oldTeamOfTournamentHasTeamListNewTournamentHasTeam.equals(team)) {
                        oldTeamOfTournamentHasTeamListNewTournamentHasTeam.getTournamentHasTeamList().remove(tournamentHasTeamListNewTournamentHasTeam);
                        oldTeamOfTournamentHasTeamListNewTournamentHasTeam = em.merge(oldTeamOfTournamentHasTeamListNewTournamentHasTeam);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = team.getId();
                if (findTeam(id) == null) {
                    throw new NonexistentEntityException("The team with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Team team;
            try {
                team = em.getReference(Team.class, id);
                team.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The team with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<TournamentHasTeam> tournamentHasTeamListOrphanCheck = team.getTournamentHasTeamList();
            for (TournamentHasTeam tournamentHasTeamListOrphanCheckTournamentHasTeam : tournamentHasTeamListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Team (" + team + ") cannot be destroyed since the TournamentHasTeam " + tournamentHasTeamListOrphanCheckTournamentHasTeam + " in its tournamentHasTeamList field has a non-nullable team field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Player> playerList = team.getPlayerList();
            for (Player playerListPlayer : playerList) {
                playerListPlayer.getTeamList().remove(team);
                playerListPlayer = em.merge(playerListPlayer);
            }
            List<Match> matchList = team.getMatchList();
            for (Match matchListMatch : matchList) {
                matchListMatch.getTeamList().remove(team);
                matchListMatch = em.merge(matchListMatch);
            }
            em.remove(team);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Team> findTeamEntities() {
        return findTeamEntities(true, -1, -1);
    }

    public List<Team> findTeamEntities(int maxResults, int firstResult) {
        return findTeamEntities(false, maxResults, firstResult);
    }

    private List<Team> findTeamEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Team.class));
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

    public Team findTeam(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Team.class, id);
        } finally {
            em.close();
        }
    }

    public int getTeamCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Team> rt = cq.from(Team.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
