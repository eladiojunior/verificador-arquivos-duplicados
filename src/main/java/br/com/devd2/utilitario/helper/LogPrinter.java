package br.com.devd2.utilitario.helper;

import br.com.devd2.utilitario.Main;

import java.io.InputStream;
import java.util.logging.LogManager;

public final class LogPrinter {
    private LogPrinter() {}
    public static void log(String menssage) {
        log(menssage, false);
    }
    public static void log(String menssage, boolean error) {
        if (error) {
            System.err.println("- ERRO --------------------------------------------------------------------");
            System.err.println(" " + menssage);
            System.err.println("---------------------------------------------------------------------------");
            return;
        }
        System.out.println(menssage);
    }

    public static void configLogging() {
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (is != null) {
                LogManager.getLogManager().readConfiguration(is);
            }
        } catch (Exception e) {
            // Em caso de erro, segue com configuração padrão
        }
    }
}