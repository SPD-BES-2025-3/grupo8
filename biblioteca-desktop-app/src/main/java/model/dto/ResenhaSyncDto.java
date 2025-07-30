package model.dto;

import model.Resenha;
import java.time.format.DateTimeFormatter;


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
            // CORREÇÃO: Use o novo DateTimeFormatter do Java 8+ para consistência
            DateTimeFormatter isoFormat = DateTimeFormatter.ISO_INSTANT;
            dto.dataAvaliacao = isoFormat.format(resenha.getDtAvaliacao().toInstant());
        }
        return dto;
    }

    // Getters
    public String getTexto() { return texto; }
    public String getDataAvaliacao() { return dataAvaliacao; }
    public String getNomeUsuario() { return nomeUsuario; }
    public int getNota() { return nota; }
}