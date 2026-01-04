package br.com.devd2.utilitario.service;

import br.com.devd2.utilitario.helper.HelperUtils;
import br.com.devd2.utilitario.helper.LogPrinter;
import br.com.devd2.utilitario.db.FileIndexRepository;
import br.com.devd2.utilitario.db.model.FileIndexEntity;
import br.com.devd2.utilitario.dto.ResultCheckDuplicatesDto;
import br.com.devd2.utilitario.dto.ResultScannerDuplicateDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ScannerDuplicateService {

    /**
     * Responsável por varrer o local informado e as subpastas se necessário para identificar os arquivos,
     * registrando esses arquivos em banco de dados local para facilitar a verificação de duplicidade.
     * @param basePath - Local da pasta informada para varredura.
     * @param scanSubpastas - Flag que indica se é para entrar nas subpastas.
     * @return Resultado da varredura dos arquivos.
     */
    public ResultScannerDuplicateDto scanFiles(Path basePath, boolean scanSubpastas) {

        long total_arquivos = 0L;
        long total_bytes_arquivos = 0L;

        try {

            var repository = new FileIndexRepository();

            var strBasePath = basePath.toAbsolutePath().toString();
            LogPrinter.log("> Varrendo em: " + strBasePath);

            var paths = HelperUtils.listarArquivos(basePath, scanSubpastas);
            for (var path : paths) {
                if (!strBasePath.equals(path.getParent().toString())) {
                    strBasePath = path.getParent().toString();
                    LogPrinter.log("> Varrendo em: " + strBasePath);
                }
                total_arquivos++;
                var file_existe = repository.obterPorPath(path.toFile().getAbsolutePath());
                if (file_existe == null) {
                    total_bytes_arquivos+= Files.size(path);
                    var file = path.toFile();
                    var fileIndex = new FileIndexEntity();
                    fileIndex.setFileName(file.getName());
                    fileIndex.setFilePath(file.getAbsolutePath());
                    fileIndex.setFileSize(file.length());
                    fileIndex.setLastModified(Instant.ofEpochMilli(file.lastModified()));
                    fileIndex.setFileHash(HelperUtils.gerarHashSHA256(file.toPath()));
                    repository.salvar(fileIndex);
                    LogPrinter.log(">> Arquivo registrado: " + path.toFile().getAbsolutePath());
                } else {
                    total_bytes_arquivos+=file_existe.getFileSize();
                }
            }
            LogPrinter.log("+------------------------------------------------------------------------+");

            return new ResultScannerDuplicateDto(total_arquivos, total_bytes_arquivos);

        } catch (Exception error) {
            LogPrinter.log(" >> [ERRO] Falha ao verificar arquivos no local: " + basePath.toFile().getAbsolutePath() + " -> " + error.getMessage(), true);
            return null;
        }
    }

    /**
     * Responsável por recuperar a lista de arquivos do banco local escaneados e verificar os duplicados.
     * @return Lista de arquivos duplicados.
     */
    public ResultCheckDuplicatesDto checkFilesDuplicates() {

        var listaDuplicados = new ArrayList<FileIndexEntity>();
        long total_duplicados = 0L;
        long total_bytes_desnecessarios = 0L;

        try {

            var repository = new FileIndexRepository();
            List<FileIndexEntity> listaDuplicadosEntites = repository.listarDuplicados();

            String ultimoHashFile = null;

            LogPrinter.log("> Verificando duplicidade e agrupando arquivos para remoção ");

            for (var item : listaDuplicadosEntites) {
                if (item.getFileHash().equals(ultimoHashFile)) {
                    total_duplicados++;
                    total_bytes_desnecessarios+=item.getFileSize();
                    listaDuplicados.add(item);
                    LogPrinter.log(">>> Arquivo duplicado: " + item.getFilePath() +
                            " -> Última modificação: " + HelperUtils.formatDateTime(item.getLastModified()));
                    continue;
                }
                LogPrinter.log(">> Arquivo mais recente: " + item.getFilePath() +
                        " -> Última modificação: " + HelperUtils.formatDateTime(item.getLastModified()));
                ultimoHashFile = item.getFileHash();
            }

            return new ResultCheckDuplicatesDto(listaDuplicados, total_duplicados, total_bytes_desnecessarios);

        } catch (Exception error) {
            LogPrinter.log(" >> [ERRO] Falha ao verificar arquivos duplicados -> " + error.getMessage(), true);
            return null;
        }

    }
}