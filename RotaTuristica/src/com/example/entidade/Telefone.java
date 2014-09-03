package com.example.entidade;

public class Telefone {
	
	private Long id;
    private String codArea;
    private String numero;
    private InfoDoResponsavel responsavel;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodArea() {
		return codArea;
	}
	public void setCodArea(String codArea) {
		this.codArea = codArea;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public InfoDoResponsavel getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(InfoDoResponsavel responsavel) {
		this.responsavel = responsavel;
	}
    
    

}
