package br.com.devd2.utilitario.dto;

public record ResultProcessDto(long totalFiles,
                               long totalBytesFiles,
                               long totalFilesDuplicates,
                               long totalBytesFilesDuplicates,
                               long totalFilesRemoved,
                               long totalBytesFilesRemoved) {
}