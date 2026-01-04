package br.com.devd2.utilitario.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleConfirm {
    private ConsoleConfirm() {}

    public enum Decision {
        YES, NO, YES_ALL, NO_ALL, QUIT
    }

    /**
     * Confirmação segura.
     * - Enter => NÃO (default)
     * - s/ss => SIM / SIM para todos
     * - n/nn => NÃO / NÃO para todos
     * - q => sair/abort
     */
    public static Decision ask(String message) {
        String prompt = message + "\n [s=sim, n=não, ss=sim para todos, nn=não para todos, q=sair] (padrão: n): ";

        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(System.in, StandardCharsets.UTF_8)
            );

            while (true) {
                System.out.print(prompt);
                String line = br.readLine();
                if (line == null) return Decision.NO; // EOF => não

                String v = line.trim().toLowerCase();

                switch (v) {
                    case "", "n" -> {
                        return Decision.NO;
                    }
                    case "s" -> {
                        return Decision.YES;
                    }
                    case "ss" -> {
                        return Decision.YES_ALL;
                    }
                    case "nn" -> {
                        return Decision.NO_ALL;
                    }
                    case "q" -> {
                        return Decision.QUIT;
                    }
                }

                System.out.println("Entrada inválida. Use s, n, ss, nn ou q.");
            }
        } catch (IOException e) {
            return Decision.NO;
        }
    }
}