package br.com.devd2.utilitario.dto;

import java.time.Instant;

public record FileIndexDto(String filePath,
                           String fileName,
                           long fileSize,
                           String fileHash,
                           Instant lastModified) {
}