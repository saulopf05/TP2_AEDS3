package arquivos;

import java.util.ArrayList;
import aed3.ArvoreBMais;
import entidades.Resposta;
import indices.ParPerguntaResposta;

public class ArquivoResposta extends aed3.Arquivo<Resposta> {
    private ArvoreBMais<ParPerguntaResposta> indicePerguntaResposta;

    public ArquivoResposta() throws Exception {
        super("respostas", Resposta.class.getConstructor());

        indicePerguntaResposta = new ArvoreBMais<>(
                ParPerguntaResposta.class.getConstructor(),
                5,
                "dados/respostas/indicePerguntaResposta.db");
    }

    public ArvoreBMais<ParPerguntaResposta> getIndicePerguntaResposta() {
        return indicePerguntaResposta;
    }

    @Override
    public int create(Resposta r) throws Exception {
        int id = super.create(r);
        r.setId(id);
        indicePerguntaResposta.create(new ParPerguntaResposta(r.getIdPergunta(), id));
        return id;
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

    public boolean arquivar(int idResposta) throws Exception {
        Resposta r = super.read(idResposta);
        if (r != null) {
            r.ativa = false;
            return super.update(r);
        }
        return false;
    }
}