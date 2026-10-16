package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParRespostaAvaliacao implements aed3.InterfaceArvoreBMais<ParRespostaAvaliacao> {

    private int idResposta;
    private int idAvaliacao;

    public ParRespostaAvaliacao() {
        this.idResposta = -1;
        this.idAvaliacao = -1;
    }

    public ParRespostaAvaliacao(int idResposta, int idAvaliacao) {
        this.idResposta = idResposta;
        this.idAvaliacao = idAvaliacao;
    }

    public int getIdResposta() {
        return idResposta;
    }

    public int getIdAvaliacao() {
        return idAvaliacao;
    }

    @Override
    public short size() {
        return 8;
    }

    @Override
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(idResposta);
        dos.writeInt(idAvaliacao);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idResposta = dis.readInt();
        idAvaliacao = dis.readInt();
    }

    @Override
    public int compareTo(ParRespostaAvaliacao outro) {
        if (this.idResposta != outro.idResposta) {
            return Integer.compare(this.idResposta, outro.idResposta);
        }

        if (this.idAvaliacao == -1 || outro.idAvaliacao == -1) {
            return 0;
        }

        return Integer.compare(this.idAvaliacao, outro.idAvaliacao);
    }

    @Override
    public ParRespostaAvaliacao clone() {
        return new ParRespostaAvaliacao(this.idResposta, this.idAvaliacao);
    }

    @Override
    public String toString() {
        return "(" + idResposta + ";" + idAvaliacao + ")";
    }
}
