package br.com.devd2.utilitario.helper;

public record CliConfig(
        String path,
        boolean scanSubpastas,
        boolean removeDuplicados,
        boolean help
) {}
