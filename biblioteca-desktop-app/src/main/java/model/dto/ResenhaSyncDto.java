package model.dto;

import model.Resenha;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ResenhaSyncDto {
    private String texto;
    private String dataAvaliacao;
    private String nomeUsuario;
    private int nota;

    private ResenhaSyncDto() {}

    public static ResenhaSyncDto fromResenha(Resenha resenha) {
        ResenhaSyncDto dto = new ResenhaSyncDto();
        dto.texto = resenha.getTexto();
        dto.nota = resenha.getNota();
        if (resenha.getUsuario() != null) {
            dto.nomeUsuario = resenha.getUsuario().getNome();
        }
        if (resenha.getDtAvaliacao() != null) {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            dto.dataAvaliacao = isoFormat.format(resenha.getDtAvaliacao());
        }
        return dto;
    }

    // Getters
    public String getTexto() { return texto; }
    public String getDataAvaliacao() { return dataAvaliacao; }
    public String getNomeUsuario() { return nomeUsuario; }
    public int getNota() { return nota; }
}