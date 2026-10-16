package arquivos;

import java.util.ArrayList;
import aed3.ArvoreBMais;
import entidades.Resposta;
import indices.ParPerguntaResposta;
import indices.ParUsuarioResposta;

public class ArquivoResposta extends aed3.Arquivo<Resposta> {
    private ArvoreBMais<ParPerguntaResposta> indicePerguntaResposta;
    private ArvoreBMais<ParUsuarioResposta> indiceUsuarioResposta;

    public ArquivoResposta() throws Exception {
        super("respostas", Resposta.class.getConstructor());

        indicePerguntaResposta = new ArvoreBMais<>(
                ParPerguntaResposta.class.getConstructor(),
                5,
                "dados/respostas/indicePerguntaResposta.db");

        indiceUsuarioResposta = new ArvoreBMais<>(
                ParUsuarioResposta.class.getConstructor(),
                5,
                "dados/respostas/indiceUsuarioResposta.db");
    }

    public ArvoreBMais<ParPerguntaResposta> getIndicePerguntaResposta() {
        return indicePerguntaResposta;
    }

    public ArvoreBMais<ParUsuarioResposta> getIndiceUsuarioResposta() {
        return indiceUsuarioResposta;
    }

    @Override
    public int create(Resposta r) throws Exception {
        int id = super.create(r);
        r.setId(id);
        indicePerguntaResposta.create(new ParPerguntaResposta(r.getIdPergunta(), id));
        indiceUsuarioResposta.create(new ParUsuarioResposta(r.getIdUsuario(), id));
        return id;
    }

    // O sistema arquiva respostas em vez de excluí-las; se uma exclusão
    // acontecer, as duas árvores do relacionamento são mantidas em dia.
    @Override
    public boolean delete(int idResposta) throws Exception {
        Resposta r = super.read(idResposta);
        if (r != null && super.delete(idResposta)) {
            indicePerguntaResposta.delete(new ParPerguntaResposta(r.getIdPergunta(), idResposta));
            indiceUsuarioResposta.delete(new ParUsuarioResposta(r.getIdUsuario(), idResposta));
            return true;
        }
        return false;
    }

    public ArrayList<Resposta> getRespostasPorPergunta(int idPergunta) throws Exception {
        ArrayList<Resposta> respostasDaPergunta = new ArrayList<>();

        ArrayList<ParPerguntaResposta> pares = indicePerguntaResposta.read(new ParPerguntaResposta(idPergunta, -1));

        for (ParPerguntaResposta par : pares) {
            Resposta r = super.read(par.getIdResposta());
            if (r != null && r.ativa) {
                respostasDaPergunta.add(r);
            }
        }

        respostasDaPergunta.sort((r1, r2) -> Short.compare(r2.nota, r1.nota));

        return respostasDaPergunta;
    }

    public ArrayList<Resposta> getRespostasPorUsuario(int idUsuario) throws Exception {
        ArrayList<Resposta> respostasDoUsuario = new ArrayList<>();

        ArrayList<ParUsuarioResposta> pares = indiceUsuarioResposta.read(new ParUsuarioResposta(idUsuario, -1));

        for (ParUsuarioResposta par : pares) {
            Resposta r = super.read(par.getIdResposta());
            if (r != null && r.ativa) {
                respostasDoUsuario.add(r);
            }
        }

        return respostasDoUsuario;
    }

    public boolean arquivar(int idResposta) throws Exception {
        Resposta r = super.read(idResposta);
        if (r != null) {
            r.ativa = false;
            return super.update(r);
        }
        return false;
    }
}
