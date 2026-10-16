package menus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import arquivos.ArquivoAvaliacao;
import arquivos.ArquivoResposta;
import arquivos.ArquivoUsuario;
import entidades.Avaliacao;
import entidades.Pergunta;
import entidades.Resposta;
import entidades.Usuario;

/**
 * Telas de pergunta, de resposta e de avaliação.
 *
 * As mesmas telas aparecem em caminhos diferentes (busca de perguntas e
 * área do usuário); por isso cada método recebe o breadcrumb da tela.
 */
public class MenuResposta {
    private static Scanner console = Console.entrada();
    private Usuario usuario;
    private ArquivoUsuario arqUsuarios;
    private ArquivoResposta arqRespostas;
    private ArquivoAvaliacao arqAvaliacoes;

    public MenuResposta(Usuario usuario, ArquivoUsuario arqUsuarios,
            ArquivoResposta arqRespostas, ArquivoAvaliacao arqAvaliacoes) {
        this.usuario = usuario;
        this.arqUsuarios = arqUsuarios;
        this.arqRespostas = arqRespostas;
        this.arqAvaliacoes = arqAvaliacoes;
    }

    /** Pergunta aberta pela busca: mostra as respostas e permite responder ou abrir uma resposta. */
    public void abrirPergunta(Pergunta pergunta) {
        String caminho = "Início > Buscar perguntas > Perguntas mais recentes > Pergunta";
        String opcao;

        try {
            do {
                Console.cabecalho(caminho);
                exibirPergunta(pergunta);
                ArrayList<Resposta> respostas = exibirRespostas(pergunta);

                System.out.println("\n(A) Responder à pergunta");
                System.out.println("\n(R) Retornar");

                opcao = Console.lerOpcao();
                int item = Console.numeroDoItem(opcao, respostas.size());

                if (item > 0) {
                    abrirResposta(pergunta, respostas.get(item - 1), caminho + " > Resposta", false);
                } else if (opcao.equals("A")) {
                    responder(pergunta, caminho + " > Responder");
                } else if (!opcao.equals("R")) {
                    System.out.println("\nOpção inválida!");
                }
            } while (!opcao.equals("R"));

        } catch (Exception e) {
            System.out.println("\nErro ao exibir a pergunta!");
            e.printStackTrace();
        }
    }

    /** Pergunta como aparece no topo das telas de pergunta, resposta e avaliação. */
    void exibirPergunta(Pergunta pergunta) throws Exception {
        System.out.println("\n" + pergunta.pergunta);
        System.out.println("Criada por " + nomeDoAutor(pergunta.getIdUsuario()) +
                " em " + Formato.dataHoraExtenso(pergunta.criacao));
    }

    /**
     * Bloco RESPOSTAS: respostas ativas da pergunta, da maior para a menor nota.
     * Devolve as respostas na ordem em que foram numeradas na tela.
     */
    ArrayList<Resposta> exibirRespostas(Pergunta pergunta) throws Exception {
        ArrayList<Resposta> respostas = arqRespostas.getRespostasPorPergunta(pergunta.getId());

        System.out.println("\nRESPOSTAS");
        System.out.println("---------");

        if (respostas.isEmpty()) {
            System.out.println("Ainda não há respostas para esta pergunta.");
        }

        for (int i = 0; i < respostas.size(); i++) {
            if (i > 0) System.out.println();
            System.out.print("(" + (i + 1) + ") ");
            exibirResposta(respostas.get(i));
        }

        return respostas;
    }

    private void exibirResposta(Resposta resposta) throws Exception {
        System.out.println(resposta.resposta);
        System.out.println("Respondido por " + nomeDoAutor(resposta.getIdUsuario()) +
                " em " + Formato.dataHoraExtenso(resposta.criacao));
        System.out.println("Nota: " + resposta.nota + "  (" + Formato.avaliacoes(resposta.avaliacoes) + ")");
        if (!resposta.ativa) {
            System.out.println("Esta resposta foi arquivada.");
        }
    }

    private void responder(Pergunta pergunta, String caminho) throws Exception {
        Console.cabecalho(caminho);
        exibirPergunta(pergunta);

        if (!pergunta.ativa) {
            System.out.println("\nEsta pergunta foi arquivada e não pode receber respostas.");
            return;
        }

        System.out.print("\nResposta: ");
        String texto = console.nextLine().trim();

        if (texto.isEmpty()) {
            System.out.println("\nNenhuma resposta foi registrada.");
            return;
        }

        arqRespostas.create(new Resposta(pergunta.getId(), usuario.getId(), texto));
        System.out.println("\nResposta registrada com sucesso!");
    }

    /**
     * Tela de uma resposta. Quem escreveu a resposta, ou o autor da pergunta
     * dentro da área do usuário, só pode arquivá-la; os demais usuários podem
     * concordar ou discordar dela.
     */
    void abrirResposta(Pergunta pergunta, Resposta resposta, String caminho,
            boolean autorDaPergunta) throws Exception {
        boolean podeArquivar = autorDaPergunta || resposta.getIdUsuario() == usuario.getId();

        while (true) {
            Console.cabecalho(caminho);
            exibirPergunta(pergunta);

            System.out.println("\nRESPOSTA");
            System.out.println("--------\n");
            exibirResposta(resposta);

            if (podeArquivar) {
                System.out.println("\n(A) Arquivar resposta");
            } else {
                System.out.println("\n(A) Concordar com a resposta");
                System.out.println("(B) Discordar da resposta");
            }
            System.out.println("\n(R) Retornar ao menu anterior");

            String opcao = Console.lerOpcao();

            if (opcao.equals("R")) {
                return;
            }
            if (opcao.equals("A")) {
                if (podeArquivar) {
                    arquivar(resposta);
                } else {
                    avaliar(resposta, true);
                }
                return;
            }
            if (opcao.equals("B") && !podeArquivar) {
                avaliar(resposta, false);
                return;
            }
            System.out.println("\nOpção inválida!");
        }
    }

    private void arquivar(Resposta resposta) throws Exception {
        System.out.print("\nConfirma o arquivamento desta resposta? (S/N): ");
        String confirma = console.nextLine().trim().toUpperCase();

        if (!confirma.startsWith("S")) {
            System.out.println("\nArquivamento cancelado.");
            return;
        }

        if (arqRespostas.arquivar(resposta.getId())) {
            System.out.println("\nResposta arquivada com sucesso!");
        } else {
            System.out.println("\nNão foi possível arquivar a resposta.");
        }
    }

    private void avaliar(Resposta resposta, boolean positiva) throws Exception {
        // A nota e a quantidade de avaliações da resposta são atualizadas pelo ArquivoAvaliacao
        Avaliacao avaliacao = new Avaliacao(usuario.getId(), resposta.getId(), positiva);
        try {
            arqAvaliacoes.create(avaliacao);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            // ArquivoAvaliacao.create recusa uma segunda avaliação do mesmo usuário na mesma resposta
            System.out.println("\nVocê já avaliou esta resposta.");
            System.out.println("Para mudar sua avaliação, cancele a anterior em Minha área > Minhas avaliações.");
            return;
        }

        System.out.println(positiva ? "\nVocê concordou com a resposta." : "\nVocê discordou da resposta.");
    }

    /** Tela de uma avaliação feita pelo usuário; a única operação é cancelá-la. */
    void abrirAvaliacao(Avaliacao avaliacao, Pergunta pergunta, Resposta resposta) throws Exception {
        while (true) {
            Console.cabecalho("Início > Minha área > Minhas avaliações > Avaliação");
            exibirPergunta(pergunta);

            System.out.println("\nRESPOSTA");
            System.out.println("--------\n");
            exibirResposta(resposta);

            System.out.println("\nSua avaliação: " + (avaliacao.avaliacao
                    ? "Positiva (você concordou com a resposta)"
                    : "Negativa (você discordou da resposta)"));

            System.out.println("\n(A) Cancelar avaliação");
            System.out.println("\n(R) Retornar ao menu anterior");

            String opcao = Console.lerOpcao();

            if (opcao.equals("R")) {
                return;
            }
            if (opcao.equals("A")) {
                System.out.print("\nConfirma o cancelamento desta avaliação? (S/N): ");
                String confirma = console.nextLine().trim().toUpperCase();

                if (!confirma.startsWith("S")) {
                    System.out.println("\nA avaliação foi mantida.");
                } else if (arqAvaliacoes.delete(avaliacao.getId())) {
                    // ArquivoAvaliacao.delete também desfaz o efeito da avaliação na nota da resposta
                    System.out.println("\nAvaliação cancelada com sucesso!");
                } else {
                    System.out.println("\nNão foi possível cancelar a avaliação.");
                }
                return;
            }
            System.out.println("\nOpção inválida!");
        }
    }

    /** Nome exibido como autor; o próprio usuário aparece como "você". */
    String nomeDoAutor(int idUsuario) throws Exception {
        if (idUsuario == usuario.getId()) {
            return "você";
        }
        Usuario autor = arqUsuarios.read(idUsuario);
        return autor != null ? autor.nome : "usuário desconhecido";
    }
}
