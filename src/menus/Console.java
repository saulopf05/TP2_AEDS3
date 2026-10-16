package menus;

import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Entrada padrão compartilhada por todos os menus.
 *
 * Cada menu manter o seu próprio Scanner sobre o System.in faz com que os
 * buffers internos disputem a mesma entrada: o Scanner de um menu lê um bloco
 * inteiro, e as linhas que sobram somem quando o controle passa para o menu
 * seguinte. Por isso existe uma única instância para todo o sistema.
 */
public class Console {

    private static final Scanner ENTRADA = new Scanner(System.in);

    private Console() {
    }

    public static Scanner entrada() {
        return ENTRADA;
    }

    /** Cabeçalho das telas, com o caminho (breadcrumb) até a tela atual. */
    public static void cabecalho(String caminho) {
        System.out.println("\n\nAJUDA AÍ 1.0");
        System.out.println("------------");
        System.out.println("\n> " + caminho);
    }

    /**
     * Lê a opção de um menu, já sem espaços e em maiúsculas.
     * Se a entrada padrão terminar, encerra o programa: sem isso os menus
     * repetiriam "Opção inválida" indefinidamente.
     */
    public static String lerOpcao() {
        System.out.print("\nOpção: ");
        try {
            return ENTRADA.nextLine().trim().toUpperCase();
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada.");
            System.exit(0);
            return "";
        }
    }

    /**
     * Interpreta a opção como o número de um item listado na tela.
     * Devolve o número (de 1 a total) ou -1 se a opção não for um item válido.
     */
    public static int numeroDoItem(String opcao, int total) {
        try {
            int n = Integer.parseInt(opcao);
            return (n >= 1 && n <= total) ? n : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
