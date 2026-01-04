package br.com.devd2.utilitario.service;

import br.com.devd2.utilitario.db.FileIndexRepository;
import br.com.devd2.utilitario.dto.FileIndexDto;
import br.com.devd2.utilitario.dto.ResultScannerDuplicateDto;
import br.com.devd2.utilitario.helper.HelperUtils;
import br.com.devd2.utilitario.helper.LogPrinter;

import java.nio.file.*;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class ParallelScannerService {

    public interface Writer {
        void write(FileIndexDto r) throws Exception;
        void flush() throws Exception;
        void close() throws Exception;
    }

    public static ResultScannerDuplicateDto scanFiles(Path basePath, boolean scanSubpastas, Writer writer) {

        AtomicLong total_arquivos = new AtomicLong(0L);
        AtomicLong total_bytes_arquivos = new AtomicLong(0L);

        int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        // fila para desacoplar produtores do writer
        BlockingQueue<FileIndexDto> queue = new LinkedBlockingQueue<>(10_000);
        AtomicLong submitted = new AtomicLong(0);

        // Writer worker (single thread)
        Thread dbWriter = new Thread(() -> {
            try {
                while (true) {
                    FileIndexDto r = queue.take();
                    if (r == POISON) break;
                    writer.write(r);
                }
                writer.flush();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, "h2-writer");

        dbWriter.start();

        var repository = new FileIndexRepository();

        try {

            var strBasePath = basePath.toAbsolutePath().toString();
            LogPrinter.log("> Varrendo em: " + strBasePath);

            var paths = HelperUtils.listarArquivos(basePath, scanSubpastas);
            for (var path : paths) {
                if (!strBasePath.equals(path.getParent().toString())) {
                    strBasePath = path.getParent().toString();
                    LogPrinter.log("> Varrendo em: " + strBasePath);
                }
                total_arquivos.incrementAndGet();
                submitted.incrementAndGet();
                pool.submit(() -> {
                    try {
                        var fileExiste = repository.obterPorPath(path.toFile().getAbsolutePath());
                        if (fileExiste==null) {
                            var file = path.toFile();
                            total_bytes_arquivos.addAndGet(file.length());
                            String hashFile = HelperUtils.gerarHashSHA256(path);
                            FileIndexDto fileIndex = new FileIndexDto(
                                    file.getAbsolutePath(),
                                    file.getName(),
                                    file.length(),
                                    hashFile,
                                    Instant.ofEpochMilli(file.lastModified())
                            );
                            queue.put(fileIndex);
                            LogPrinter.log(">> Arquivo registrado: " + path.toFile().getAbsolutePath());
                        } else {
                            total_bytes_arquivos.addAndGet(fileExiste.getFileSize());
                        }
                    } catch (Exception e) {
                        LogPrinter.log("[ERRO] " + path + " -> " + e.getMessage(), true);
                    } finally {
                        submitted.decrementAndGet();
                    }
                });
            }

            pool.shutdown();
            if (!pool.awaitTermination(3, TimeUnit.HOURS)) {
                LogPrinter.log("Tempo máximo de processamento excedido (3 horas).\n " +
                        "Encerrando execução e interrompendo tarefas pendentes.", true);
                pool.shutdownNow();
                throw new IllegalStateException("Timeout aguardando término do pool");
            }

            // garante que tudo foi enfileirado antes de finalizar
            while (submitted.get() != 0) {
                Thread.sleep(50);
            }

            // sinaliza fim para o writer
            queue.put(POISON);
            dbWriter.join();
            writer.close();

            LogPrinter.log("+------------------------------------------------------------------------+");

            return new ResultScannerDuplicateDto(total_arquivos.get(), total_bytes_arquivos.get());

        } catch (Exception error) {
            LogPrinter.log(" >> [ERRO] Falha ao verificar arquivos no local: " + basePath.toFile().getAbsolutePath() + " -> " + error.getMessage(), true);
            return null;
        }

    }

    private static final FileIndexDto POISON =
            new FileIndexDto("__POISON__", "__POISON__", 0L, "__POISON__", Instant.EPOCH);
}

