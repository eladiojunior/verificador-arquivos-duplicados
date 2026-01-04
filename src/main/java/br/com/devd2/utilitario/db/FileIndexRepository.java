package br.com.devd2.utilitario.db;

import br.com.devd2.utilitario.db.model.FileIndexEntity;
import jakarta.persistence.EntityManager;

import java.util.List;

public class FileIndexRepository {

    public void salvar(FileIndexEntity entity) {
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        }
    }

    public FileIndexEntity obterPorPath(String path) {
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            return em.createQuery(
                            "SELECT f FROM FileIndexEntity f WHERE f.filePath = :path",
                            FileIndexEntity.class)
                    .setParameter("path", path)
                    .getResultStream().findFirst().orElse(null);
        }
    }

    public List<FileIndexEntity> listarDuplicados() {
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            var query = em.createQuery(
                    "SELECT f" +
                       " FROM FileIndexEntity f" +
                       " WHERE f.fileHash IN (" +
                       "   SELECT f2.fileHash" +
                       "   FROM FileIndexEntity f2" +
                       "   GROUP BY f2.fileHash" +
                       "   HAVING COUNT(f2) > 1" +
                       "  )" +
                       " ORDER BY f.fileHash, f.lastModified DESC ",
                    FileIndexEntity.class);
            return query.getResultList();
        }
    }

    public void remover(Long idEntity) {
        try (EntityManager em = JPAUtil.getEMF().createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery(" DELETE FROM FileIndexEntity f WHERE f.id = :id")
                    .setParameter("id", idEntity)
                    .executeUpdate();
            em.getTransaction().commit();
        }
    }

}