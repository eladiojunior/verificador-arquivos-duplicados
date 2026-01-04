package br.com.devd2.utilitario;

import br.com.devd2.utilitario.db.JpaBatchWriter;
import br.com.devd2.utilitario.dto.ResultProcessDto;
import br.com.devd2.utilitario.helper.*;
import br.com.devd2.utilitario.service.ParallelScannerService;
import br.com.devd2.utilitario.service.RemoveDuplicateService;
import br.com.devd2.utilitario.service.ScannerDuplicateService;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Programa responsável por verificar arquivos duplicados.
 * @author Eladio Júnior
 * @since 20/12/2025
 */
public class Main {

    public static void main(String[] args) {

        LogPrinter.configLogging();

        CliConfig config = CliParser.parse(args);

        // 1) Help tem prioridade e encerra
        if (config.help()) {
            HelpPrinter.print();
            return;
        }

        // 2) Validação do parâmetro obrigatório -p
        if (config.path() == null || config.path().isBlank()) {
            LogPrinter.log("Parâmetro "+ CliParams.PARAM_PATH_SHORT+" {pasta} é obrigatório.", true);
            HelpPrinter.print();
            return;
        }

        Path basePath = Path.of(config.path());
        if (!HelperUtils.caminhoValido(basePath)) {
            LogPrinter.log("Caminho inválido ou não é diretório: " + basePath.toAbsolutePath(),true);
            HelpPrinter.print();
            return;
        }

        // 3) Aqui você chama seu fluxo principal
        var dtInicio = LocalDateTime.now();
        LogPrinter.log("+-------------------------------------------------------------------------+");
        LogPrinter.log("> Iniciando: " + HelperUtils.formatDateTime(dtInicio));
        LogPrinter.log("+-------------------------------------------------------------------------+");

        var resultProcess = process(basePath, config.scanSubpastas(), config.removeDuplicados());

        var dtFinal = LocalDateTime.now();
        LogPrinter.log("+-------------------------------------------------------------------------+");
        LogPrinter.log(" Finalizado: " + HelperUtils.formatDateTime(dtFinal));
        LogPrinter.log("+-------------------------------------------------------------------------+");

        if (resultProcess == null) {
            return; //ERRO
        }

        // 4) Apresentação do resultado do processamento se não houver ERRO!
        LogPrinter.log(" ");
        LogPrinter.log("+-------------------------------------------------------------------------+");
        LogPrinter.log("|                         RESUMO DO PROCESSAMENTO                         |");
        LogPrinter.log("+-------------------------------------------------------------------------+");
        var tempoProcessamento = HelperUtils.tempoProcessamentoHumano(dtInicio, dtFinal);
        LogPrinter.log(" Tempo total de processamento -------: " + tempoProcessamento);
        LogPrinter.log(" ");
        LogPrinter.log(" Modo de execução");
        LogPrinter.log("  - Subpastas incluídas -------------: " + (config.scanSubpastas() ? "SIM" : "NÃO"));
        LogPrinter.log("  - Remoção de duplicados -----------: " + (config.removeDuplicados() ? "SIM" : "NÃO"));
        LogPrinter.log(" ");
        LogPrinter.log(" Arquivos analisados");
        LogPrinter.log("  - Quantidade total ----------------: "
                + HelperUtils.formatNumero(resultProcess.totalFiles()));
        LogPrinter.log("  - Tamanho total -------------------: "
                + HelperUtils.bytesToSizeXB(resultProcess.totalBytesFiles()));
        LogPrinter.log(" ");
        LogPrinter.log(" Duplicidades identificadas");
        LogPrinter.log("  - Arquivos duplicados -------------: "
                + HelperUtils.formatNumero(resultProcess.totalFilesDuplicates()));
        LogPrinter.log("  - Espaço potencialmente liberável -: "
                + HelperUtils.bytesToSizeXB(resultProcess.totalBytesFilesDuplicates()));
        if (config.removeDuplicados()) {
            LogPrinter.log(" ");
            LogPrinter.log(" Remoção de arquivos duplicados");
            LogPrinter.log("  - Arquivos removidos --------------: "
                    + HelperUtils.formatNumero(resultProcess.totalFilesRemoved()));
            LogPrinter.log("  - Espaço efetivamente liberado ----: "
                    + HelperUtils.bytesToSizeXB(resultProcess.totalBytesFilesRemoved()));
        }
        LogPrinter.log("+-------------------------------------------------------------------------+");

    }

    /**
     * Realiza o processamento em sequência... varre, verifica e remove...
     * @param basePath - Local que verificação dos arquivos.
     * @param scanSubpastas - Flag se é para varrer as subpastas.
     * @param removeDuplicados - Flag se é para remover os arquivos duplicados, com confirmação.
     * @return Resultado do processamento para apresentação dos quantitativos.
     */
    private static ResultProcessDto process(Path basePath, boolean scanSubpastas, boolean removeDuplicados) {
        var scannerService = new ScannerDuplicateService();

        long total_arquivos;
        long total_bytes_arquivos;
        long total_duplicados;
        long total_bytes_duplicados;
        long total_removidos = 0L;
        long total_bytes_removidos = 0L;

        // 4) Verifica o local e varre para registrar os arquivos em banco local...
        // Implementação de paralelismo...
        var writer = new JpaBatchWriter(200); // lote de 200 costuma ser ótimo
        var resultScanner = ParallelScannerService.scanFiles(basePath, scanSubpastas, writer);
        // var resultScanner = scannerService.scanFiles(basePath, scanSubpastas);
        if (resultScanner == null)
            return null; //Erro
        total_arquivos = resultScanner.totalFilesScan();
        total_bytes_arquivos = resultScanner.totalBytesFileScan();

        // 5) Verifica os arquivos duplicados e deixa a lista pronta para remover, se necessário...
        var resultCheckDuplicates = scannerService.checkFilesDuplicates();
        if (resultCheckDuplicates == null)
            return null; //Erro
        total_duplicados = resultCheckDuplicates.totalFilesDuplicates();
        total_bytes_duplicados = resultCheckDuplicates.totalBytesFilesDuplicates();

        if (removeDuplicados) {
            var resultRemoveDuplicate = new RemoveDuplicateService()
                    .removerComConfirmacao(resultCheckDuplicates.listDuplicates(), true);
            if (resultRemoveDuplicate == null)
                return null; //ERRO
            total_removidos = resultRemoveDuplicate.totalFilesRemoved();
            total_bytes_removidos = resultRemoveDuplicate.totalBytesFileRemoved();
        }

        return new ResultProcessDto(total_arquivos, total_bytes_arquivos, total_duplicados, total_bytes_duplicados,
                total_removidos, total_bytes_removidos);
    }

}