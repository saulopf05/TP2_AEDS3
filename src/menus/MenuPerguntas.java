package menus;

import java.util.Scanner;
import java.util.ArrayList;

import arquivos.ArquivoPergunta;
import entidades.Pergunta;
import entidades.Resposta;
import entidades.Usuario;

public class MenuPerguntas {
    private static Scanner console = Console.entrada();
    private Usuario usuario;
    private ArquivoPergunta arqPerguntas;
    private MenuResposta menuResposta;

    public MenuPerguntas(Usuario usuario, ArquivoPergunta arqPerguntas, MenuResposta menuResposta) {
        this.usuario = usuario;
        this.arqPerguntas = arqPerguntas;
        this.menuResposta = menuResposta;
    }

    /** Lista as perguntas do usuário; o número de uma pergunta abre os seus dados e respostas. */
    public void menu() {
        String opcao;

        try {
            do {
                Console.cabecalho("Início > Minha área > Minhas perguntas");

                ArrayList<Pergunta> perguntas = arqPerguntas.getPerguntasPorUsuario(usuario.getId());

                if (perguntas.isEmpty()) {
                    System.out.println("\nVocê ainda não possui perguntas.");
                }

                for (int i = 0; i < perguntas.size(); i++) {
                    Pergunta p = perguntas.get(i);
                    // O estado só é mostrado quando a pergunta está arquivada;
                    // o ID real nunca aparece na tela.
                    System.out.println("\n(" + (i + 1) + ") " + Formato.dataHora(p.criacao) +
                        (p.ativa ? "" : "  ARQUIVADA"));
                    System.out.println("    " + p.pergunta);
                }

                System.out.println("\n(A) Incluir pergunta");
                System.out.println("\n(R) Retornar ao menu anterior");

                opcao = Console.lerOpcao();
                int item = Console.numeroDoItem(opcao, perguntas.size());

                if (item > 0) {
                    abrirPergunta(perguntas.get(item - 1));
                } else if (opcao.equals("A")) {
                    incluir();
                } else if (!opcao.equals("R")) {
                    System.out.println("\nOpção inválida!");
                }
            } while (!opcao.equals("R"));

        } catch (Exception e) {
            System.out.println("\nErro ao listar perguntas!");
            e.printStackTrace();
        }
    }

    /**
     * Dados de uma pergunta do usuário com as respostas recebidas. O autor da
     * pergunta pode abrir qualquer resposta e arquivá-la.
     */
    private void abrirPergunta(Pergunta pergunta) throws Exception {
        String caminho = "Início > Minha área > Minhas perguntas > Pergunta";
        String opcao;

        do {
            Console.cabecalho(caminho);
            System.out.println("\n" + Formato.dataHora(pergunta.criacao));
            System.out.println(pergunta.pergunta);
            System.out.println("Palavras-chave: " + pergunta.palavrasChave);

            if (!pergunta.ativa) {
                System.out.println("\nEsta pergunta está arquivada.");
            }

            ArrayList<Resposta> respostas = menuResposta.exibirRespostas(pergunta);

            if (pergunta.ativa) {
                System.out.println("\n(A) Alterar dados da pergunta");
                System.out.println("(B) Arquivar pergunta");
            }
            System.out.println("\n(R) Retornar");

            opcao = Console.lerOpcao();
            int item = Console.numeroDoItem(opcao, respostas.size());

            if (item > 0) {
                menuResposta.abrirResposta(pergunta, respostas.get(item - 1), caminho + " > Resposta", true);
            } else if (opcao.equals("A") && pergunta.ativa) {
                alterar(pergunta);
            } else if (opcao.equals("B") && pergunta.ativa) {
                arquivar(pergunta);
            } else if (!opcao.equals("R")) {
                System.out.println("\nOpção inválida!");
            }
        } while (!opcao.equals("R"));
    }

    private void incluir() {
        try {
            Console.cabecalho("Início > Minha área > Minhas perguntas > Incluir");
            System.out.print("\nPergunta (vazio para cancelar): ");
            String textoPergunta = console.nextLine();

            if(textoPergunta.isEmpty()) {
                return;
            }

            System.out.print("Palavras-chave (separadas por ;): ");
            String palavrasChave = console.nextLine();

            if(palavrasChave.isEmpty()) {
                System.out.println("\nPalavras-chave inválidas.");
                return;
            }

            Pergunta p = new Pergunta(
                usuario.getId(),
                textoPergunta,
                palavrasChave
            );

            arqPerguntas.create(p);
            System.out.println("\nPergunta cadastrada com sucesso!");

        } catch(Exception e) {
            System.out.println("\nErro ao cadastrar pergunta!");
            e.printStackTrace();
        }
    }

    private void alterar(Pergunta p) {
        try {
            Console.cabecalho("Início > Minha área > Minhas perguntas > Pergunta > Alterar");

            System.out.println("\nPergunta atual: " + p.pergunta);
            System.out.print("Nova pergunta (deixe vazio para não alterar): ");
            String novaPergunta = console.nextLine();

            if (!novaPergunta.isEmpty()) {
                p.pergunta = novaPergunta;
            }

            System.out.println("Palavras-chave atuais: " + p.palavrasChave);
            System.out.print("Novas palavras-chave (deixe vazio para não alterar): ");
            String novasPalavras = console.nextLine();

            if (!novasPalavras.isEmpty()) {
                p.palavrasChave = novasPalavras;
            }

            // Atualiza a data de alteração sem mexer nos outros campos
            p.alteracao = System.currentTimeMillis();

            if (arqPerguntas.update(p)) {
                System.out.println("\nPergunta alterada com sucesso!");
            } else {
                System.out.println("\nNão foi possível atualizar a pergunta.");
            }

        } catch (Exception e) {
            System.out.println("\nErro ao alterar a pergunta!");
            e.printStackTrace();
        }
    }

    private void arquivar(Pergunta p) {
        try {
            System.out.print("\nConfirma o arquivamento desta pergunta? (S/N): ");

            String resposta = console.nextLine().toUpperCase();
            char confirma = resposta.isEmpty() ? ' ' : resposta.charAt(0);

            if (confirma == 'S') {
                p.ativa = false; // Desativa a pergunta
                p.alteracao = System.currentTimeMillis();

                if (arqPerguntas.update(p)) {
                    System.out.println("\nPergunta arquivada com sucesso!");
                } else {
                    System.out.println("\nNão foi possível arquivar a pergunta.");
                }
            } else {
                System.out.println("\nArquivamento cancelado.");
            }

        } catch (Exception e) {
            System.out.println("\nErro ao arquivar a pergunta!");
            e.printStackTrace();
        }
    }
}
