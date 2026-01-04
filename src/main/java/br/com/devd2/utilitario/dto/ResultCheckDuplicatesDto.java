package br.com.devd2.utilitario.dto;

import br.com.devd2.utilitario.db.model.FileIndexEntity;

import java.util.List;

public record ResultCheckDuplicatesDto(List<FileIndexEntity> listDuplicates,
                                       long totalFilesDuplicates,
                                       long totalBytesFilesDuplicates) {
}