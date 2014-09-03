package com.example.entidade;

public class OrdemPontoRota {
	
	private Long id;
	private Long pontoLocalizavelId;
    private PontoLocalizavel pontoLocalizavel;
    private RotaTuristica rotaTuristica;
    private Long rotaTuristicaId;
	private int posicao;
	
    public Long getRotaTuristicaId() {
		return rotaTuristicaId;
	}
	public void setRotaTuristicaId(Long rotaTuristicaId) {
		this.rotaTuristicaId = rotaTuristicaId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPontoLocalizavelId() {
		return pontoLocalizavelId;
	}
	public void setPontoLocalizavelId(Long pontoLocalizavelId) {
		this.pontoLocalizavelId = pontoLocalizavelId;
	}
	public PontoLocalizavel getPontoLocalizavel() {
		return pontoLocalizavel;
	}
	public void setPontoLocalizavel(PontoLocalizavel pontoLocalizavel) {
		this.pontoLocalizavel = pontoLocalizavel;
	}
	public RotaTuristica getRotaTuristica() {
		return rotaTuristica;
	}
	public void setRotaTuristica(RotaTuristica rotaTuristica) {
		this.rotaTuristica = rotaTuristica;
	}
	public int getPosicao() {
		return posicao;
	}
	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}
}
