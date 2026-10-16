package indices;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ParUsuarioResposta implements aed3.InterfaceArvoreBMais<ParUsuarioResposta> {

    private int idUsuario;
    private int idResposta;

    public ParUsuarioResposta() {
        this.idUsuario = -1;
        this.idResposta = -1;
    }

    public ParUsuarioResposta(int idUsuario, int idResposta) {
        this.idUsuario = idUsuario;
        this.idResposta = idResposta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdResposta() {
        return idResposta;
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
        dos.writeInt(idResposta);

        return baos.toByteArray();
    }

    @Override
    public void deserialize(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba);
        DataInputStream dis = new DataInputStream(bais);

        idUsuario = dis.readInt();
        idResposta = dis.readInt();
    }

    @Override
    public int compareTo(ParUsuarioResposta outro) {
        if (this.idUsuario != outro.idUsuario) {
            return Integer.compare(this.idUsuario, outro.idUsuario);
        }

        if (this.idResposta == -1 || outro.idResposta == -1) {
            return 0;
        }

        return Integer.compare(this.idResposta, outro.idResposta);
    }

    @Override
    public ParUsuarioResposta clone() {
        return new ParUsuarioResposta(this.idUsuario, this.idResposta);
    }

    @Override
    public String toString() {
        return "(" + idUsuario + ";" + idResposta + ")";
    }
}
