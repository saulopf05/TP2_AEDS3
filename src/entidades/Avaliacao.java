package entidades;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import aed3.Registro;

public class Voto implements Registro {

    public int idVoto;
    public int idUsuario;
    public int idResposta;
    public boolean avaliacao;

    public Voto() {
        this(-1, -1, -1, true);
    }

    public Voto(int idUsuario, int idResposta, boolean avaliacao) {
        this(-1, idUsuario, idResposta, avaliacao);
    }

    public Voto(int idVoto, int idUsuario, int idResposta, boolean avaliacao) {
        this.idVoto = idVoto;
        this.idUsuario = idUsuario;
        this.idResposta = idResposta;
        this.avaliacao = avaliacao;
    }

    @Override
    public void setId(int id) {
        this.idVoto = id;
    }

    @Override
    public int getId() {
        return this.idVoto;
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

        dos.writeInt(this.idVoto);
        dos.writeInt(this.idUsuario);
        dos.writeInt(this.idResposta);
        dos.writeBoolean(this.avaliacao);

        return baos.toByteArray();
    }

    @Override
    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);

        this.idVoto = dis.readInt();
        this.idUsuario = dis.readInt();
        this.idResposta = dis.readInt();
        this.avaliacao = dis.readBoolean();
    }

    @Override
    public String toString() {
        return "\nVoto: " + (this.avaliacao ? "Positivo" : "Negativo") +
                "\nResposta: " + this.idResposta +
                "\nUsuário: " + this.idUsuario;
    }
}