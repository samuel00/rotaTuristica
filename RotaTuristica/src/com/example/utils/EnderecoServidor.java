package com.example.utils;

public class EnderecoServidor {
	
	private String endereco;
	
	public EnderecoServidor(){
//		this.endereco = "http://10.0.2.2:8282/WSRotaTuristica/";
		this.endereco = "http://177.74.1.48:8080/WSRotaTuristica/";
	}
	
	public String getEndereco(){
		return this.endereco;
	}
}
