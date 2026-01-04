package br.com.devd2.utilitario.db.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "FILE_INDEX",
        indexes = {
                @Index(name = "IDX_FILE_HASH", columnList = "file_hash"),
                @Index(name = "IDX_FILE_PATH", columnList = "file_path")
        })
@Data
public class FileIndexEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_path", length = 1024, nullable = false)
    private String filePath;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "file_hash", length = 64, nullable = false)
    private String fileHash;

    @Column(name = "last_modified", nullable = false)
    private Instant lastModified;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

}