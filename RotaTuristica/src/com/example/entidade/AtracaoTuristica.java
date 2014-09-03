package com.example.entidade;

public class AtracaoTuristica extends PontoLocalizavel{
    
    public AtracaoTuristica(){
        super();
    }
    
    private long tipoAtracao;

	public long getTipoAtracao() {
		return tipoAtracao;
	}

	public void setTipoAtracao(long tipoAtracao) {
		this.tipoAtracao = tipoAtracao;
	}
	
	
}