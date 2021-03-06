package br.projetoparticularnext.com.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.projetoparticularnext.com.bean.Endereco;
import br.projetoparticularnext.com.bean.cartao.Apolice;
import br.projetoparticularnext.com.bean.cartao.CartaoCredito;
import br.projetoparticularnext.com.bean.cartao.CartaoDebito;
import br.projetoparticularnext.com.bean.conta.TipoConta;
import br.projetoparticularnext.com.bo.ApoliceBO;
import br.projetoparticularnext.com.bo.CartaoBO;
import br.projetoparticularnext.com.bo.ClienteBO;
import br.projetoparticularnext.com.bo.ContaBO;
import br.projetoparticularnext.com.bo.EnderecoBO;
import br.projetoparticularnext.com.bo.PixBO;
import br.projetoparticularnext.com.utils.Banco;
import br.projetoparticularnext.com.utils.Menus;
import br.projetoparticularnext.com.utils.UtilFormatConsole;
import br.projetoparticularnext.com.utils.Utils;

public class Main {
	static Utils utils = new Utils();
	static Banco banco = new Banco();
	static String cpfConsole = "";
	static String senha = "";
	static boolean logado = false; // FLAG
	static int tentativas = 0;// FLAG PARA VERIFICA QUANTIDADE DE ERROS

	public static void main(String[] args) {
		// Utils.returnDataDiaDefinido("20");

		logado = false;
		menuInicio();

	}

// EXIBE MENU LOGIN
	private static void menuInicio() {
		UtilFormatConsole.writeConsole("    -----BEM VINDO AO BANCO NEXT-----");
		UtilFormatConsole.writeConsole("    ------------  LOGIN  ------------");

		while (true) {
			try {
				cpfConsole = utils.lerConsole("|Digite seu CPF:");
			} catch (NumberFormatException e) {
				System.err.println("Digite Apenas números");
			}

			if (ContaBO.validarCpf(cpfConsole)) {
				if (ContaBO.testaLogin(cpfConsole)) {// Verifica se cpf existe no banco
					senha = utils.lerConsole("|Digite sua senha: ");
					buscaContaCadastrada(ContaBO.buscaContaCadastrada(true, cpfConsole, senha));
				} else {
					break;
				}
			}

		}
		cadastrar();
	}

// CADASTRA O USUARIO
	private static void cadastrar() {
		UtilFormatConsole.writeConsole("    ----- CADASTRO DADOS BASICOS -----");
		System.out.println("|Seu CPF: " + cpfConsole);
		String rg = utils.lerConsole("|Digite seu RG: ");
		String nome = utils.lerConsole("|Digite seu nome completo: ");
		String email = utils.lerConsole("|Digite seu email: ");
		senha = utils.lerConsole("|Digite sua senha: ");
		UtilFormatConsole.writeConsole("    ----- CADASTRO DO ENDEREÇO -----");
		String rua = utils.lerConsole("|Digite sua rua: ");
		String bairro = utils.lerConsole("|Digite seu bairro: ");
		String numero = utils.lerConsole("|Digite seu numero: ");
		String cidade = utils.lerConsole("|Digite sua cidade: ");
		String estado = utils.lerConsole("|Digite seu estado: ");
		String cep = utils.lerConsole("|Digite seu cep: ");
		Menus.exibeMenuOpcoesConfirmacao("1 - POUPANÇA", "2 - CORRENTE", "ESCOLHA A CONTA", "               3 - AMBAS");
		String tipoConta = utils.lerConsole("|Digite a opção:");

		// FALTA VALIDAR CAMPOS ANTES DE ENVIAR
		// CADASTRA ENDERECO,CLIENTE E CONTA
		EnderecoBO enderecoBO = new EnderecoBO(cidade, estado, bairro, numero, rua, cep);
		identificaECadastraContas(tipoConta, senha, email, cpfConsole, rg, nome, enderecoBO.endereco);
		utils.loading("\n\nSalvando cliente e cadastrando conta"); // EXIBE LOADING NA TELA
		System.out.println("\n\n>>Cliente e conta cadastrados com sucesso!<<\n\n");
		ContaBO.buscaContaCadastrada(true, cpfConsole, senha);// RECUPERA LISTA DE CONTAS DESTE CPF
		menuPrincipal();
	}

// IDENTIFICA O TIPO DE CONTA ENUM e cadastra
	private static void identificaECadastraContas(String tipoConta, String senha, String email, String cpf, String rg,
			String nome, Endereco endereco) {
		ClienteBO clienteBO = new ClienteBO(senha, email, cpf, rg, nome, endereco);
		if (tipoConta.equals("1")) {
			new ContaBO(clienteBO.cliente, TipoConta.ContaCorrente);
		} else if (tipoConta.equals("2")) {
			new ContaBO(clienteBO.cliente, TipoConta.ContaPoupanca);
		} else if (tipoConta.equals("3")) {
			new ContaBO(clienteBO.cliente, TipoConta.ContaCorrente);
			new ContaBO(clienteBO.cliente, TipoConta.ContaPoupanca);
		}

	}

// EXIBE MENU PRINCIPAL
// SWITCH COM OPERAÇÕES PRINCIPAIS(SALDO,SAQUE,TRANSFERENCIA,DEPOSITO,PIX E
// SAIR)
	private static void menuPrincipal() {

		ContaBO.TaxaseRendimentos();// consulta taxas e rendimentos e seta valores /mes

		boolean loop = true;
		while (loop) {
			List<String> listDetalhes = ContaBO.exibeDetalhesConta();// BUSCA DETALHES CONTA
			UtilFormatConsole.writeConsole(listDetalhes, listDetalhes.size());
			Menus.exibeOpcoesConta();// EXIBE DETALHES CONTA E MENU PRINCIPAL
			String operacao = utils.lerConsole("|Digite sua operação: ");

			switch (operacao) {
			case "1": { // transferencia
				UtilFormatConsole.writeConsole(" ------------ TRANSFERENCIA  -----------");
				transferencia();
				break;
			}
			case "2": { // deposito
				// solicita valor, envia pro metodo saque que retorna a mensagem
				UtilFormatConsole.writeConsole("    ------------  DEPÓSITO  ------------");
				deposito();
				break;
			}
			case "3": { // saldo
				UtilFormatConsole.writeConsole("    ------------  SALDO  ------------");
				System.out.println(ContaBO.consultaSaldo(Menus.exibeOpcaoConta("Qual conta?")));
				break;
			}
			case "4": { // saque
				UtilFormatConsole.writeConsole("    ------------  SAQUE  ------------");
				// solicita valor, envia pro metodo saque que retorna a mensagem
				saque();
				break;
			}
			case "5": { // area crédito
				buscaEExibeOpcoesCartaoCredito();
				break;
			}
			case "6": { // area debitos
				buscaEExibeOpcoesDebito();
				break;
			}
			case "7": { // area pix
				Menus.exibeMenuPix();
				String op = utils.lerConsole("|Digite a operação: ");
				buscaOperacaoPix(op);
				break;
			}
			case "0": { // sair
				utils.loading("\n\nSaindo"); // EXIBE LOADING NA TELA
				loop = logout();
				break;
			}
			default:
				System.out.println("|    DIGITE NUMEROS ENTRE 0 E 3   |");
			}
		}
		menuInicio();
	}

	// REALIZA O LOGOUT DA CONTA
	public static boolean logout() {
		boolean loop;
		System.out.println("\n\n|        Logoff Concluido!        |\n\n"
				+ "====================================================================================");
		ContaBO.zerarAlocacoesDeMemoria(); // zera os cadastros setados em mem�ria
		Banco.zerarTodasAsInstanciasBanco();
		loop = false;
		return loop;
	}

	// BUSCA CARTAO CADASTRADO E EXIBE O MENU E OPÇÕES DE DEBITO
	public static void buscaEExibeOpcoesDebito() {
		boolean cadastrado = false;
		boolean cartaoAtivado = false;
		CartaoDebito debito = CartaoBO.recuperaCartaoDebito();
		if (debito != null) {
			cadastrado = true;
			cartaoAtivado = debito.isAtivo();
			Menus.dadosCartoes(debito.getNumero(), "", debito.getLimitePorTransacao(), debito.getLimitePorTransacao(),
					"P/Transacao", cartaoAtivado);
		}
		Menus.exibeOpcoesCartaoDebito(cadastrado, cartaoAtivado);
		String op = utils.lerConsole("|Digite a operação: ");
		buscaOperacaoDebito(op, cadastrado, cartaoAtivado, debito);
	}

	// BUSCA CARTAO CADASTRADO E EXIBE O MENU E OPÇÕES DE CREDITO
	public static void buscaEExibeOpcoesCartaoCredito() {
		boolean cadastrado = false;
		boolean cartaoAtivado = false;
		CartaoCredito credito = CartaoBO.recuperaCartaoCredito();
		if (credito != null) {
			cartaoAtivado = credito.isAtivo();
			cadastrado = true;
			Menus.dadosCartoes(credito.getNumero(), String.valueOf(credito.getDataVencimento()),
					credito.getValorFatura(), credito.getLimite(), "disponivel", cartaoAtivado);
		}
		Menus.exibeOpcoesCartaoCredito(cadastrado, cartaoAtivado);
		String op = utils.lerConsole("|Digite a operação: ");
		buscaOperacaoCredito(op, cadastrado, cartaoAtivado, credito);
	}

//REALIZA SAQUE 
	public static void saque() {
		tentativas = 0;
		while (true) {
			if ((tentativas % 2) == 0 && tentativas > 0) {
				Menus.exibeMenuOpcoesConfirmacao("1 - SIM", "2 - NÃO", "SAIR DA OPERAÇÃO?","");
				if (UtilFormatConsole.readConsole().equals("1")) {
					System.out.println("      >>Operação cancelada pelo usuario!");
					break;
				}
			} 
			if (ContaBO.saqueConta(Menus.exibeOpcaoConta("Qual conta"))) {
				utils.loading("\n\nSacando"); // EXIBE LOADING NA TELA
				System.out.println("\n\n>>Saque realizado com sucesso!<<");
				menuPrincipal();
				break;
			} else {
				utils.loading("\n\nSacando"); // EXIBE LOADING NA TELA
				System.err.println("\n>>Saldo Insuficiente<<\n");
			}
			tentativas ++;
		}
	}

//REALIZA DEPOSITO
	public static void deposito() {
		tentativas = 0;
		
		while (true) {
			if ((tentativas % 2) == 0 && tentativas >0) {
				Menus.exibeMenuOpcoesConfirmacao("1 - SIM", "2 - NÃO", "SAIR DA OPERAÇÃO?","");
				if (UtilFormatConsole.readConsole().equals("1")) {
					System.out.println("      >>Operação cancelada pelo usuario!");
					break;
				}
			} 
			if (ContaBO.depositaNaConta(Menus.exibeOpcaoConta("Qual conta?"))) {
				utils.loading("\n\nDepositando"); // EXIBE LOADING NA TELA
				System.out.println("\n\n>>Deposito realizado com sucesso!<< \n\n");
				menuPrincipal();
				break;
			} else {
				utils.loading("\n\nDepositando"); // EXIBE LOADING NA TELA
				System.err.println("\n>>Erro no depósito!<< \n");
			}
			tentativas ++;
		}
	}

// REALIZA TRANFERENCIA ENTRE CONTAS
	public static void transferencia() {
		tentativas = 0;
		while (true) {

			if ((tentativas % 2) == 0 && tentativas >0) {
				Menus.exibeMenuOpcoesConfirmacao("1 - SIM", "2 - NÃO", "SAIR DA OPERAÇÃO?","");
				if (UtilFormatConsole.readConsole().equals("1")) {
					break;
				}
			} else {
				String numDestino = utils.lerConsole("Digite o numero da conta destino: ");
				double valorDeTransferencia = 0.0;
				try {
					valorDeTransferencia = Double
							.parseDouble(utils.lerConsole("Digite o valor que deseja transferir: "));
					String[] resposta = ContaBO.buscaContaeTransfere(numDestino, valorDeTransferencia,
							Menus.exibeOpcaoConta("Qual conta?"));
					utils.loading("\nTransferindo"); // EXIBE LOADING NA TELA
					System.out.println("\n"+resposta[0]);
					if (resposta[1].equals("0")) {
						menuPrincipal();
						break;
					}
				} catch (NumberFormatException e) {
					System.err.println("     >>Digite apenas números!<<");
				}
			}
			tentativas++;
		}
	}

	// BUSCA OPERACOES E EXECUTA OPERACOES DO MENU CREDITO
	private static void buscaOperacaoCredito(String op, boolean cadastrado, boolean ativado, CartaoCredito credito) {
		tentativas = 0;
		if (op.equals("*")) {//CADASTRA CREDITO
			String numBandeira = retornaBandeira();
			if (!numBandeira.equals("") || !numBandeira.equals("0") && cadastrado) {
				String senha = retornaSenha();// solicita senha e retorna
				String dataVencimento = escolherDataVencimento();
				System.out.println(CartaoBO.cadastraCartaoCredito(numBandeira, senha, true, dataVencimento)
						? "     >>Cartão de crédito criado com sucesso!<<    "
						: "\n     >>Houve um erro ao criar o cartão!<<     ");
			}
		} else if (op.equals("1") && cadastrado) {// COMPRAR
			if (ativado) {
				comprarComCartao(1);// 1 credito e 2 debito
			} else {
				System.err.println("         >>Cartão se encontra bloqueado!<<");
			}
		} else if (op.equals("2") && cadastrado) {// CONSULTAR FATURA
			System.out.println(CartaoBO.consultaFaturasCredito());
		} else if (op.equals("3") && cadastrado) {// ALTERA VENCIMENTO
			System.out.println("Data de vencimento atual: " + credito.getDataVencimento());
			String dataVencimento = escolherDataVencimento();
			System.out.println(
					CartaoBO.alteraDataVencimento(dataVencimento) ? "\n          >>Data alterada com sucesso!<<\n"
							: "\n        >>Houve um erro na alteração da Data!<<");
		} else if (op.equals("4") && cadastrado) {// PAGAMENTO DE FATURA
			System.out.println("\n>>Fatura atual:" + utils.convertToReais(credito.getValorFatura()));
			while(true) {
				try {
					double valorPagamento = Double.parseDouble(utils.lerConsole("Digite o valor que deseja pagar: "));
					System.out.println("\n"+CartaoBO.debitarFaturaCredito(valorPagamento));
					
					break;
				}catch(NumberFormatException e) {
					System.err.println("\n   >>Valor precisa ser no formato 0.00");
				}
			}
		} else if (op.equals("5") && cadastrado) {// BLOQUEIA CARTON
			exibeAtivacao(ativado, 2);// 1 = debito 2= credito
		} else if (op.equals("6") && cadastrado) {// APOLICE DO SEGURO
			buscaOperacoesDeSeguro();
		}
	}

	private static void buscaOperacoesDeSeguro() {
		Apolice apolice = ApoliceBO.buscaApolice();// VERIFICA SE EXISTE APOLICE NO CARTAO DO USUARIO
		if (apolice != null) {
			Menus.exibeOpcoesApolice(true);
		} else {
			Menus.exibeOpcoesApolice(false);
		}
		String op = utils.lerConsole("|Digite a operação: ");
		if (op.equals("*")) {// CONTRATAÇÃO APOLICE SEGURO
			Menus.exibeMenuOpcoesSeguro();
			String opcaoTipoSeguro = UtilFormatConsole.readConsole();
			String anos = "/";// para forçar a entrada no while
			while (!anos.matches("[0-9]*")) {
				anos = utils.lerConsole("|Digite quantos anos de contrato: ");
			}
			apolice = ApoliceBO.CriaApoliceProvisoria(opcaoTipoSeguro, Integer.parseInt(anos));
			Menus.exibeApolice(apolice, ContaBO.retornaContaPrincipal());
			double valorTaxaAnos = (apolice.getAnos() * apolice.getSeguro().getTaxa());
			Menus.exibeMenuOpcoesConfirmacao("1 - SIM", "2 - NÃO", "CONFIRMA AS CONDIÇOES?", ">>" + apolice.getAnos()
					+ " anos de Contrato " + ">>valor total que será debitado: " + Utils.convertToReais(valorTaxaAnos));
			if (UtilFormatConsole.readConsole().equals("1")) {
				if (ContaBO.saqueDeUmaDasContas(valorTaxaAnos)) {
					utils.loading("\n\n        Contratando seguro");
					System.out.println(
							ApoliceBO.CriaApolice(apolice) ? "\n      >>Apólice do seguro criada com sucesso!<<\n\n"
									: "\n      Houve um erro na contratação<<");
				} else {
					System.err.println(">>Você não possui saldo para pagamento de taxa da apólice<<");
				}
			} else {
				System.out.println("\n     >>Aquisição do seguro cancelada pelo usuário!<<");
			}

		} else if (op.equals("1")) { // VISUALIZAR SEGURO
			Menus.exibeApolice(apolice, ContaBO.retornaContaPrincipal());
		} else if (op.equals("2")) { // CANCELAR SEGURO
			Menus.exibeDetalhesApolice(apolice);
			Menus.exibeMenuOpcoesConfirmacao("1 - SIM", "       2 - NÃO", "CANCELAR APÓLICE?", "");
			if (UtilFormatConsole.readConsole().equals("1")) {
				utils.loading("\n\n        Cancelando seguro");
				ApoliceBO.deletaApolice();
				System.out.println("      >>Cancelamento de seguro efetuado com sucesso!<<");
			} else {
				System.out.println("      >>Não foi possivel o cancelamento do seguro!<<");
			}
		} else if (op.equals("3")) { // ACIONAR SEGURO
			// verificar se carencia já passou
			// depositar valor na conta
			// add nova carencia

			// new Date().after(utils.readConsoleData(apolice.getDataCarencia())
			// SETAR DATA SENÃO NUNCA ENTRA AQUI
			Menus.exibeDetalhesApolice(apolice);
			if (new Date().after(utils.readConsoleData(apolice.getDataCarencia()))) {
				utils.loading("\n\n        Acionando seguro");
				if (ContaBO.depositaEmUmaDasContas(apolice.getSeguro().getValor())) {
					apolice.setDataCarencia(90);
					Menus.exibeDetalhesApolice(apolice);
					System.out.println("       >>Seguro acionado com sucesso!<<\n "
							+ "       >>valor já se encontra disponivel<< \n"
							+ "       >>Carencia de 90 dias incluida na apólice<<");
				} else
					System.err.println(">>Não foi possivel acionar o seguro!<<");
			} else
				System.err
						.println(">> Não foi possivel acionar pois a data atual está dentro da carencia estipulada!\n");

		}
	}

	public static void comprarComCartao(int op) {
		Menus.exibeMenuCompra();
		tentativas = 0;
		while(true) {
			if(tentativas > 0) {
				Menus.exibeMenuOpcoesConfirmacao("1 - SIM", "       2 - NÃO", "NOVA COMPRA?", "");
				if (UtilFormatConsole.readConsole().equals("2")) {
					break;
				}
			}
			try {
				String descricao = utils.lerConsole("|Nome do Produto: ");
				double valor = Double.parseDouble(utils.lerConsole("|Digite o valor do produto: "));
				String senha = utils.lerConsole("|Digite a senha: ");
				if(!descricao.equals("") && valor > 0.0 && !senha.equals("")) {
					if (op == 1) {
						System.out.println(CartaoBO.cadastraCompraCredito(descricao, valor, senha));
					} else {
						System.out.println(CartaoBO.cadastraCompraDebito(descricao, valor, senha));
					}
				}else {
					System.err.println("   >>Todos os campos precisam estar preenchidos<<");
				}
				
			}catch(NumberFormatException e) {
				System.err.println("   >>Valor do produto precisa ser apenas numeros (ex: 2.99)");
			}
			tentativas++;
		}
	}

	// BUSCA OPERAÇÕES E EXECUTA OPERAÇÕES DO MENU DEBITO
	private static void buscaOperacaoDebito(String op, boolean cadastrado, boolean ativado, CartaoDebito debito) {
		if (op.equals("*")) {//CADASTRA DÉBITO
			String numBandeira = retornaBandeira();
			if (!numBandeira.equals("") || !numBandeira.equals("0") && cadastrado) {
				// SETANDO SENHA
				String senha = retornaSenha();
				utils.loading("\n       Criando cartão de débito");
				if (CartaoBO.cadastraCartaoDebito(numBandeira, senha, true, returnLimite())) {
					System.out.println("     >>Cartão de débito criado com sucesso!<<    ");
				} else {
					System.err.println("\n     >>Houve um erro ao criar o cartão!<<     ");
				}
			}
		} else if (op.equals("1") && cadastrado) {// COMPRAR
			comprarComCartao(2);// 1 credito e 2 debito

		} else if (op.equals("2") && cadastrado) {// CONSULTA EXTRATO
			System.out.println(CartaoBO.consultaExtratoDebito());

		} else if (op.equals("3") && cadastrado) {// ALTERA LIMITE P/TRANSACAO
			System.out.println("\n--------------- ALTERAR LIMITE POR TRANSACAO ---------------");
			System.out.println(">>Limite atual: " + debito.getLimitePorTransacao());
			double novoLimiteTransacao = returnLimite();
			System.out.println(CartaoBO.alterarLimitePorTransacao(novoLimiteTransacao)
					? "\n          >>Limite alterado com sucesso!<<\n"
					: "        >>Houve um erro na alteração do limite!<<");
		} else if (op.equals("4") && cadastrado) {// BLOQUEIA CARTON
			exibeAtivacao(ativado, 1);// 1 = debito 2= credito
		}
	}

// retorna o limite com base na opçao selicionada
	private static double returnLimite() {
		Menus.exibeLimites();// EXIBE LIMITES NO CONSOLE
		String opLimite = utils.lerConsole("|Digite a operação do Limite:");
		if (opLimite.equals("1")) {
			return 100.0;
		} else if (opLimite.equals("2")) {
			return 500.0;
		} else if (opLimite.equals("3")) {
			return 1000.0;
		} else if (opLimite.equals("4")) {
			return 5000.0;
		} else if (opLimite.equals("5")) {
			return 10000.0;
		}
		return 100.0;
	}

// retorna a bandeira do cartao com base na opcao selecionada
	public static String retornaBandeira() {
		Menus.exibeBandeirasCartoes();
		String numBandeira = utils.lerConsole("|Digite a operação da bandeira: ");
		// SETANDO BANDEIRA
		if (numBandeira.equals("1"))// VISA
		{
			System.out.println("\n    >>Visa selecionado!<<    \n");
			return "visa";

		} else if (numBandeira.equals("2")) {// MASTERCARD
			System.out.println("\n    >>Mastercard selecionado!<<    \n");
			return "mastercard";
		} else if (numBandeira.equals("3")) {// MASTERCARD
			System.out.println("\n    >>Elo selecionado!<<    \n");
			return "elo";
		}else {
			System.out.println("\n    >>Nenhuma opção selecionada!<<\n      Visa padrão selecionado!<<    \n");
			return "visa";
		}
	}

	// ATIVANDO OU DESATIVANDO CARTON
	public static void exibeAtivacao(boolean ativado, int tipo) {
		if (ativado) {
			boolean operacao = (tipo == 1) ? CartaoBO.ativaDesativaDebito(false) : CartaoBO.ativaDesativaCredito(false);
			System.out.println(
					(operacao) ? "\n     >>Cartao bloqueado!<<     \n" : "\n     >>Houve um erro no bloqueio<<     \n");

		} else {
			boolean operacao = (tipo == 1) ? CartaoBO.ativaDesativaDebito(true) : CartaoBO.ativaDesativaCredito(true);
			System.out.println((operacao) ? "\n     >>Cartão desbloqueado!<<     \n"
					: "\n     >>Houve um erro no desbloqueio<<     \n");
		}
	}

// SOLICITA UMA SENHA DO USUARIO E RETORNA 
	public static String retornaSenha() {
		// SETANDO SENHA
		int senha = 0;
		while (true) {
			try {
				senha = Integer.parseInt(utils.lerConsole("|Digite uma senha de 4 numeros: "));
				if (String.valueOf(senha).length() == 4) {
					break;
				} else
					System.err.println("Senha deve ser igual a 4 numeros!");
			} catch (NumberFormatException e) {
				System.err.println("Sua senha deve conter apenas numeros");
				continue;
			}
		}
		return String.valueOf(senha);
	}

// ESCOLHER DATA DE VENCIMENTO E RETORNA
	private static String escolherDataVencimento() {
		Menus.exibeDatasVencimento();
		String op = utils.lerConsole("Digite a opcao de data de vencimento: ");
		if (op.equals("1")) {
			return "1";
		} else if (op.equals("2")) {
			return "5";
		} else if (op.equals("3")) {
			return "10";
		} else if (op.equals("4")) {
			return "15";
		}
		return "5";
	}

	private static void buscaOperacaoPix(String op) {
		switch (op) {
		case "1": {// EXIBE CHAVES E CADASTRA
			exibirChavesDisponiveis();
			exibirOpcoesChavesPixCadastro();
			menuPrincipal();
			break;
		}
		case "2": {// EXIBE CHAVES
			exibirChavesDisponiveis();
			break;
		}
		case "3": {// TRANSFERE VIA PIX
			String chavePix = utils.lerConsole("Digite a chave pix de destino: ");
			double valor = Double.parseDouble(utils.lerConsole("Digite o valor que deseja transferir: "));
			String[] resposta = PixBO.buscaETRansferePix(chavePix, valor, Menus.exibeOpcaoConta("Qual conta?"));
			System.out.println(resposta[0]);// Exibe resposta do control
			if (resposta[1].equals("0") || resposta[1].equals("2")) {
				menuPrincipal();
				break;
			}
		}
		case "4": {// DELETA CHAVES PIX
			exibirChavesDisponiveis();
			int chavePix = Integer.parseInt(utils.lerConsole("Digite o id de chave: "));
			utils.loading("\n       Deletando chave pix");
			if (PixBO.deletarChave(chavePix)) {
				System.out.println("           >>>Pix removido com sucesso!<<<    ");
				break;
			} else {
				System.err.println("           >>>O id do pix n�o existe!<<<    ");
			}

		}
		case "0": { // SAIR
			menuPrincipal();
		}
		default:
			System.out.println("Digite valores entre 0 e 4");
		}

	}

	// CPF,Email,Telefone,Aleatorio;
	private static void exibirOpcoesChavesPixCadastro() {
		while (true) {
			Menus.exibeTiposChavesPix();
			String op = utils.lerConsole("|Digite a opção: ");
			if (buscaOperacaoTipoPix(op)) {
				System.out.println("\n   >>Chave pix registrada com sucesso!<<  ");
				break;
			} else {
				System.out.println("\n   >>Erro na chave pix ou pix j� existe, Digite novamente!<<  ");
			}
		}
	}

//VERIFICA TIPO DE OPERACAO PIX E RETORNA MSG TOMANDO UMA A��O
	// int idCC, int idCP, TipoChavePix tipoChavePix, String cpf, boolean b
	// int tipoChavePix, String conteudoChave,int chave, boolean b
	private static boolean buscaOperacaoTipoPix(String op) {

		switch (op) {
		case "0": {
			return PixBO.cadastraChavePix(0, cpfConsole, true);
		}
		case "1": {
			String email = utils.lerConsole("Digite o email: ");
			return PixBO.cadastraChavePix(1, email, true);
		}
		case "2": {
			String telefone = utils.lerConsole("Digite o telefone: ");
			return PixBO.cadastraChavePix(2, telefone, true);
		}
		case "3": {
			return PixBO.cadastraChavePix(3, "", true);
		}
		default:
			System.out.println("Digite valores entre 0 e 3");
			return false;
		}

	}

	private static void exibirChavesDisponiveis() {
		List<String> textoCompleto = new ArrayList<String>();
		if (ContaBO.cc != null && ContaBO.cp != null) {
			textoCompleto = PixBO.exibirChavesPix(ContaBO.cc.getNumero(), ContaBO.cp.getNumero());
			// System.out.println(textoCompleto);
			UtilFormatConsole.writeConsole(textoCompleto, textoCompleto.size());
		} else if (ContaBO.cc != null) {
			textoCompleto = PixBO.exibirChavesPix(ContaBO.cc.getNumero(), "0");
			UtilFormatConsole.writeConsole(textoCompleto, textoCompleto.size());
		} else if (ContaBO.cp != null) {
			textoCompleto = PixBO.exibirChavesPix("0", ContaBO.cp.getNumero());
			UtilFormatConsole.writeConsole(textoCompleto, textoCompleto.size());
		}
	}

	// VERIFICA O BANCO SE CONTEM CONTA E EXIBE A INFORMA��O
	private static void buscaContaCadastrada(int op) {
		if (op == 0) {
			logado = true;
			menuPrincipal();// chama menu principal
		} else if (op == 1) {
			cadastrar();
		} else {
			System.out.println("\n|CPF ou senha incorretos!\n");

		}
	}

//EXIBE E SOLICITA EMAIL PARA RECUPERAR SENHA
	private static void exibirRecuperacaoSenha() {
		System.out.println("|-------  RECUPERACAO SENHA  -----|");
		while (true) {
			String emailRecuperar = utils.lerConsole("|Digite o email cadastrado: ");
			if (ClienteBO.recuperaSenha(emailRecuperar)) {
				System.out.println("\n\n|        Recuperacao de senha enviada com sucesso!        |\n\n");
				break;
			}
		}
		System.out.println("|_________________________________|");
	}

}