package arquivos;

import java.util.ArrayList;
import aed3.Arquivo;
import aed3.ArvoreBMais;
import entidades.Avaliacao;
import entidades.Resposta;
import indices.ParRespostaAvaliacao;
import indices.ParUsuarioAvaliacao;

public class ArquivoAvaliacao extends Arquivo<Avaliacao> {
    private ArvoreBMais<ParUsuarioAvaliacao> indiceUsuarioAvaliacao;
    private ArvoreBMais<ParRespostaAvaliacao> indiceRespostaAvaliacao;
    private ArquivoResposta arqRespostas;

    public ArquivoAvaliacao(ArquivoResposta arqRespostas) throws Exception {
        super("avaliacoes", Avaliacao.class.getConstructor());

        indiceUsuarioAvaliacao = new ArvoreBMais<>(
                ParUsuarioAvaliacao.class.getConstructor(),
                5,
                "dados/avaliacoes/indiceUsuarioAvaliacao.db");

        indiceRespostaAvaliacao = new ArvoreBMais<>(
                ParRespostaAvaliacao.class.getConstructor(),
                5,
                "dados/avaliacoes/indiceRespostaAvaliacao.db");

        this.arqRespostas = arqRespostas;
    }

    @Override
    public int create(Avaliacao a) throws Exception {
        ArrayList<ParUsuarioAvaliacao> avaliacoesUsuario = indiceUsuarioAvaliacao.read(new ParUsuarioAvaliacao(a.getIdUsuario(), -1));

        for (ParUsuarioAvaliacao par : avaliacoesUsuario) {
            Avaliacao existente = super.read(par.getIdAvaliacao());
            if (existente != null && existente.getIdResposta() == a.getIdResposta()) {
                throw new Exception("O usuário já avaliou esta resposta.");
            }
        }

        int id = super.create(a);
        a.setId(id);
        indiceUsuarioAvaliacao.create(new ParUsuarioAvaliacao(a.getIdUsuario(), id));
        indiceRespostaAvaliacao.create(new ParRespostaAvaliacao(a.getIdResposta(), id));

        Resposta r = arqRespostas.read(a.getIdResposta());
        if (r != null) {
            r.avaliacoes++;
            if (a.avaliacao) {
                r.nota++;
            } else {
                r.nota--;
            }
            arqRespostas.update(r);
        }

        return id;
    }

    @Override
    public boolean delete(int idAvaliacao) throws Exception {
        Avaliacao a = super.read(idAvaliacao);
        if (a != null) {
            Resposta r = arqRespostas.read(a.getIdResposta());
            if (r != null) {
                r.avaliacoes--;
                if (a.avaliacao) {
                    r.nota--;
                } else {
                    r.nota++;
                }
                arqRespostas.update(r);
            }

            indiceUsuarioAvaliacao.delete(new ParUsuarioAvaliacao(a.getIdUsuario(), a.getId()));
            indiceRespostaAvaliacao.delete(new ParRespostaAvaliacao(a.getIdResposta(), a.getId()));
            return super.delete(idAvaliacao);
        }
        return false;
    }

    public ArrayList<Avaliacao> getAvaliacoesPorUsuario(int idUsuario) throws Exception {
        ArrayList<Avaliacao> lista = new ArrayList<>();
        ArrayList<ParUsuarioAvaliacao> pares = indiceUsuarioAvaliacao.read(new ParUsuarioAvaliacao(idUsuario, -1));

        for (ParUsuarioAvaliacao par : pares) {
            Avaliacao a = super.read(par.getIdAvaliacao());
            if (a != null) {
                lista.add(a);
            }
        }

        return lista;
    }

    public ArrayList<Avaliacao> getAvaliacoesPorResposta(int idResposta) throws Exception {
        ArrayList<Avaliacao> lista = new ArrayList<>();
        ArrayList<ParRespostaAvaliacao> pares = indiceRespostaAvaliacao.read(new ParRespostaAvaliacao(idResposta, -1));

        for (ParRespostaAvaliacao par : pares) {
            Avaliacao a = super.read(par.getIdAvaliacao());
            if (a != null) {
                lista.add(a);
            }
        }

        return lista;
    }
}
