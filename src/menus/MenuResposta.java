package menus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import arquivos.ArquivoResposta;
import arquivos.ArquivoUsuario;
import arquivos.ArquivoVoto;
import entidades.Pergunta;
import entidades.Resposta;
import entidades.Usuario;
import entidades.Voto;

public class MenuResposta {
    private static Scanner console = Console.entrada();
    private Pergunta pergunta;
    private Usuario usuario;
    private ArquivoResposta arqRespostas;
    private ArquivoUsuario arqUsuarios;
    private ArquivoVoto arqVotos;

    public MenuResposta(Pergunta pergunta, Usuario usuario) throws Exception {
        this.pergunta = pergunta;
        this.usuario = usuario;
        this.arqRespostas = new ArquivoResposta();
        this.arqUsuarios = new ArquivoUsuario();
        this.arqVotos = new ArquivoVoto();
    }

    public void menu() {
        char opcao;

        if (!pergunta.ativa) {
            System.out.println("\nEsta pergunta foi arquivada e não pode receber mais respostas.");
            return;
        }

        do {
            System.out.println("\nPergunta selecionada");
            System.out.println("--------------------");
            System.out.println(pergunta.pergunta);
            System.out.println("\nA - Listar respostas");
            System.out.println("B - Responder");
            System.out.println("C - Votar na resposta");
            System.out.println("R - Retornar");
            System.out.print("\nOpção: ");

            try {
                opcao = console.nextLine().toUpperCase().charAt(0);
            } catch (Exception e) {
                opcao = ' ';
            }

            switch (opcao) {
                case 'A':
                    listar();
                    break;
                case 'B':
                    incluir();
                    break;
                case 'C':
                    votarResposta();
                    break;
                case 'R':
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 'R');
    }

    private void listar() {
        try {
            System.out.println("\nRespostas ordenadas pela nota (maior primeiro)");
            System.out.println("---------------------------------------------");
            ArrayList<Resposta> respostas = arqRespostas.getRespostasPorPergunta(pergunta.getId());
            if (respostas.isEmpty()) {
                System.out.println("\nAinda não há respostas para esta pergunta.");
                return;
            }
            int numero = 1;
            for (Resposta r : respostas) {
                Usuario autor = arqUsuarios.read(r.getIdUsuario());
                System.out.println("\n" + numero + " - " + r.texto);
                System.out.println("Autor: " + (autor != null ? autor.nome : "Desconhecido"));
                System.out.println("Data: " + Formato.dataHora(r.criacao));
                System.out.println("Nota: " + r.nota);
                System.out.println("Quantidade de avaliações: " + r.avaliacoes);
                numero++;
            }
        } catch (Exception e) {
            System.out.println("\nErro ao listar respostas!");
            e.printStackTrace();
        }
    }

    private void incluir() {
        try {
            if (!pergunta.ativa) {
                System.out.println("\nEsta pergunta foi arquivada e não pode receber respostas.");
                return;
            }

            System.out.println("\nResponder pergunta");
            System.out.println("-----------------");
            System.out.print("Texto da resposta (vazio para cancelar): ");
            String texto = console.nextLine();

            if (texto.isEmpty()) {
                return;
            }

            Resposta resposta = new Resposta(
                    pergunta.getId(),
                    usuario.getId(),
                    texto);

            arqRespostas.create(resposta);
            System.out.println("\nResposta cadastrada com sucesso!");

        } catch (Exception e) {
            System.out.println("\nErro ao responder pergunta!");
            e.printStackTrace();
        }
    }

    private void votarResposta() {
        try {
            ArrayList<Resposta> respostas = arqRespostas.getRespostasPorPergunta(pergunta.getId());

            if (respostas.isEmpty()) {
                System.out.println("\nAinda não há respostas para esta pergunta.");
                return;
            }

            System.out.println("\nVotar em resposta");
            System.out.println("-----------------");

            int numero = 1;
            for (Resposta r : respostas) {
                Usuario autor = arqUsuarios.read(r.getIdUsuario());
                System.out.println("\n" + numero + " - " + r.texto);
                System.out.println("Autor: " + (autor != null ? autor.nome : "Desconhecido"));
                System.out.println("Nota atual: " + r.nota);
                numero++;
            }

            System.out.print("\nDigite o número da resposta para votar (0 para cancelar): ");
            int escolha = Integer.parseInt(console.nextLine());

            if (escolha == 0) {
                return;
            }

            if (escolha < 1 || escolha > respostas.size()) {
                System.out.println("\nNúmero inválido.");
                return;
            }

            Resposta respostaSelecionada = respostas.get(escolha - 1);

            if (respostaSelecionada.getIdUsuario() == usuario.getId()) {
                System.out.println("\nVocê não pode votar na própria resposta.");
                return;
            }

            System.out.print("Valor do voto (+1 ou -1): ");
            String entrada = console.nextLine().trim();
            boolean avaliacao;

            if (entrada.equals("+1") || entrada.equals("1")) {
                avaliacao = true;
            } else if (entrada.equals("-1")) {
                avaliacao = false;
            } else {
                System.out.println("\nValor inválido. Use apenas +1 ou -1.");
                return;
            }

            // A nota e a quantidade de avaliações da resposta são atualizadas pelo ArquivoVoto
            Voto voto = new Voto(usuario.getId(), respostaSelecionada.getId(), avaliacao);
            try {
                arqVotos.create(voto);
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                // ArquivoVoto.create recusa uma segunda avaliação do mesmo usuário na mesma resposta
                System.out.println("\nVocê já avaliou esta resposta.");
                System.out.println("Para mudar sua avaliação, cancele a anterior em Minha área > Minhas avaliações.");
                return;
            }

            System.out.println("\nVoto registrado com sucesso!");

        } catch (NumberFormatException e) {
            System.out.println("\nNúmero inválido. Digite apenas números.");
        } catch (Exception e) {
            System.out.println("\nErro ao votar na resposta!");
            e.printStackTrace();
        }
    }
}
