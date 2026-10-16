package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import aed3.Registro;

public class Avaliacao implements Registro {

    public int idAvaliacao;
    public int idUsuario;
    public int idResposta;
    public boolean avaliacao;

    public Avaliacao() {
        this(-1, -1, -1, true);
    }

    public Avaliacao(int idUsuario, int idResposta, boolean avaliacao) {
        this(-1, idUsuario, idResposta, avaliacao);
    }

    public Avaliacao(int idAvaliacao, int idUsuario, int idResposta, boolean avaliacao) {
        this.idAvaliacao = idAvaliacao;
        this.idUsuario = idUsuario;
        this.idResposta = idResposta;
        this.avaliacao = avaliacao;
    }

    @Override
    public void setId(int id) {
        this.idAvaliacao = id;
    }

    @Override
    public int getId() {
        return this.idAvaliacao;
    }

    public int getIdUsuario() {
        return this.idUsuario;
    }

    public int getIdResposta() {
        return this.idResposta;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(this.idAvaliacao);
        dos.writeInt(this.idUsuario);
        dos.writeInt(this.idResposta);
        dos.writeBoolean(this.avaliacao);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.idAvaliacao = dis.readInt();
        this.idUsuario = dis.readInt();
        this.idResposta = dis.readInt();
        this.avaliacao = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "\nAvaliação: " + (this.avaliacao ? "Positiva" : "Negativa") +
                "\nResposta: " + this.idResposta +
                "\nUsuário: " + this.idUsuario;
    }
}
