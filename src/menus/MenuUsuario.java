package menus;

import java.util.ArrayList;
import java.util.Scanner;

import arquivos.ArquivoPergunta;
import arquivos.ArquivoResposta;
import arquivos.ArquivoUsuario;
import arquivos.ArquivoVoto;
import entidades.Pergunta;
import entidades.Resposta;
import entidades.Usuario;
import entidades.Voto;
import indices.ParPerguntaResposta;
import indices.ParUsuarioPergunta;

public class MenuUsuario {
    private static Scanner console = Console.entrada();
    private Usuario usuario;
    private ArquivoUsuario arqUsuarios;
    private ArrayList<Integer> mapPerguntas;

    public MenuUsuario(Usuario usuario, ArquivoUsuario arqUsuarios) {
        this.usuario = usuario;
        this.arqUsuarios = arqUsuarios;
        this.mapPerguntas = new ArrayList<>();
    }

    public void menu() {
        char opcao;

        do {
            System.out.println("\n\nAJUDA AÍ 1.0");
            System.out.println("------------");
            System.out.println("Usuário: " + usuario.nome);

            System.out.println("\nA - Minha área");
            System.out.println("B - Buscar perguntas");
            System.out.println("S - Sair");

            System.out.print("\nOpção: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);
            } catch (Exception e) {
                opcao = ' ';
            }

            switch (opcao) {
                case 'A':
                    minhaArea();
                    break;

                case 'B':
                    menuBuscarPerguntas();
                    break;

                case 'S':
                    break;

                default:
                    System.out.println("Opção inválida!");
                    break;
            }

        } while (opcao != 'S');
    }

    private void menuBuscarPerguntas() {
        char opcao;

        do {
            System.out.println("\n\nAJUDA AÍ 1.0");
            System.out.println("------------");
            System.out.println("> Início > Buscar perguntas");

            System.out.println("\n(A) Perguntas mais recentes");
            System.out.println("(B) Busca por palavras-chave");
            System.out.println("\n(R) Retornar");

            System.out.print("\nOpção: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);
            } catch (Exception e) {
                opcao = ' ';
            }

            switch (opcao) {
                case 'A':
                    listarPerguntasRecentes();
                    break;

                case 'B':
                    System.out.println(
                        "\nA busca por palavras-chave ainda não foi implementada."
                    );
                    break;

                case 'R':
                    break;

                default:
                    System.out.println("\nOpção inválida!");
                    break;
            }

        } while (opcao != 'R');
    }

    private void listarPerguntasRecentes() {
        try {
            ArquivoPergunta arqPerguntas = new ArquivoPergunta();
            ArquivoUsuario arqUsuarios = new ArquivoUsuario();
            int pagina = 0;
            int escolha = -1;
            boolean sair = false;

            while (!sair && escolha == -1) {
                mapPerguntas.clear();
                ArrayList<Pergunta> recentes = new ArrayList<>(arqPerguntas.listarRecentes(pagina, 5));
                System.out.println("\nPerguntas mais recentes — página " + (pagina + 1));
                System.out.println("----------------------------------------");
                if (recentes.isEmpty()) {
                    if (pagina == 0) System.out.println("Nenhuma pergunta ativa no fórum.");
                    else System.out.println("Não há mais perguntas nesta página.");
                    System.out.println("P - Página anterior | R - Voltar");
                } else {
                    int numero = 1;
                    for (Pergunta p : recentes) {
                        Usuario autor = arqUsuarios.read(p.getIdUsuario());
                        System.out.println("\n(" + numero + ") " + Formato.dataHora(p.criacao));
                        System.out.println(p.pergunta);
                        System.out.println("Autor: " + (autor != null ? autor.nome : "Desconhecido"));
                        System.out.println("Palavras-chave: " + p.palavrasChave);
                        mapPerguntas.add(p.getId());
                        numero++;
                    }
                    System.out.println("\nN - Próxima página | P - Página anterior | R - Voltar");
                }
                System.out.print("Selecione o número da pergunta ou uma opção: ");
                String entrada = console.nextLine().trim();
                if (entrada.equalsIgnoreCase("R") || entrada.isEmpty()) {
                    sair = true;
                } else if (entrada.equalsIgnoreCase("N")) {
                    if (recentes.size() == 5) pagina++;
                    else System.out.println("Você já está na última página.");
                } else if (entrada.equalsIgnoreCase("P")) {
                    if (pagina > 0) pagina--;
                    else System.out.println("Você já está na primeira página.");
                } else {
                    try {
                        int n = Integer.parseInt(entrada);
                        if (n >= 1 && n <= mapPerguntas.size()) escolha = n;
                        else System.out.println("Número inválido.");
                    } catch (NumberFormatException e) {
                        System.out.println("Opção inválida.");
                    }
                }
            }
            if (sair || escolha == -1) return;

            int idPergunta = mapPerguntas.get(escolha - 1);
            Pergunta selecionada = arqPerguntas.read(idPergunta);

            if (selecionada == null) {
                System.out.println("\nPergunta não encontrada.");
                return;
            }

            Usuario autorPerg = arqUsuarios.read(selecionada.getIdUsuario());

            System.out.println("\nDetalhes da pergunta");
            System.out.println("-------------------");
            System.out.println("Autor: " + (autorPerg != null ? autorPerg.nome : "Desconhecido"));
            System.out.println("Pergunta: " + selecionada.pergunta);
            System.out.println("Palavras-chave: " + selecionada.palavrasChave);
            System.out.println("Nota: " + selecionada.nota);
            System.out.println("Criada em: " + Formato.dataHora(selecionada.criacao));
            System.out.println("Alterada em: " + Formato.dataHora(selecionada.alteracao));
            System.out.println("Status: " + (selecionada.ativa ? "Ativa" : "Arquivada"));

            char opcaoDetalhe;
            do {
                System.out.println("\nA - Ver respostas");
                System.out.println("R - Retornar");
                System.out.print("\nOpção: ");

                try {
                    opcaoDetalhe = console.nextLine().toUpperCase().charAt(0);
                } catch (Exception e) {
                    opcaoDetalhe = ' ';
                }

                switch (opcaoDetalhe) {
                    case 'A':
                        try {
                            MenuResposta menuResposta = new MenuResposta(selecionada, usuario);
                            menuResposta.menu();
                        } catch (Exception e) {
                            System.out.println("\nErro ao acessar o menu de respostas!");
                            e.printStackTrace();
                        }
                        break;

                    case 'R':
                        break;

                    default:
                        System.out.println("\nOpção inválida!");
                        break;
                }
            } while (opcaoDetalhe != 'R');

        } catch (NumberFormatException e) {
            System.out.println("\nNúmero inválido! Digite apenas números.");
        } catch (Exception e) {
            System.out.println("\nErro ao buscar perguntas!");
            e.printStackTrace();
        }
    }

    private void minhaArea() {

        System.out.println("\nMinha área");
        System.out.println("----------");
        System.out.println("A - Meus dados");
        System.out.println("B - Minhas perguntas");
        System.out.println("C - Minhas respostas");
        System.out.println("D - Minhas avaliações");
        System.out.println("R - Retornar");
        System.out.print("\nOpção: ");

        char opcao;

        try {
            opcao = console.nextLine().toUpperCase().charAt(0);
        } catch (Exception e) {
            opcao = ' ';
        }

        switch (opcao) {
            case 'A':
                meusDados();
                break;

            case 'B':
                try {
                    MenuPerguntas menuPerguntas = new MenuPerguntas(usuario);
                    menuPerguntas.menu();
                } catch (Exception e) {
                    System.out.println("\nErro ao acessar o menu de perguntas!");
                    e.printStackTrace();
                }
                break;

            case 'C':
                minhasRespostas();
                break;

            case 'D':
                minhasAvaliacoes();
                break;

            case 'R':
                break;

            default:
                System.out.println("Opção inválida!");
                break;
        }
    }

    private void minhasRespostas() {
        try {
            System.out.println("\nMinhas respostas");
            System.out.println("---------------");

            ArquivoResposta arqRespostas = new ArquivoResposta();
            ArquivoPergunta arqPerguntas = new ArquivoPergunta();

            // Não há índice de respostas por usuário: percorre o índice pergunta -> resposta
            ArrayList<ParPerguntaResposta> lista = arqRespostas.getIndicePerguntaResposta().read(null);
            ArrayList<Resposta> minhas = new ArrayList<>();

            for (ParPerguntaResposta par : lista) {
                Resposta r = arqRespostas.read(par.getIdResposta());
                if (r != null && r.ativa && r.getIdUsuario() == usuario.getId()) {
                    minhas.add(r);
                }
            }

            if (minhas.isEmpty()) {
                System.out.println("\nVocê não tem respostas ativas.");
                return;
            }

            int numero = 1;
            for (Resposta r : minhas) {
                Pergunta p = arqPerguntas.read(r.getIdPergunta());
                System.out.println("\n" + numero + " - Pergunta: " + (p != null ? p.pergunta : "Pergunta removida"));
                System.out.println("Resposta: " + r.texto);
                System.out.println("Nota: " + r.nota + " (" + r.avaliacoes + " avaliações)");
                numero++;
            }

            System.out.print("\nDigite o número da resposta que deseja arquivar (0 para retornar): ");
            int escolha = Integer.parseInt(console.nextLine());

            if (escolha == 0) return;

            if (escolha < 1 || escolha > minhas.size()) {
                System.out.println("Número inválido.");
                return;
            }

            Resposta selecionada = minhas.get(escolha - 1);

            System.out.println("\nResposta: " + selecionada.texto);
            System.out.print("Confirma o arquivamento desta resposta? (S/N): ");

            String resposta = console.nextLine().toUpperCase();
            char confirma = resposta.isEmpty() ? ' ' : resposta.charAt(0);

            if (confirma == 'S') {
                if (arqRespostas.arquivar(selecionada.getId())) {
                    System.out.println("\nResposta arquivada com sucesso!");
                } else {
                    System.out.println("\nNão foi possível arquivar a resposta.");
                }
            } else {
                System.out.println("\nArquivamento cancelado.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\nNúmero inválido! Digite apenas números.");
        } catch (Exception e) {
            System.out.println("\nErro ao listar suas respostas!");
            e.printStackTrace();
        }
    }

    private void minhasAvaliacoes() {
        try {
            System.out.println("\nMinhas avaliações");
            System.out.println("-----------------");

            ArquivoVoto arqVotos = new ArquivoVoto();
            ArquivoPergunta arqPerguntas = new ArquivoPergunta();
            ArquivoResposta arqRespostas = new ArquivoResposta();

            ArrayList<Voto> votos = arqVotos.getVotosPorUsuario(usuario.getId());

            if (votos.isEmpty()) {
                System.out.println("\nVocê ainda não avaliou nenhuma resposta.");
                return;
            }

            int numero = 1;
            for (Voto v : votos) {
                Resposta r = arqRespostas.read(v.idResposta);
                Pergunta p = (r != null) ? arqPerguntas.read(r.getIdPergunta()) : null;

                System.out.println("\n" + numero + " - Pergunta: " + (p != null ? p.pergunta : "Pergunta removida"));
                if (r == null) {
                    System.out.println("Resposta: Resposta removida");
                } else {
                    System.out.println("Resposta: " + r.texto + (r.ativa ? "" : " (arquivada)"));
                }
                System.out.println("Sua avaliação: " + (v.avaliacao ? "Positiva (+1)" : "Negativa (-1)"));
                numero++;
            }

            System.out.print("\nDigite o número da avaliação que deseja cancelar (0 para retornar): ");
            int escolha = Integer.parseInt(console.nextLine());

            if (escolha == 0) return;

            if (escolha < 1 || escolha > votos.size()) {
                System.out.println("Número inválido.");
                return;
            }

            Voto selecionado = votos.get(escolha - 1);

            System.out.print("Confirma o cancelamento desta avaliação? (S/N): ");

            String resposta = console.nextLine().toUpperCase();
            char confirma = resposta.isEmpty() ? ' ' : resposta.charAt(0);

            if (confirma == 'S') {
                // ArquivoVoto.delete também desfaz o efeito do voto na nota da resposta
                if (arqVotos.delete(selecionado.getId())) {
                    System.out.println("\nAvaliação cancelada com sucesso!");
                } else {
                    System.out.println("\nNão foi possível cancelar a avaliação.");
                }
            } else {
                System.out.println("\nA avaliação foi mantida.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\nNúmero inválido! Digite apenas números.");
        } catch (Exception e) {
            System.out.println("\nErro ao listar suas avaliações!");
            e.printStackTrace();
        }
    }

    private void meusDados() {
        char opcao;

        do {
            System.out.println("\nMeus dados");
            System.out.println("----------");
            System.out.println("Nome: " + usuario.nome);
            System.out.println("Email: " + usuario.email);

            System.out.println("\nA - Alterar nome");
            System.out.println("B - Alterar email");
            System.out.println("C - Alterar senha");
            System.out.println("D - Alterar pergunta e resposta de recuperação");
            System.out.println("R - Retornar");

            System.out.print("\nOpção: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);

            } catch (Exception e) {
                opcao = ' ';
            }

            switch (opcao) {
                case 'A':
                    alterarNome();
                    break;

                case 'B':
                    alterarEmail();
                    break;

                case 'C':
                    alterarSenha();
                    break;

                case 'D':
                    alterarRecuperacao();
                    break;

                case 'R':
                    break;

                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 'R');
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