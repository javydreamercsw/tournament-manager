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
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import net.sourceforge.javydreamercsw.database.storage.db.Player;
import net.sourceforge.javydreamercsw.database.storage.db.Record;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerJpaController implements Serializable {

    public PlayerJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Player player) {
        if (player.getTeamList() == null) {
            player.setTeamList(new ArrayList<Team>());
        }
        if (player.getRecordList() == null) {
            player.setRecordList(new ArrayList<Record>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Team> attachedTeamList = new ArrayList<>();
            for (Team teamListTeamToAttach : player.getTeamList()) {
                teamListTeamToAttach = em.getReference(teamListTeamToAttach.getClass(), teamListTeamToAttach.getId());
                attachedTeamList.add(teamListTeamToAttach);
            }
            player.setTeamList(attachedTeamList);
            List<Record> attachedRecordList = new ArrayList<>();
            for (Record recordListRecordToAttach : player.getRecordList()) {
                recordListRecordToAttach = em.getReference(recordListRecordToAttach.getClass(), recordListRecordToAttach.getId());
                attachedRecordList.add(recordListRecordToAttach);
            }
            player.setRecordList(attachedRecordList);
            em.persist(player);
            for (Team teamListTeam : player.getTeamList()) {
                teamListTeam.getPlayerList().add(player);
                teamListTeam = em.merge(teamListTeam);
            }
            for (Record recordListRecord : player.getRecordList()) {
                recordListRecord.getPlayerList().add(player);
                recordListRecord = em.merge(recordListRecord);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Player player) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Player persistentPlayer = em.find(Player.class, player.getId());
            List<Team> teamListOld = persistentPlayer.getTeamList();
            List<Team> teamListNew = player.getTeamList();
            List<Record> recordListOld = persistentPlayer.getRecordList();
            List<Record> recordListNew = player.getRecordList();
            List<Team> attachedTeamListNew = new ArrayList<>();
            for (Team teamListNewTeamToAttach : teamListNew) {
                teamListNewTeamToAttach = em.getReference(teamListNewTeamToAttach.getClass(), teamListNewTeamToAttach.getId());
                attachedTeamListNew.add(teamListNewTeamToAttach);
            }
            teamListNew = attachedTeamListNew;
            player.setTeamList(teamListNew);
            List<Record> attachedRecordListNew = new ArrayList<>();
            for (Record recordListNewRecordToAttach : recordListNew) {
                recordListNewRecordToAttach = em.getReference(recordListNewRecordToAttach.getClass(), recordListNewRecordToAttach.getId());
                attachedRecordListNew.add(recordListNewRecordToAttach);
            }
            recordListNew = attachedRecordListNew;
            player.setRecordList(recordListNew);
            player = em.merge(player);
            for (Team teamListOldTeam : teamListOld) {
                if (!teamListNew.contains(teamListOldTeam)) {
                    teamListOldTeam.getPlayerList().remove(player);
                    teamListOldTeam = em.merge(teamListOldTeam);
                }
            }
            for (Team teamListNewTeam : teamListNew) {
                if (!teamListOld.contains(teamListNewTeam)) {
                    teamListNewTeam.getPlayerList().add(player);
                    teamListNewTeam = em.merge(teamListNewTeam);
                }
            }
            for (Record recordListOldRecord : recordListOld) {
                if (!recordListNew.contains(recordListOldRecord)) {
                    recordListOldRecord.getPlayerList().remove(player);
                    recordListOldRecord = em.merge(recordListOldRecord);
                }
            }
            for (Record recordListNewRecord : recordListNew) {
                if (!recordListOld.contains(recordListNewRecord)) {
                    recordListNewRecord.getPlayerList().add(player);
                    recordListNewRecord = em.merge(recordListNewRecord);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = player.getId();
                if (findPlayer(id) == null) {
                    throw new NonexistentEntityException("The player with id "
                            + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Player player;
            try {
                player = em.getReference(Player.class, id);
                player.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The player with id "
                        + id + " no longer exists.", enfe);
            }
            List<Team> teamList = player.getTeamList();
            for (Team teamListTeam : teamList) {
                teamListTeam.getPlayerList().remove(player);
                teamListTeam = em.merge(teamListTeam);
            }
            List<Record> recordList = player.getRecordList();
            for (Record recordListRecord : recordList) {
                recordListRecord.getPlayerList().remove(player);
                recordListRecord = em.merge(recordListRecord);
            }
            em.remove(player);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Player> findPlayerEntities() {
        return findPlayerEntities(true, -1, -1);
    }

    public List<Player> findPlayerEntities(int maxResults, int firstResult) {
        return findPlayerEntities(false, maxResults, firstResult);
    }

    private List<Player> findPlayerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Player.class));
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

    public Player findPlayer(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Player.class, id);
        } finally {
            em.close();
        }
    }

    public int getPlayerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Player> rt = cq.from(Player.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}