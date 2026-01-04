## 🚀 Descrição do Projeto

Este projeto é uma aplicação console em Java 17 desenvolvida para:
- Escanear um diretório e, opcionalmente, suas subpastas
- Registrar metadados dos arquivos em um banco de dados local H2 (embedded)
- Identificar **arquivos duplicados** com base em hash de conteúdo
- Remover arquivos duplicados somente mediante confirmação explícita do usuário

O objetivo principal é fornecer uma ferramenta segura, rápida e reutilizável para análise de arquvivos duplicados em ambientes locais.

## 🎯 Funcionalidades
- Leitura de diretórios e subdiretórios
- Geração de hash do conteúdo dos arquivos (SHA-256)
-  Persistência local dos arquivos indexados (H2)
- Identificação de duplicidades por hash
- Remoção opcional de arquivos duplicados
- Confirmação interativa antes de qualquer exclusão
- Execução via linha de comando

## 🏗️ Tecnologias Utilizadas

- Java 17
- JPA (Hibernate)
- H2 Database (embedded / local)
- JDBC / JPQL
- Java NIO (Files, Path, Streams)

## 📦 Como Executar
`
java -jar verificador-arquivos-duplicados.jar -p {path} [opções]
`
### Parâmetros de Linha de Comando
| Parâmetro   | Descrição                                              |
|-------------|--------------------------------------------------------|
| `-p {path}` | **Obrigatório.** Caminho da pasta a ser analisada      |
| `-s`        | (Opcional) Inclui subpastas na varredura               |
| `-r`        | (Opcional) Remove arquivos duplicados após confirmação |
| `-h`        | (Opcional) Exibe informações sobre a solução           |  

### Exemplos de Uso
Exibe informações sobre a solução
```sh
java -jar verificador-arquivos-duplicados.jar -h
```

Apenas escanear a pasta (sem subpastas)
```sh
java -jar verificador-arquivos-duplicados.jar -p /home/fotos
```

Escanear pasta + subpastas

```sh
java -jar verificador-arquivos-duplicados.jar -p /home/fotos -s
```

Escanear e remover duplicados (com confirmação)

```sh
java -jar verificador-arquivos-duplicados.jar -p /home/fotos -s -r
```

### Remoção de Arquivos Duplicados (Segurança)

Quando o parâmetro `-r` é informado, o sistema:
1. Identifica os arquivos duplicados
2. Exibe um **resumo da operação**, por exemplo:<br/>
> ===========================================================================<br/>
ATENÇÃO: Remoção de arquivos duplicados<br/>
Quantidade de arquivos a remover: X<br/>
===========================================================================
3. Solicita **confirmação explícita** do usuário:<br/>
>  Deseja continuar e remover os arquivos duplicados?<br/>
[s=sim, n=não, ss=sim para todos, nn=não para todos, q=sair] (padrão: n):

✔️ Apenas respostas **s**, **ss**, **S** ou **SS** confirmam a remoção<br/>
❌ Qualquer outra resposta cancela a exclusão<br/>
> Essa confirmação evita exclusões indevidas ou acidentais.

### Como a Duplicidade é Determinada
Dois ou mais arquivos são considerados duplicados quando:
- Possuem o mesmo hash de conteúdo
- Independentemente de nome ou localização

## 📜 Licença
Este projeto é licenciado sob a **MIT License**.

## ✨ Contato
📧 Email: eladiojunior@gmail.com (Aceito PIX, qualquer valor $$$)