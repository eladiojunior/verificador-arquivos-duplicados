package br.com.devd2.utilitario.helper;

import java.util.ArrayList;
import java.util.List;

import static br.com.devd2.utilitario.helper.CliParams.*;

public final class CliParser {

    private CliParser() {}

    public static CliConfig parse(String[] args) {
        boolean help = false;
        boolean subpastas = false;
        boolean remove = false;
        String path = null;

        List<String> argList = List.of(args);

        for (int i = 0; i < argList.size(); i++) {

            String arg = argList.get(i);

            switch (arg) {
                case PARAM_HELP_SHORT, PARAM_HELP_LONG -> help = true;
                case PARAM_SUBPASTA_SHORT -> subpastas = true;
                case PARAM_REMOVE_SHORT -> remove = true;

                case PARAM_PATH_SHORT -> {
                    List<String> pathParts = new ArrayList<>();
                    int j = i + 1;
                    while (j < argList.size() && !argList.get(j).startsWith("-")) {
                        pathParts.add(argList.get(j));
                        j++;
                    }
                    if (!pathParts.isEmpty()) {
                        path = String.join(" ", pathParts);
                        i = j - 1; // avança o índice
                    }
                }
            }
        }

        return new CliConfig(path, subpastas, remove, help);

    }

}