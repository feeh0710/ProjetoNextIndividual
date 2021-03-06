package br.projetoparticularnext.com.bean.cartao;

import br.projetoparticularnext.com.utils.Utils;

public class CartaoCredito extends Cartao {


	private double limite;
	private String dataVencimento;
	private double valorFatura;
	private Apolice apolice;

	public CartaoCredito(String bandeira, String senha, boolean isAtivo, double limite, String dataVencimento) {
		super(Utils.geraBlocosNumeros(4), bandeira, senha, isAtivo);// cartao pede
		this.limite = limite;
		this.dataVencimento = Utils.returnDataDiaDefinido(dataVencimento);
		this.valorFatura = 0.0;
	}
	

	public double getLimite() {
		return limite;
	}

	public void setLimite(double limite) {
		this.limite = limite;
	}


	public String getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(String dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public double getValorFatura() {
		return valorFatura;
	}

	public void setValorFatura(double valorFatura) {
		this.valorFatura = valorFatura;
	}


	public Apolice getApolice() {
		return apolice;
	}


	public void setApolice(Apolice apolice) {
		this.apolice = apolice;
	}

	
	

}
