package com.example.entidade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PontoLocalizavel extends ItemDescricao{
    
    public PontoLocalizavel(){
        super();
    }
    private double latitude;
    private double longitude;
    private Endereco endereco;
    private List<InfoDoResponsavel> infoResponsavel = new ArrayList<InfoDoResponsavel>();
    private Set<OrdemPontoRota> pontoRota = new HashSet<OrdemPontoRota>();
    
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public Endereco getEndereco() {
		return endereco;
	}
	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}
	public List<InfoDoResponsavel> getInfoResponsavel() {
		return infoResponsavel;
	}
	public void setInfoResponsavel(List<InfoDoResponsavel> infoResponsavel) {
		this.infoResponsavel = infoResponsavel;
	}
	public Set<OrdemPontoRota> getPontoRota() {
		return pontoRota;
	}
	public void setPontoRota(Set<OrdemPontoRota> pontoRota) {
		this.pontoRota = pontoRota;
	}
    
}
