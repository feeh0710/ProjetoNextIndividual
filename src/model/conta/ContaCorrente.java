package model.conta;

import model.cliente.Cliente;

public class ContaCorrente extends Conta {
	
	

	public ContaCorrente(Cliente cliente) {
		super(cliente);
		
		
	}


	private double taxaManutencao;
	
	
// TINHA RETORNO DOUBLE, MAS N�O FAZ SENTIDO POIS VOU MANIPULAR A CONTA
	public void descontarTaxa() {
		
	}
}
