package com.example.entidade;

public class Descricao {
	private long id;
	private long item_descricao;
	private String descricao;
	private long idioma;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getItem_descricao() {
		return item_descricao;
	}
	public void setItem_descricao(long item_descricao) {
		this.item_descricao = item_descricao;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public long getIdioma() {
		return idioma;
	}
	public void setIdioma(long idioma) {
		this.idioma = idioma;
	}
}
