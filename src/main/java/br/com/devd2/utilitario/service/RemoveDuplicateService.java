package br.com.devd2.utilitario.service;

import br.com.devd2.utilitario.helper.ConsoleConfirm;
import br.com.devd2.utilitario.helper.LogPrinter;
import br.com.devd2.utilitario.db.FileIndexRepository;
import br.com.devd2.utilitario.db.model.FileIndexEntity;
import br.com.devd2.utilitario.dto.ResultRemoveDuplicateDto;

import java.io.File;
import java.util.List;

public class RemoveDuplicateService {

    /**
     * Remove duplicados com confirmação.
     *
     * @param duplicados Lista de arquivos que serão removidos (ex.: "copias").
     * @param confirmarCadaArquivo Se true, pergunta para cada arquivo.
     * @return quantidade removida com sucesso
     */
    public ResultRemoveDuplicateDto removerComConfirmacao(List<FileIndexEntity> duplicados,
                                                          boolean confirmarCadaArquivo) {

        if (duplicados == null || duplicados.isEmpty()) {
            LogPrinter.log(">> Nenhum arquivo duplicado para remover.");
            return new ResultRemoveDuplicateDto(0, 0);
        }

        // Resumo (ajuda a evitar erro)
        LogPrinter.log("===========================================================================");
        LogPrinter.log(" ATENÇÃO: Remoção de arquivos duplicados");
        LogPrinter.log(" Quantidade de arquivos a remover: " + duplicados.size());
        LogPrinter.log("===========================================================================");

        // Confirmação global
        var dec = ConsoleConfirm.ask(" Deseja continuar e remover os arquivos duplicados?");
        if (dec == ConsoleConfirm.Decision.NO || dec == ConsoleConfirm.Decision.NO_ALL ||
                dec == ConsoleConfirm.Decision.QUIT) {
            LogPrinter.log(" Operação cancelada pelo usuário.");
            return new ResultRemoveDuplicateDto(0, 0);
        }

        boolean yesAll = (dec == ConsoleConfirm.Decision.YES_ALL);
        var total_removidos = 0;
        var total_bytes_removidos = 0L;

        var repository = new FileIndexRepository();

        for (var item : duplicados) {

            File file = new  File(item.getFilePath());
            if (!file.isFile() && !file.exists()) {//Arquivo não existe no disco...
                LogPrinter.log(" >> Arquivo não existe mais: " + item.getFilePath());
                // Tentar remover do banco se existir...
                repository.remover(item.getId());
                continue;
            }

            if (confirmarCadaArquivo && !yesAll) {
                var perFile = ConsoleConfirm.ask(" >> Remover este arquivo?\n >>> " + item.getFilePath());
                if (perFile == ConsoleConfirm.Decision.QUIT) {
                    LogPrinter.log(" Operação encerrada pelo usuário.");
                    break;
                }
                if (perFile == ConsoleConfirm.Decision.NO) continue;
                if (perFile == ConsoleConfirm.Decision.YES_ALL) { yesAll = true; }
            }

            try {
                if (file.delete()) {
                    LogPrinter.log(" >> Arquivo removido: " + item.getFilePath() +
                            " -> Última modificação: " + item.getLastModified());
                    total_removidos++;
                    total_bytes_removidos+=item.getFileSize();
                    repository.remover(item.getId());
                }
            } catch (Exception e) {
                LogPrinter.log(" >> [ERRO] Falha ao remover: " + item.getFilePath() + " -> " +
                        e.getMessage(), true);
            }

        }
        System.gc();

        return new ResultRemoveDuplicateDto(total_removidos, total_bytes_removidos);

    }

}