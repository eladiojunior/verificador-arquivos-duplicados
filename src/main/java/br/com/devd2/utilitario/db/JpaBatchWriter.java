package br.com.devd2.utilitario.db;

import br.com.devd2.utilitario.db.model.FileIndexEntity;
import br.com.devd2.utilitario.dto.FileIndexDto;
import br.com.devd2.utilitario.service.ParallelScannerService;
import jakarta.persistence.EntityManager;

public class JpaBatchWriter implements ParallelScannerService.Writer {

    private final EntityManager em;
    private final int batchSize;
    private int count = 0;

    public JpaBatchWriter(int batchSize) {
        this.em = JpaUtil.getEMF().createEntityManager();
        this.batchSize = batchSize;
        em.getTransaction().begin();
    }

    @Override
    public void write(FileIndexDto r) {
        FileIndexEntity e = new FileIndexEntity();
        e.setFilePath(r.filePath());
        e.setFileName(r.fileName());
        e.setFileSize(r.fileSize());
        e.setFileHash(r.fileHash());
        e.setLastModified(r.lastModified());
        em.persist(e);
        count++;
        if (count % batchSize == 0) {
            em.flush();
            em.clear();
        }
    }

    @Override
    public void flush() {
        em.flush();
        em.clear();
        em.getTransaction().commit();
    }

    @Override
    public void close() {
        if (em.isOpen()) em.close();
    }

}