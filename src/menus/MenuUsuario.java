package menus;

import java.util.ArrayList;
import java.util.Scanner;

import arquivos.ArquivoAvaliacao;
import arquivos.ArquivoPergunta;
import arquivos.ArquivoResposta;
import arquivos.ArquivoUsuario;
import entidades.Avaliacao;
import entidades.Pergunta;
import entidades.Resposta;
import entidades.Usuario;

public class MenuUsuario {
    private static Scanner console = Console.entrada();
    private static final int PERGUNTAS_POR_PAGINA = 5;

    private Usuario usuario;
    private ArquivoUsuario arqUsuarios;
    private ArquivoPergunta arqPerguntas;
    private ArquivoResposta arqRespostas;
    private ArquivoAvaliacao arqAvaliacoes;
    private MenuResposta menuResposta;

    public MenuUsuario(Usuario usuario, ArquivoUsuario arqUsuarios) throws Exception {
        this.usuario = usuario;
        this.arqUsuarios = arqUsuarios;
        this.arqPerguntas = new ArquivoPergunta();
        this.arqRespostas = new ArquivoResposta();
        this.arqAvaliacoes = new ArquivoAvaliacao(arqRespostas);
        this.menuResposta = new MenuResposta(usuario, arqUsuarios, arqRespostas, arqAvaliacoes);
    }

    public void menu() {
        String opcao;

        do {
            Console.cabecalho("Início");

            System.out.println("\n(A) Minha área");
            System.out.println("(B) Buscar perguntas");
            System.out.println("\n(S) Sair");

            opcao = Console.lerOpcao();

            switch (opcao) {
                case "A":
                    minhaArea();
                    break;

                case "B":
                    menuBuscarPerguntas();
                    break;

                case "S":
                    break;

                default:
                    System.out.println("\nOpção inválida!");
                    break;
            }

        } while (!opcao.equals("S"));
    }

    private void menuBuscarPerguntas() {
        String opcao;

        do {
            Console.cabecalho("Início > Buscar perguntas");

            System.out.println("\n(A) Perguntas mais recentes");
            System.out.println("(B) Busca por palavras-chave");
            System.out.println("\n(R) Retornar");

            opcao = Console.lerOpcao();

            switch (opcao) {
                case "A":
                    perguntasRecentes();
                    break;

                case "B":
                    System.out.println(
                        "\nA busca por palavras-chave ainda não foi implementada."
                    );
                    break;

                case "R":
                    break;

                default:
                    System.out.println("\nOpção inválida!");
                    break;
            }

        } while (!opcao.equals("R"));
    }

    /** Perguntas ativas de todos os usuários, cinco por página, da mais recente para a mais antiga. */
    private void perguntasRecentes() {
        int pagina = 0;
        String opcao;

        try {
            do {
                ArrayList<Pergunta> perguntas =
                    new ArrayList<>(arqPerguntas.listarRecentes(pagina, PERGUNTAS_POR_PAGINA));

                Console.cabecalho("Início > Buscar perguntas > Perguntas mais recentes");

                if (perguntas.isEmpty()) {
                    System.out.println("\nNenhuma pergunta ativa no sistema.");
                }

                for (int i = 0; i < perguntas.size(); i++) {
                    Pergunta p = perguntas.get(i);
                    System.out.println("\n(" + (i + 1) + ") " + p.pergunta);
                    System.out.println("    Criada por " + menuResposta.nomeDoAutor(p.getIdUsuario()) +
                        " em " + Formato.dataHoraExtenso(p.criacao));
                }

                System.out.println("\n(A) Página anterior");
                System.out.println("(B) Próxima página");
                System.out.println("\n(R) Retornar");

                opcao = Console.lerOpcao();
                int item = Console.numeroDoItem(opcao, perguntas.size());

                if (item > 0) {
                    menuResposta.abrirPergunta(perguntas.get(item - 1));
                } else if (opcao.equals("A")) {
                    if (pagina > 0) pagina--;
                    else System.out.println("\nVocê já está na primeira página.");
                } else if (opcao.equals("B")) {
                    if (!arqPerguntas.listarRecentes(pagina + 1, PERGUNTAS_POR_PAGINA).isEmpty()) pagina++;
                    else System.out.println("\nVocê já está na última página.");
                } else if (!opcao.equals("R")) {
                    System.out.println("\nOpção inválida!");
                }
            } while (!opcao.equals("R"));

        } catch (Exception e) {
            System.out.println("\nErro ao buscar perguntas!");
            e.printStackTrace();
        }
    }

    private void minhaArea() {
        String opcao;

        do {
            Console.cabecalho("Início > Minha área");

            System.out.println("\n(A) Meus dados");
            System.out.println("(B) Minhas perguntas");
            System.out.println("(C) Minhas respostas");
            System.out.println("(D) Minhas avaliações");
            System.out.println("\n(R) Retornar ao menu anterior");

            opcao = Console.lerOpcao();

            switch (opcao) {
                case "A":
                    meusDados();
                    break;

                case "B":
                    new MenuPerguntas(usuario, arqPerguntas, menuResposta).menu();
                    break;

                case "C":
                    minhasRespostas();
                    break;

                case "D":
                    minhasAvaliacoes();
                    break;

                case "R":
                    break;

                default:
                    System.out.println("\nOpção inválida!");
                    break;
            }
        } while (!opcao.equals("R"));
    }

    /** Respostas ativas do usuário, pela Árvore B+ (idUsuario; idResposta). */
    private void minhasRespostas() {
        String opcao;

        try {
            do {
                Console.cabecalho("Início > Minha área > Minhas respostas");

                ArrayList<Resposta> respostas = arqRespostas.getRespostasPorUsuario(usuario.getId());

                if (respostas.isEmpty()) {
                    System.out.println("\nVocê não tem respostas ativas.");
                }

                for (int i = 0; i < respostas.size(); i++) {
                    Resposta r = respostas.get(i);
                    Pergunta p = arqPerguntas.read(r.getIdPergunta());
                    System.out.println("\n(" + (i + 1) + ") " + r.resposta);
                    System.out.println("    Pergunta: " + (p != null ? p.pergunta : "pergunta removida"));
                    System.out.println("    Nota: " + r.nota + "  (" + Formato.avaliacoes(r.avaliacoes) + ")");
                }

                System.out.println("\n(R) Retornar ao menu anterior");

                opcao = Console.lerOpcao();
                int item = Console.numeroDoItem(opcao, respostas.size());

                if (item > 0) {
                    Resposta r = respostas.get(item - 1);
                    Pergunta p = arqPerguntas.read(r.getIdPergunta());
                    if (p != null) {
                        menuResposta.abrirResposta(p, r, "Início > Minha área > Minhas respostas > Resposta", false);
                    } else {
                        System.out.println("\nA pergunta desta resposta não foi encontrada.");
                    }
                } else if (!opcao.equals("R")) {
                    System.out.println("\nOpção inválida!");
                }
            } while (!opcao.equals("R"));

        } catch (Exception e) {
            System.out.println("\nErro ao listar suas respostas!");
            e.printStackTrace();
        }
    }

    /** Avaliações feitas pelo usuário, pela Árvore B+ (idUsuario; idAvaliacao). */
    private void minhasAvaliacoes() {
        String opcao;

        try {
            do {
                Console.cabecalho("Início > Minha área > Minhas avaliações");

                ArrayList<Avaliacao> avaliacoes = arqAvaliacoes.getAvaliacoesPorUsuario(usuario.getId());

                if (avaliacoes.isEmpty()) {
                    System.out.println("\nVocê ainda não avaliou nenhuma resposta.");
                }

                for (int i = 0; i < avaliacoes.size(); i++) {
                    Avaliacao a = avaliacoes.get(i);
                    Resposta r = arqRespostas.read(a.getIdResposta());
                    Pergunta p = (r != null) ? arqPerguntas.read(r.getIdPergunta()) : null;
                    System.out.println("\n(" + (i + 1) + ") " + (r != null ? r.resposta : "resposta removida"));
                    System.out.println("    Pergunta: " + (p != null ? p.pergunta : "pergunta removida"));
                    System.out.println("    Sua avaliação: " + (a.avaliacao ? "Positiva" : "Negativa"));
                }

                System.out.println("\n(R) Retornar ao menu anterior");

                opcao = Console.lerOpcao();
                int item = Console.numeroDoItem(opcao, avaliacoes.size());

                if (item > 0) {
                    Avaliacao a = avaliacoes.get(item - 1);
                    Resposta r = arqRespostas.read(a.getIdResposta());
                    Pergunta p = (r != null) ? arqPerguntas.read(r.getIdPergunta()) : null;
                    if (r != null && p != null) {
                        menuResposta.abrirAvaliacao(a, p, r);
                    } else {
                        System.out.println("\nA resposta avaliada não foi encontrada.");
                    }
                } else if (!opcao.equals("R")) {
                    System.out.println("\nOpção inválida!");
                }
            } while (!opcao.equals("R"));

        } catch (Exception e) {
            System.out.println("\nErro ao listar suas avaliações!");
            e.printStackTrace();
        }
    }

    private void meusDados() {
        String opcao;

        do {
            Console.cabecalho("Início > Minha área > Meus dados");
            System.out.println("\nNome: " + usuario.nome);
            System.out.println("E-mail: " + usuario.email);

            System.out.println("\n(A) Alterar nome");
            System.out.println("(B) Alterar e-mail");
            System.out.println("(C) Alterar senha");
            System.out.println("(D) Alterar pergunta e resposta de recuperação");
            System.out.println("\n(R) Retornar ao menu anterior");

            opcao = Console.lerOpcao();

            switch (opcao) {
                case "A":
                    alterarNome();
                    break;

                case "B":
                    alterarEmail();
                    break;

                case "C":
                    alterarSenha();
                    break;

                case "D":
                    alterarRecuperacao();
                    break;

                case "R":
                    break;

                default:
                    System.out.println("\nOpção inválida!");
                    break;
            }
        } while (!opcao.equals("R"));
    }

    private void alterarNome() {
        try {
            System.out.print("\nNovo nome (vazio para cancelar): ");
            String novoNome = console.nextLine();

            if (novoNome.isEmpty()) {
                return;
            }

            usuario.nome = novoNome;

            if (arqUsuarios.update(usuario)) {
                System.out.println("\nNome alterado com sucesso!");
            } else {
                System.out.println("\nNão foi possível alterar o nome.");
            }

        } catch (Exception e) {
            System.out.println("\nErro ao alterar o nome!");
            e.printStackTrace();
        }
    }

    private void alterarEmail() {
        try {
            System.out.print("\nNovo e-mail (vazio para cancelar): ");
            String novoEmail = console.nextLine();

            if (novoEmail.isEmpty()) {
                return;
            }

            // Verifica se o novo e-mail já pertence a outro usuário
            Usuario usuarioExistente = arqUsuarios.read(novoEmail);

            if (usuarioExistente != null &&
                    usuarioExistente.getId() != usuario.getId()) {
                System.out.println("\nEste e-mail já está cadastrado.");
                return;
            }

            usuario.email = novoEmail;

            if (arqUsuarios.update(usuario)) {
                System.out.println("\nE-mail alterado com sucesso!");
            } else {
                System.out.println("\nNão foi possível alterar o e-mail.");
            }

        } catch (Exception e) {
            System.out.println("\nErro ao alterar o e-mail!");
            e.printStackTrace();
        }
    }

    private void alterarSenha() {
        try {
            System.out.print("\nNova senha (vazio para cancelar): ");
            String novaSenha = console.nextLine();

            if (novaSenha.isEmpty()) {
                return;
            }

            System.out.print("Confirme a nova senha: ");
            String confirmacao = console.nextLine();

            if (!novaSenha.equals(confirmacao)) {
                System.out.println("\nAs senhas não conferem.");
                return;
            }

            usuario.hashSenha = Usuario.gerarHash(novaSenha);

            if (arqUsuarios.update(usuario)) {
                System.out.println("\nSenha alterada com sucesso!");
            } else {
                System.out.println("\nNão foi possível alterar a senha.");
            }

        } catch (Exception e) {
            System.out.println("\nErro ao alterar a senha!");
            e.printStackTrace();
        }
    }

    private void alterarRecuperacao() {
        try {
            System.out.print("\nNova pergunta secreta (vazio para cancelar): ");
            String novaPergunta = console.nextLine();

            if (novaPergunta.isEmpty()) {
                return;
            }

            System.out.print("Nova resposta secreta: ");
            String novaResposta = console.nextLine();

            if (novaResposta.isEmpty()) {
                System.out.println("\nResposta secreta inválida.");
                return;
            }

            usuario.perguntaSecreta = novaPergunta;
            usuario.hashRespostaSecreta = Usuario.gerarHashResposta(novaResposta);

            if (arqUsuarios.update(usuario)) {
                System.out.println("\nPergunta e resposta de recuperação alteradas com sucesso!");
            } else {
                System.out.println("\nNão foi possível alterar os dados de recuperação.");
            }

        } catch (Exception e) {
            System.out.println("\nErro ao alterar os dados de recuperação!");
            e.printStackTrace();
        }
    }
}
