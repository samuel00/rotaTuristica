package com.example.entidade;

import java.util.HashSet;
import java.util.Set;

public class RotaTuristica extends ItemDescricao{
    
    public RotaTuristica(){
        super();
    }
    private long tipo_rota;
    private Set<OrdemPontoRota> pontoRota = new HashSet<OrdemPontoRota>();
    
	public long getTipo_rota() {
		return tipo_rota;
	}
	public void setTipo_rota(long tipo_rota) {
		this.tipo_rota = tipo_rota;
	}
	public Set<OrdemPontoRota> getPontoRota() {
		return pontoRota;
	}
	public void setPontoRota(Set<OrdemPontoRota> pontoRota) {
		this.pontoRota = pontoRota;
	}
}
