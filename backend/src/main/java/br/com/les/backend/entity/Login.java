package br.com.les.backend.entity;

import java.util.Date;

import javax.persistence.Embeddable;

@Embeddable
public class Login extends EntidadeDominio {

	private static final long serialVersionUID = 2225388166302371888L;

	private String nomeLogin;
	private String senha;
	private Date dataCriacao;
	private Boolean ativo;

	public String getSenha() {
		return senha;
	}
	public void setSenha( String senha ) {
		this.senha = senha;
	}
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao( Date dataCriacao ) {
		this.dataCriacao = dataCriacao;
	}
	public Boolean getAtivo() {
		return ativo;
	}
	public void setAtivo( Boolean ativo ) {
		this.ativo = ativo;
	}
	public String getNomeLogin() {
		return nomeLogin;
	}
	public void setNomeLogin(String nomeLogin) {
		this.nomeLogin = nomeLogin;
	}
}
