package br.projetoparticularnext.com.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.projetoparticularnext.com.bean.Endereco;
import br.projetoparticularnext.com.bean.cartao.Cartao;
import br.projetoparticularnext.com.bean.cartao.CartaoCredito;
import br.projetoparticularnext.com.bean.cartao.CartaoDebito;
import br.projetoparticularnext.com.bean.cartao.Compra;
import br.projetoparticularnext.com.bean.cliente.Cliente;
import br.projetoparticularnext.com.bean.conta.Conta;
import br.projetoparticularnext.com.bean.conta.TipoConta;
import br.projetoparticularnext.com.bean.pix.Pix;
import br.projetoparticularnext.com.bean.pix.TipoChavePix;

public class Banco {
	public static Map<String, Conta> banco_De_Dados;
	public static ArrayList<Pix> listPix;
	static boolean ccIsActive = false;
	static boolean cpIsActive = false;
	static Conta cc, cp;

	public Banco() {
		banco_De_Dados = new HashMap<String, Conta>();
		listPix = new ArrayList<Pix>();
		criarObjetosEstaticos();
	}

//CRIA UM OBJETO ESTATICO DE CONTA E CLIENTE PARA FINS DE TESTE
	private void criarObjetosEstaticos() {
		Cliente cliente1 = new Cliente("1234", "luizfernando962@gmail.com", "43546219830", "447218402",
				"Fernando Silveira",
				new Endereco("Ces�rio Lange", "SP", "Centro", "1759", "Rua do Com�rcio", "18285-000"));
		Conta conta = new Conta(cliente1, TipoConta.ContaCorrente);// 1
		Pix pix = new Pix();
		pix.ativarChave(TipoChavePix.CPF, "43546219830", true);
		conta.getListPix().add(pix);
		cadastraConta(conta.getNumero(), conta);
		Cliente cliente2 = new Cliente("1234", "isabela@gmail.com", "43096078882", "447218402", "Isabela Silveira",
				new Endereco("Ces�rio Lange", "SP", "Centro", "1759", "Rua do Com�rcio", "18285-000"));
		Conta conta2 = new Conta(cliente2, TipoConta.ContaPoupanca);// 1
		Pix pix2 = new Pix();
		pix2.ativarChave(TipoChavePix.CPF, "43096078882", true);
		conta2.getListPix().add(pix2);
		cadastraConta(conta2.getNumero(), conta2);
	}

	// BUSCA CONTA POR NUMERO DE CONTA
	public static Conta buscaContaPorNumero(String numeroDestino) {
		Conta conta = banco_De_Dados.get(numeroDestino);
		if (conta == null) {
//			System.out.println("Conta n�o encontrada");
			return null;
		}
		return conta;
	}

	// CADASTRA CLIENTE
	public static void cadastraConta(String numeroCOnta, Conta conta) {
		banco_De_Dados.put(numeroCOnta, conta);
		// k v
//		System.out.println("BANCO DE DADOS CADASTRANDO..." + banco_De_Dados.size());
	}

	public static boolean consultaCpfBanco(String cpfConsole) {
		for (Map.Entry<String, Conta> tipoConta : banco_De_Dados.entrySet()) {
			if (tipoConta.getValue().getCliente().getCpf().equals(cpfConsole)) {
				return true;
			}
		}
		return false;
	}

	public static List<Conta> retornaConta(String cpf, String senha) {
		List<Conta> listContas = new ArrayList<Conta>();
		for (Map.Entry<String, Conta> tipoConta : banco_De_Dados.entrySet()) {
			if (tipoConta.getValue().getCliente().getCpf().equals(cpf)
					&& tipoConta.getValue().getCliente().getSenha().equals(senha)) {
//				System.out.println("ENCONTRADO:" + tipoConta.getValue().getTipoConta());
				if (tipoConta.getValue().getTipoConta() == TipoConta.ContaCorrente) {
					cc = tipoConta.getValue();
				} else {
					cp = tipoConta.getValue();
				}
				listContas.add(tipoConta.getValue());
			}
		}
		return listContas;
	}

	public static String consultaPix(String idCC, String idCP) {
		String chavesPix = "\n-------- SUAS CHAVES ---------\n";
		cc = buscaContaPorNumero(String.valueOf(idCC));
		cp = buscaContaPorNumero(String.valueOf(idCP));
		if (cc != null) {
			if (cc.getNumero().equals(idCC)) {
				ccIsActive = true;
				for (Pix pix : cc.getListPix()) {
					chavesPix += pix.id + " - TIPO:" + pix.tipoChave.name() + " CHAVE:" + pix.conteudoChave + "\n";
				}
			}
		} else if (cp != null) {
			if (cp.getNumero().equals(idCP)) {
				cpIsActive = true;
				for (Pix pix : cp.getListPix()) {
					chavesPix += pix.id + " - " + "TIPO:" + pix.tipoChave.name() + " CHAVE:" + pix.conteudoChave + "\n";
				}

			}

		}
		return chavesPix;
	}

	// CRIA CHAVE PIX
	public static boolean cadastraChavePix(String idCC, String idCP, TipoChavePix tipoChavePix, String conteudoChave,
			boolean b) {
		consultaPix(idCC, idCP);
		if (!identificaPixExistente(conteudoChave)) {
			System.out.println("EXISTE PIX ");
			Pix pix = new Pix();
			pix.ativarChave(tipoChavePix, conteudoChave, b);
			if (ccIsActive && cpIsActive) {
				System.out.println("EXISTE DUAS CONTAS");
				banco_De_Dados.get(idCC).getListPix().add(pix);
				return true;
			} else if (ccIsActive) {
				System.out.println("EXISTE S� CC");
				banco_De_Dados.get(idCC).getListPix().add(pix);
				return true;
			}

			else {
				System.out.println("EXISTE S� CP");
				banco_De_Dados.get(idCP).getListPix().add(pix);
				return true;
			}
		}
		return false;
	}

	private static boolean identificaPixExistente(String conteudoChave) {

		if (cc != null) {
			for (Pix pix : cc.getListPix()) {
				if (pix.getConteudoChave().equals(conteudoChave)) {
					return true;
				}
			}
		} else {
			for (Pix pix : cp.getListPix()) {
				if (pix.getConteudoChave().equals(conteudoChave)) {
					return true;
				}
			}
		}
		return false;

	}

	// zera as instancias e limpa os atributos staticos
	public static void zerarTodasAsInstanciasBanco() {
		cc = null;
		cp = null;
		ccIsActive = false;
		cpIsActive = false;
	}

	public static Conta buscaeTransferePix(String chavePix) {
		for (Map.Entry<String, Conta> tipoConta : banco_De_Dados.entrySet()) {
			for (Pix pix : tipoConta.getValue().getListPix()) {
				if (pix.conteudoChave.equals(chavePix)) {
					return tipoConta.getValue();
				}
			}
		}
		return null;
	}

	public static boolean deletarChave(int chavePix) {
		if (cc != null) {
			for (Pix pix : cc.getListPix()) {
				if (pix.id == chavePix) {
					cc.getListPix().remove(pix);
					return true;
				}
			}

		} else if (cp != null) {
			for (Pix pix : cp.getListPix()) {
				if (pix.id == chavePix) {
					cp.getListPix().remove(pix);
					return true;
				}
			}
		}
		return false;
	}

////cadastra cart�o com base na conta
	public static boolean cadastraCartaoDebito(CartaoDebito debito) {
		if (cc != null) {
//			System.out.println("CC NUM CART�ES: " + cc.getCartoes().size());
			if (cc.getDebito() != null) {
				return false;
			} else {
				cc.setDebito(debito);
				System.out.println("           >>DADOS DO CART�O CRIADO<<\n" + "NUMERO CART�O: " + debito.getNumero()
						+ "\nBANDEIRA: " + debito.getBandeira().toUpperCase() + "   LIMITE DEFINIDO:"
						+ debito.getLimitePorTransacao());
				return true;
			}
		} else {
			if (cp.getDebito() != null) {
				return false;
			} else {
				cp.setDebito(debito);
				System.out.println("           >>DADOS DO CART�O CRIADO<<\n" + ">>NUMERO CART�O: " + debito.getNumero()
						+ "\n>>BANDEIRA: " + debito.getBandeira().toUpperCase() + "   LIMITE DEFINIDO:"
						+ debito.getLimitePorTransacao());
				return true;
			}
		}
	}

	public static boolean cadastraCartaoCredito(CartaoCredito credito) {
		if (cc != null) {
//			System.out.println("CC NUM CART�ES: " + cc.getCartoes().size());
			if (cc.getCredito() != null) {
				return false;
			} else {
				cc.setCredito(credito);
				System.out.println("           >>DADOS DO CART�O CRIADO<<\n" + ">>NUMERO CART�O: " + credito.getNumero()
						+ "\n>>BANDEIRA: " + credito.getBandeira().toUpperCase() + "   LIMITE DISP:"
						+ credito.getLimite());
				return true;
			}
		} else {
			if (cp.getCredito() != null) {
				return false;
			} else {
				cp.setCredito(credito);
				System.out.println("           >>DADOS DO CART�O CRIADO<<\n" + ">>NUMERO CART�O: " + credito.getNumero()
						+ "\n>>BANDEIRA: " + credito.getBandeira().toUpperCase() + "   LIMITE DISP:"
						+ credito.getLimite());
				return true;
			}
		}
	}

	public static CartaoCredito recuperaCartaoCredito() {
		if (cc != null) {
			if (cc.getCredito() != null) {
				return cc.getCredito();
			} else
				return null;
		} else {
			if (cp != null) {
				if (cp.getCredito() != null) {
					return cp.getCredito();
				} else
					return null;
			}
		}
		return null;
	}

	public static CartaoDebito recuperaCartaoDebito() {
		if (cc != null) {
			if (cc.getDebito() != null) {
				return cc.getDebito();
			} else
				return null;
		} else {
			if (cp != null) {
				if (cp.getDebito() != null) {
					return cp.getDebito();
				} else
					return null;
			}
		}
		return null;
	}

	public static boolean ativaDesativaCartaoCredito(boolean b) {
		if (cc != null) {
			if (cc.getCredito() != null) {
				cc.getCredito().setAtivo(b);
				return true;
			} else
				return false;
		} else {
			if (cp != null) {
				if (cp.getCredito() != null) {
					cp.getCredito().setAtivo(b);
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	public static boolean ativaDesativaCartaoDebito(boolean b) {
		if (cc != null) {
			if (cc.getDebito() != null) {
				cc.getDebito().setAtivo(b);
				return true;
			} else
				return false;
		} else {
			if (cp != null) {
				if (cp.getDebito() != null) {
					cp.getDebito().setAtivo(b);
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	public static boolean updateDataVencimento(String dataVencimento) {
		if (cc != null) {
			if (cc.getCredito() != null) {
				cc.getCredito().setDataVencimento(Utils.returnDataDiaDefinido(dataVencimento));
				return true;
			} else
				return false;
		} else {
			if (cp != null) {
				if (cp.getCredito() != null) {
					cp.getCredito().setDataVencimento(Utils.returnDataDiaDefinido(dataVencimento));
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	public static boolean updateLimitePortTransacao(double novoLimiteTransacao) {
		if (cc != null) {
			if (cc.getDebito() != null) {
				cc.getDebito().setLimitePorTransacao(novoLimiteTransacao);
				return true;
			} else
				return false;
		} else {
			if (cp != null) {
				if (cp.getDebito() != null) {
					cp.getDebito().setLimitePorTransacao(novoLimiteTransacao);
					return true;
				} else
					return false;
			}
		}
		return false;
	}

	public static String cadastraCompraCredito(String descricao, double valor, String senha) {
		if (cc != null) {
			if (cc.getCredito() != null) {
				if (cc.getCredito().getSenha().equals(senha)) {
					if (cc.getCredito().getLimite() >= valor) {
						cc.getCredito().setLimite(cc.getCredito().getLimite() - valor);
						cc.getCredito().getCompras().add(new Compra(valor, descricao));
						cc.getCredito().setValorFatura(cc.getCredito().getValorFatura() + valor);
						return "\n         >>COMPRA EFETUADA COM SUCESSO!<<";
					}
					return "\n         >>SEM LIMITE DISPONIVEL PARA ESTA TRANSACAO!<<";
				} else
					return "\n         >>SENHA INCORRETA!<<";
			} else
				return "\n         >>N�O EXISTE CART�O CADASTRADO NESTA CONTA!<<";
		} else {
			if (cp != null) {
				if (cp.getCredito() != null) {
					if (cc.getCredito().getSenha().equals(senha)) {
						if (cp.getCredito().getLimite() >= valor) {
							cp.getCredito().setLimite(cc.getCredito().getLimite() - valor);
							cp.getCredito().getCompras().add(new Compra(valor, descricao));
							return "\n         >>COMPRA EFETUADA COM SUCESSO!<<";
						}
						return "\n         >>SEM LIMITE DISPONIVEL PARA ESTA TRANSACAO!<<";

					} else
						return "\n         >>SENHA INCORRETA!<<";
				} else
					return "\n         >>N�O EXISTE CART�O CADASTRADO NESTA CONTA!<<";
			}
		}
		return "\n         >>HOUVE UM ERRO NA TRANSA��O!<<";
	}

	public static String cadastraCompraDebito(String descricao, double valor, String senha) {
		if (cc != null) {
			if (cc.getDebito() != null) {
				if (cc.getDebito().getSenha().equals(senha)) {
					if (cc.getSaldo() >= valor && cc.getDebito().getLimitePorTransacao() >= valor) {
						cc.setSaldo(cc.getSaldo() - valor);
						cc.getDebito().getCompras().add(new Compra(valor, descricao));
						return "\n         >>COMPRA NO D�BITO EFETUADA COM SUCESSO!<<";
					}
					return "\n         >>SEM LIMITE DISPONIVEL PARA ESTA TRANSACAO!<<";
				} else
					return "\n         >>SENHA INCORRETA!<<";
			} else
				return "\n         >>N�O EXISTE CART�O DE D�BITO CADASTRADO NESTA CONTA!<<";
		} else {
			if (cp != null) {
				if (cp.getDebito() != null) {
					if (cp.getDebito().getSenha().equals(senha)) {
						if (cp.getSaldo() >= valor && cp.getDebito().getLimitePorTransacao() >= valor) {
							cp.setSaldo(cp.getSaldo() - valor);
							cp.getDebito().getCompras().add(new Compra(valor, descricao));
							return "\n         >>COMPRA NO CR�DITO EFETUADA COM SUCESSO!<<";
						}
						return "\n         >>SEM LIMITE DISPONIVEL PARA ESTA TRANSACAO!<<";

					} else
						return "\n         >>SENHA INCORRETA!<<";
				} else
					return "\n         >>N�O EXISTE CART�O DE D�BITO CADASTRADO NESTA CONTA!<<";
			}
		}
		return "\n         >>HOUVE UM ERRO NA TRANSA��O!<<";
	}

	public static String consultaFaturaCredito() {
		String fatura = "----------- FATURA ATUAL ----------\n";
		if (cc != null) {
			if (cc.getCredito() != null) {
				fatura += "" + cc.getCredito().getCompras().size() + " Compras realizadas com este cart�o\n"
						+ "Fatura atual: " + Utils.convertToReais(cc.getCredito().getValorFatura()) + "  vencimento: "
						+ cc.getCredito().getDataVencimento();
				for (Compra compra : cc.getCredito().getCompras()) {
					fatura += "\n *Descricao:" + compra.getDescricao() + "  Valor: -"
							+ Utils.convertToReais(compra.getValor()) + "  " + compra.getDataCompra();
				}
				fatura += "-----------------------------------";
				return fatura;
			} else
				return fatura;
		} else {
			if (cp != null) {
				if (cp.getCredito() != null) {
					fatura += "" + cp.getCredito().getCompras().size() + " Compras realizadas com este cart�o\n"
							+ "Fatura atual: " + Utils.convertToReais(cp.getCredito().getValorFatura())
							+ "  vencimento: " + cc.getCredito().getDataVencimento();
					for (Compra compra : cp.getCredito().getCompras()) {
						fatura += "\n *Descricao:" + compra.getDescricao() + "  Valor: -"
								+ Utils.convertToReais(compra.getValor()) + "  " + compra.getDataCompra();
					}
					fatura += "-----------------------------------";
					return fatura;
				} else
					return fatura;
			}
		}
		return null;
	}

	public static String consultaExtratoDebito() {
		String fatura = "----------- EXTRATO ----------\n";
		if (cc != null) {
			if (cc.getDebito() != null) {
				fatura += "" + cc.getDebito().getCompras().size() + " Compras realizadas com este cart�o\n";
				for (Compra compra : cc.getDebito().getCompras()) {
					fatura += "\n *Descricao:" + compra.getDescricao() + "  Valor: -"
							+ Utils.convertToReais(compra.getValor()) + "  " + compra.getDataCompra();
				}
				fatura += "-----------------------------------";
				return fatura;
			} else
				return fatura;
		} else {
			if (cp != null) {
				if (cp.getCredito() != null) {
					fatura += "" + cp.getDebito().getCompras().size() + " Compras realizadas com este cart�o\n";
					for (Compra compra : cp.getDebito().getCompras()) {
						fatura += "\n *Descricao:" + compra.getDescricao() + "  Valor: -"
								+ Utils.convertToReais(compra.getValor()) + "  " + compra.getDataCompra();
					}
					fatura += "-----------------------------------";
					return fatura;
				} else
					return fatura;
			}
		}
		return null;
	}

	public static String debitarFaturaCredito(double valorPagamento) {
		if (cc != null) {
			if (cc.getCredito() != null) {
				if (cc.getSaldo() >= valorPagamento && cc.getCredito().getValorFatura() > 0.0) {
					cc.setSaldo(cc.getSaldo() - valorPagamento);
					cc.getCredito().setValorFatura(cc.getCredito().getValorFatura() - valorPagamento);
					return "\n         >>PAGAMENTO DE "+Utils.convertToReais(valorPagamento)+ "DA FATURA REALIZADA COM SUCESSO!<<";
				} else
					return "\n         >>VOCE N�O POSSUI SALDO EM CONTA PARA REALIZAR ESTE PAGAMENTO!<<";
			} else
				return "\n         >>N�O EXISTE CART�O DE CR�DITO CADASTRADO NESTA CONTA!<<";
		} else {
			if (cp != null) {
				if (cp.getCredito() != null) {
					if (cp.getSaldo() >= valorPagamento && cc.getCredito().getValorFatura() > 0.0) {
						cp.setSaldo(cp.getSaldo() - valorPagamento);
						cp.getCredito().setValorFatura(cp.getCredito().getValorFatura() - valorPagamento);
						return "\n         >>PAGAMENTO DE FATURA REALIZADA COM SUCESSO!<<";
					} else
						return "\n         >>VOCE N�O POSSUI SALDO EM CONTA PARA REALIZAR ESTE PAGAMENTO!<<";
				} else
					return "\n         >>N�O EXISTE CART�O DE CR�DITO CADASTRADO NESTA CONTA!<<";
			}
		}
		return "\n         >>HOUVE UM ERRO NA TRANSA��O!<<";
	}

}
