package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParUsuarioAvaliacao implements aed3.InterfaceArvoreBMais<ParUsuarioAvaliacao> {

    private int idUsuario;
    private int idAvaliacao;

    public ParUsuarioAvaliacao() {
        this.idUsuario = -1;
        this.idAvaliacao = -1;
    }

    public ParUsuarioAvaliacao(int idUsuario, int idAvaliacao) {
        this.idUsuario = idUsuario;
        this.idAvaliacao = idAvaliacao;
    }

    public int getIdUsuario() {
        return idUsuario;
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

        dos.writeInt(idUsuario);
        dos.writeInt(idAvaliacao);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idUsuario = dis.readInt();
        idAvaliacao = dis.readInt();
    }

    @Override
    public int compareTo(ParUsuarioAvaliacao outro) {
        if (this.idUsuario != outro.idUsuario) {
            return Integer.compare(this.idUsuario, outro.idUsuario);
        }

        if (this.idAvaliacao == -1 || outro.idAvaliacao == -1) {
            return 0;
        }

        return Integer.compare(this.idAvaliacao, outro.idAvaliacao);
    }

    @Override
    public ParUsuarioAvaliacao clone() {
        return new ParUsuarioAvaliacao(this.idUsuario, this.idAvaliacao);
    }

    @Override
    public String toString() {
        return "(" + idUsuario + ";" + idAvaliacao + ")";
    }
}
