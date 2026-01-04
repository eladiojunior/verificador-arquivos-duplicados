package br.com.devd2.utilitario.helper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HelperUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static boolean caminhoValido(Path path) {
        return Files.exists(path) && Files.isDirectory(path);
    }

    public static List<Path> listarArquivos(Path diretorio, boolean isSubdiretorios) throws IOException {
        try (Stream<Path> stream = isSubdiretorios ? Files.walk(diretorio) : Files.list(diretorio)) {
            return stream
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.comparing(Path::toString))
                    .collect(Collectors.toList());
        }
    }

    public static String gerarHashSHA256(Path arquivo)
            throws IOException, NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream is = Files.newInputStream(arquivo)) {
            byte[] buffer = new byte[8192]; // 8 KB
            int bytesLidos;

            while ((bytesLidos = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesLidos);
            }
        }

        return bytesParaHex(digest.digest());
    }

    private static String bytesParaHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String bytesToSizeXB(long bytes) {
        String[] units = {"B", "KB", "MB", "GB"};
        double n = bytes;
        int i = 0;
        while (n >= 1024 && i < units.length - 1) {
            n /= 1024;
            i++;
        }
        DecimalFormat df = (i == 0)
                ? new DecimalFormat("#0")
                : new DecimalFormat("#0.0");

        return df.format(n) + " " + units[i];
    }

     public static String formatNumero(long valor) {
         return String.format("%,d", valor);
     }

    public static String tempoProcessamentoHumano(LocalDateTime inicio, LocalDateTime fim) {
        Duration d = Duration.between(inicio, fim);
        long s = d.getSeconds();

        long h = s / 3600;
        long m = (s % 3600) / 60;
        long sec = s % 60;

        StringBuilder sb = new StringBuilder();
        if (h > 0) sb.append(h).append("h ");
        if (m > 0 || h > 0) sb.append(m).append("m ");
        sb.append(sec).append("s");

        return sb.toString();
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    public static String formatDateTime(Instant instant) {
        if (instant == null) {
            return "-";
        }
        return DATE_TIME_FORMATTER
                .withZone(ZoneId.systemDefault())
                .format(instant);
    }

}