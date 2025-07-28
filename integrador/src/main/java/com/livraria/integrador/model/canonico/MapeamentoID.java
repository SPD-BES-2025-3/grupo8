package com.livraria.integrador.model.canonico;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

@DatabaseTable(tableName = "mapeamento_id")
public class MapeamentoID {
    @DatabaseField(id = true)
    private String idCanonico;

    @DatabaseField(canBeNull = false, index = true, unique = true)
    private Integer idDesktop;

    @DatabaseField(index = true, unique = true)
    private String idApi;

    @DatabaseField
    private Date ultimaAtualizacao;

    // Getters e Setters
    public String getIdCanonico() { return idCanonico; }
    public void setIdCanonico(String idCanonico) { this.idCanonico = idCanonico; }
    public Integer getIdDesktop() { return idDesktop; }
    public void setIdDesktop(Integer idDesktop) { this.idDesktop = idDesktop; }
    public String getIdApi() { return idApi; }
    public void setIdApi(String idApi) { this.idApi = idApi; }
    public Date getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(Date ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
}