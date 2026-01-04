package br.com.devd2.utilitario.helper;

import static br.com.devd2.utilitario.helper.CliParams.*;

public final class HelpPrinter {

    private HelpPrinter() {}

    public static void print() {
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                  VERIFICADOR DE ARQUIVOS DUPLICADOS                     |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("| Descrição:                                                              |");
        System.out.println("|  Escaneia diretórios, registra arquivos no H2 e identifica/remover      |");
        System.out.println("|  duplicados com base no hash do conteúdo.                               |");
        System.out.println("|                                                                         |");
        System.out.println("| Versão: 1.0                                                             |");
        System.out.println("| Desenvolvido por: Eladio Lima Magalhães Júnior                          |");
        System.out.println("|                                                                         |");
        System.out.println("| Uso:                                                                    |");
        System.out.println("|  java -jar verificador-arquivos-duplicados.jar " + PARAM_PATH_SHORT + " {pasta} [opções]      |");
        System.out.println("|                                                                         |");
        System.out.println("| Parâmetros:                                                             |");
        System.out.println("|  " + PARAM_PATH_SHORT + " {pasta}   (obrigatório) - Caminho da pasta a verificar              |");
        System.out.println("|  " + PARAM_SUBPASTA_SHORT + "           (opcional)    - Inclui subpastas na varredura             |");
        System.out.println("|  " + PARAM_REMOVE_SHORT + "           (opcional)    - Remove arquivos duplicados                |");
        System.out.println("|  " + PARAM_HELP_SHORT + " / " + PARAM_HELP_LONG + "  (opcional)    - Exibe esta ajuda                          |");
        System.out.println("|                                                                         |");
        System.out.println("| Exemplo:                                                                |");
        System.out.println("|  java -jar verificador-arquivos-duplicados.jar -p C:\\Fotos -s -r        |");
        System.out.println("|                                                                         |");
        System.out.println("| Observação:                                                             |");
        System.out.println("|  Ao usar -r o sistema solicitará confirmação antes de remover arquivos. |");
        System.out.println("|                                                                         |");
        System.out.println("| Contribuição (opcional):                                                |");
        System.out.println("|  PIX: eladiojunior@gmail.com                                            |");
        System.out.println("+-------------------------------------------------------------------------+");
    }

}