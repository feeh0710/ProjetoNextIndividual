package br.projetoparticularnext.com.bean;

import br.projetoparticularnext.com.bean.cartao.CartaoCredito;

public class Apolice {
	private int nrApolice;
	private CartaoCredito cartaoCredito;
	private Seguro seguro;
	private double valorApolice;
	private String descricaoCondicoes;
	
	public int gerarNumeroApolice(CartaoCredito cartaoCredito, Seguro seguro,
			double valorApolice,String descricaoCondicoes) {
		return 0;
	}
}
