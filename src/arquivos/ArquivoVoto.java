package arquivos;

import java.util.ArrayList;
import aed3.Arquivo;
import aed3.ArvoreBMais;
import entidades.Resposta;
import entidades.Voto;
import indices.ParUsuarioVoto;

public class ArquivoVoto extends Arquivo<Voto> {
    private ArvoreBMais<ParUsuarioVoto> indiceUsuarioVoto;
    private ArquivoResposta arqRespostas;

    public ArquivoVoto() throws Exception {
        super("votos", Voto.class.getConstructor());
        
        indiceUsuarioVoto = new ArvoreBMais<>(
                ParUsuarioVoto.class.getConstructor(),
                5,
                "dados/votos/indiceUsuarioVoto.db");
                
        arqRespostas = new ArquivoResposta();
    }

    @Override
    public int create(Voto v) throws Exception {
        ArrayList<ParUsuarioVoto> votosUsuario = indiceUsuarioVoto.read(new ParUsuarioVoto(v.getIdUsuario(), -1));
        
        for (ParUsuarioVoto par : votosUsuario) {
            Voto votoExistente = super.read(par.getIdVoto());
            if (votoExistente != null && votoExistente.getIdResposta() == v.getIdResposta()) {
                throw new Exception("O usuario ja avaliou esta resposta.");
            }
        }

        int id = super.create(v);
        v.setId(id);
        indiceUsuarioVoto.create(new ParUsuarioVoto(v.getIdUsuario(), id));

        Resposta r = arqRespostas.read(v.getIdResposta());
        if (r != null) {
            r.avaliacoes++;
            if (v.avaliacao) {
                r.nota++;
            } else {
                r.nota--;
            }
            arqRespostas.update(r);
        }

        return id;
    }

    @Override
    public boolean delete(int idVoto) throws Exception {
        Voto v = super.read(idVoto);
        if (v != null) {
            Resposta r = arqRespostas.read(v.getIdResposta());
            if (r != null) {
                r.avaliacoes--;
                if (v.avaliacao) {
                    r.nota--;
                } else {
                    r.nota++;
                }
                arqRespostas.update(r);
            }
            
            indiceUsuarioVoto.delete(new ParUsuarioVoto(v.getIdUsuario(), v.getId()));
            return super.delete(idVoto);
        }
        return false;
    }

    public ArrayList<Voto> getVotosPorUsuario(int idUsuario) throws Exception {
        ArrayList<Voto> lista = new ArrayList<>();
        ArrayList<ParUsuarioVoto> pares = indiceUsuarioVoto.read(new ParUsuarioVoto(idUsuario, -1));
        
        for (ParUsuarioVoto par : pares) {
            Voto v = super.read(par.getIdVoto());
            if (v != null) {
                lista.add(v);
            }
        }
        
        return lista;
    }
}