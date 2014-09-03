package com.example.entidade;

import java.util.ArrayList;
import java.util.List;

public class InfoDoResponsavel {
	
	private Long id;
    private String nome;
    private String email;
    private String facebook;
    private String instagran;
    private String webSite;
    private PontoLocalizavel pontoLocalizavel;
    private List<Telefone> telefone = new ArrayList<Telefone>();
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFacebook() {
		return facebook;
	}
	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}
	public String getInstagran() {
		return instagran;
	}
	public void setInstagran(String instagran) {
		this.instagran = instagran;
	}
	public String getWebSite() {
		return webSite;
	}
	public void setWebSite(String webSite) {
		this.webSite = webSite;
	}
	public PontoLocalizavel getPontoLocalizavel() {
		return pontoLocalizavel;
	}
	public void setPontoLocalizavel(PontoLocalizavel pontoLocalizavel) {
		this.pontoLocalizavel = pontoLocalizavel;
	}
	public List<Telefone> getTelefone() {
		return telefone;
	}
	public void setTelefone(List<Telefone> telefone) {
		this.telefone = telefone;
	}


}
