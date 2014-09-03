package com.example.entidade;

public class ServicoTuristico extends PontoLocalizavel{
    
    public ServicoTuristico(){
        super();
    }
    
    private long tipoServico;

	public long getTipoServico() {
		return tipoServico;
	}

	public void setTipoServico(long tipoServico) {
		this.tipoServico = tipoServico;
	}
	
	
	
}