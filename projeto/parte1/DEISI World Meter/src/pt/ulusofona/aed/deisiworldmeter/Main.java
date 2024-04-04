package pt.ulusofona.aed.deisiworldmeter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static ArrayList<String[]> dadosPaises = new ArrayList<>();
    static ArrayList<String[]> dadosCidades = new ArrayList<>();
    static ArrayList<String[]> dadosPopulacao = new ArrayList<>();
    static int[] leituraLinhas = new int[9]; /* [0-2] -> paises.csv || [3-5] -> cidades.csv || [6-8] -> populacao.csv */


    public static ArrayList<String> getObjects(TipoEntidade tipo) {
        ArrayList<String> output = new ArrayList<>();

        if (tipo == TipoEntidade.INPUT_INVALIDO) {
            output.add("paises.csv | " + leituraLinhas[0] + " | " + leituraLinhas[1] + " | " + leituraLinhas[2]);
            output.add("cidades.csv | " + leituraLinhas[3] + " | " + leituraLinhas[4] + " | " + leituraLinhas[5]);
            output.add("populacao.csv | " + leituraLinhas[6] + " | " + leituraLinhas[7] + " | " + leituraLinhas[8]);
        } else if (tipo == TipoEntidade.PAIS) {
            int quantidadeDadoPais = 0;

            for (String[] dadosPais : dadosPaises) {
                if (Integer.parseInt(dadosPais[0].trim()) > 700) {
                    for (String[] dadosPop : dadosPopulacao) {
                        if (dadosPais[0].equals(dadosPop[0])) {
                            quantidadeDadoPais++;
                        }
                    }
                    output.add(dadosPais[3] + " | " + dadosPais[0] + " | " + dadosPais[1].toUpperCase() + " | " + dadosPais[2].toUpperCase() + " | " + quantidadeDadoPais);
                } else {
                    output.add(dadosPais[3] + " | " + dadosPais[0] + " | " + dadosPais[1].toUpperCase() + " | " + dadosPais[2].toUpperCase());
                }
            }
        } else if (tipo == TipoEntidade.CIDADE) {
            for (String[] dadosCidade : dadosCidades) {
                for (String[] dadosPais : dadosPaises) {
                    if (dadosCidade[0].equals(dadosPais[1])) {
                        output.add(dadosCidade[1] + " | " + dadosCidade[0].toUpperCase() + " | " + dadosCidade[2] + " | " + dadosCidade[3] + " | (" + dadosCidade[4] + "," + dadosCidade[5] + ")");
                    }
                }
            }
        }

        return output;
    }


    public static boolean parseFiles(File folder) {
        String linhaAtual;
        String[] dados;
        String[] separador;
        int linhaOK = 0;
        int linhaNOK = 0;
        int primeiraLinhaIncorreta = -1;
        int tamanhoArray = 0;
        int tamanhoLeituraLinhas = -1;
        boolean encontrado;

        if (folder.isDirectory()) {
            File[] arquivos = folder.listFiles();
            boolean achouFicheiro = false;
            int posicaoFicheiro;

            if (arquivos != null) {
                for (posicaoFicheiro = 0; posicaoFicheiro < arquivos.length; posicaoFicheiro++) {
                    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
                    /* Verificar com qual ficheiro estamos trabalhando */
                    if (arquivos[posicaoFicheiro].getName().equals("paises.csv") && (leituraLinhas[0] == 0 && leituraLinhas[2] == 0)) {
                        achouFicheiro = true;
                        tamanhoArray = 4;
                        tamanhoLeituraLinhas = 0;
                        break;
                    } else if (arquivos[posicaoFicheiro].getName().equals("cidades.csv") && (leituraLinhas[3] == 0 && leituraLinhas[5] == 0)) {
                        achouFicheiro = true;
                        tamanhoArray = 6;
                        tamanhoLeituraLinhas = 3;
                        break;
                    } else if (arquivos[posicaoFicheiro].getName().equals("populacao.csv") && (leituraLinhas[6] == 0 && leituraLinhas[8] == 0)) {
                        achouFicheiro = true;
                        tamanhoArray = 5;
                        tamanhoLeituraLinhas = 6;
                        break;
                    }
                    /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
                }

                if (!achouFicheiro) {
                    return false;
                }

                try (BufferedReader br = new BufferedReader(new FileReader(arquivos[posicaoFicheiro]))) {
                    br.readLine();

                    while ((linhaAtual = br.readLine()) != null) {
                        dados = new String[tamanhoArray];
                        encontrado = false;
                        separador = linhaAtual.split(",");

                        if (separador.length != tamanhoArray) {
                            if (primeiraLinhaIncorreta == -1) {
                                primeiraLinhaIncorreta = linhaOK + 1;
                            }
                            linhaNOK++;
                            continue;
                        }

                        for (int i = 0; i < tamanhoArray; i++) {
                            dados[i] = separador[i];
                            if ((dados[1] = separador[1]).equals("Medium")) dados[1] = "";
                            if (separador[3].equals("")) dados[3] = "0";
                        }

                        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
                        /* Guardando informações dos ficheiros em variáveis */
                        if (tamanhoArray == 4) { /* pasta paises */
                            for (String[] dadosPais : dadosPaises) {
                                if (dadosPais[0].equals(dados[0])) {
                                    encontrado = true;
                                    break;
                                }
                            }
                            if (!encontrado) {
                                dadosPaises.add(dados);
                            }
                        }
                        if (tamanhoArray == 5) { /* pasta populacao */
                            dadosPopulacao.add(dados);
                        }
                        if (tamanhoArray == 6) { /* pasta cidades */
                            dadosCidades.add(dados);
                        }
                        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

                        linhaOK++;
                    }
                } catch (IOException e) {
                    return false;
                }

                /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
                /* Guardar informações para o Enumerado do tipo INPUT_INVALIDO */
                leituraLinhas[tamanhoLeituraLinhas] = linhaOK;
                leituraLinhas[tamanhoLeituraLinhas + 1] = linhaNOK;
                leituraLinhas[tamanhoLeituraLinhas + 2] = primeiraLinhaIncorreta;
                /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

                /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
                /* Ler os demais ficheiros para ter os dados de todos eles */
                if (leituraLinhas[0] == 0 || leituraLinhas[3] == 0 || leituraLinhas[6] == 0) {
                    parseFiles(new File("."));
                }
                /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
            }
        }

        return true;
    }


    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        boolean parseOk = parseFiles(new File("./"));
        if (!parseOk) {
            System.out.println("Erro na leitura dos ficheiros");
            return;
        }
        long end = System.currentTimeMillis();

        ArrayList<String> aaa = getObjects(TipoEntidade.INPUT_INVALIDO);
        System.out.println(aaa.get(0));
        System.out.println(aaa.get(1));
        System.out.println(aaa.get(2));
        System.out.println();

        ArrayList<String> bbb = getObjects(TipoEntidade.PAIS);
        for (String bb : bbb) System.out.println(bb);
        System.out.println();

        ArrayList<String> ccc = getObjects(TipoEntidade.CIDADE);
        for (String cc : ccc) System.out.println(cc);

        System.out.println("Ficheiros lidos com sucesso em " + (end - start) + " ms.");
    }
}
