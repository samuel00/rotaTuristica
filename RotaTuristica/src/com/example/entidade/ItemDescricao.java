package com.example.entidade;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemDescricao {

	private Long id;
	private String nome;
	private long idVersao;
	private Date dataAtualizacao;
	private long municipio_id;
	private List<Multimidia> multimida = new ArrayList<Multimidia>();
	private List<Descricao> descricao = new ArrayList<Descricao>();
	
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
	public long getIdVersao() {
		return idVersao;
	}
	public void setIdVersao(long idVersao) {
		this.idVersao = idVersao;
	}
	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}
	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}
	public long getMunicipio_id() {
		return municipio_id;
	}
	public void setMunicipio_id(long municipio_id) {
		this.municipio_id = municipio_id;
	}
	public List<Multimidia> getMultimida() {
		return multimida;
	}
	public void setMultimida(List<Multimidia> multimida) {
		this.multimida = multimida;
	}
	public List<Descricao> getDescricao() {
		return descricao;
	}
	public void setDescricao(List<Descricao> descricao) {
		this.descricao = descricao;
	}	

}
