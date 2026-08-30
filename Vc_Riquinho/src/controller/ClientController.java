package controller;

import java.math.BigDecimal;

import dao.ClientDAO;
import model.AutoInvestmentAccount;
import model.BaseAccount;
import model.CdiAccount;
import model.CheckingAccount;
import model.Client;
import model.CorporateClient;
import model.IndividualClient;

public class ClientController {

	private ClientDAO clientDAO;
	
	public ClientController() {
		this.clientDAO = new ClientDAO();
	}
	
	
	/**
	 * Cria o cliente no banco de dados
	 * @param nome
	 * @param email
	 * @param senha
	 * @param documento
	 * @param tipo
	 */
	public void registerNewClient(String nome, String email, String senha, String documento, String tipo) {
		
		Client cl = null;
		
		if (tipo.equalsIgnoreCase("PF")) {
			
			cl = new IndividualClient(nome, email, senha, documento);
		
		} else if (tipo.equalsIgnoreCase("PJ")) {
			
			cl = new CorporateClient(nome, email, senha, documento);
		} else {
			System.out.println("Erro: Cliente inválido");
			return;
		}
		
		clientDAO.cadastrarCliente(cl);
		System.out.println("Cliente processado e cadastrado no bando de dados");
	}
	
	
	/**
	 * Faz o login instanciando e populando o cliente
	 * @param nome
	 * @param senha
	 * @return
	 */
	public Client clientLogin(String nome, String senha) {
		
		if (nome == null || nome.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
			System.out.println("Os campos nome e senha não podem estar vazios");
			return null;
		}
		
		Client clienteLogado = clientDAO.realizarLogin(nome, senha);
		
		if (clienteLogado != null) {
			System.out.println("\nLogin realizado com sucesso, bem vindo(a), " + clienteLogado.getName());
			
			// utilizando o polimorfismo para verificação de qual cliente foi instanciado
			if (clienteLogado instanceof IndividualClient) {
				System.out.println("Tipo de cadastro: pessoa física");
			} else if (clienteLogado instanceof CorporateClient) {
				System.out.println("Tipo de cadastro: pessoa jurídica");
			}
			return clienteLogado;
			
		} else {
			System.out.println("\nErro: cliente não encontrado");
			return null;
		} 
	}

	
	
	/**
	 * Método para printar na tela todas as contas do cliente
	 * @param cli
	 */
	public void listAccounts(Client cli) {
		clientDAO.buscarContasDoCliente(cli);
		for (BaseAccount acc : cli.getAccounts()) {
			System.out.println("| ID: " + acc.getId() + " " + acc.getDetails());
		}
	}
	
	/**
	 * Método para retornar o saldo de uma das contas do cliente
	 * @param cli
	 * @param id
	 */
	public void consultaSaldo(Client cli, String id) {
		
		for (BaseAccount acc : cli.getAccounts()) {
			if (acc.getId().equalsIgnoreCase(id)) {
				System.out.println("Saldo: " + acc.getBalance());
				return;
			} 
		}
		System.out.println("Conta não encontrada");
	}
	
	/**
	 * Com base no cliente, id da conta e quantidade de dias, calcula a taxa de servico do cliente
	 * @param cli
	 * @param id
	 * @param dias
	 */
	public void calcularRendimento(Client cli, String id, int dias) {
		
		// procurando a conta 
		for (BaseAccount acc : cli.getAccounts()) {
			if (acc.getId().equalsIgnoreCase(id)) {
				
				// aplicando polimorfismo para aplicar o método segundo o tipo da conta em instancia
				if (acc instanceof CheckingAccount) {
					System.out.println("Rendimento: " + ((CheckingAccount) acc).calculateYield(dias));
				} else if (acc instanceof CdiAccount) {
					System.out.println("Rendimento: " + ((CdiAccount) acc).calculateYield(dias));
				} else if (acc instanceof AutoInvestmentAccount) {
					System.out.println("Rendimento: " + ((AutoInvestmentAccount) acc).calculateYield(dias));
				}
				
				
				return;
			} 
		}
		System.out.println("Conta não encontrada");
	}
	
	/**
	 * parecida com o método que calcula o rendimento, pois a taxa é calculada com base em um rendimento
	 * @param cli
	 * @param idConta
	 * @param dias
	 */
	public void consultaTaxaServico(Client cli, String idConta, int dias) {
		for (BaseAccount acc : cli.getAccounts()) {
			if (acc.getId().equalsIgnoreCase(idConta)) {
				if (acc instanceof CheckingAccount) {
					System.out.println("Taxa de serviço: " + ((CheckingAccount) acc).calculateServiceFee(((CheckingAccount) acc).calculateYield(dias)));
				} else if (acc instanceof CdiAccount) {
					System.out.println("Taxa de serviço: " + ((CdiAccount) acc).calculateServiceFee(((CdiAccount) acc).calculateYield(dias)));
				} else if (acc instanceof AutoInvestmentAccount) {
					System.out.println("Taxa de serviço: " + ((AutoInvestmentAccount) acc).calculateServiceFee(((AutoInvestmentAccount) acc).calculateYield(dias)));
				}
			}
		}
	}
	
	/**
	 * realiza deposito atualizando o valor do saldo na conta do cliente
	 * @param cli
	 * @param idConta
	 * @param montante
	 */
	public void realizarDeposito(Client cli, String idConta, BigDecimal montante) {
	    for (BaseAccount acc : cli.getAccounts()) {
	        if (acc.getId().equalsIgnoreCase(idConta)) {
	            // Reatribui o resultado da soma à variável
	            BigDecimal newBalance = acc.getBalance().add(montante);
	            
	            acc.setBalance(newBalance);
	            System.out.println("Valor depositado com sucesso: " + montante);
	            break;
	        }
	    }
	}
	
	
	/**
	 * 
	 * @param cl
	 * @param novoNome
	 * @param novoEmail
	 * @param novaSenha
	 */
	public void updateClientData(Client cl, String novoNome, String novoEmail, String novaSenha) {
	    if (cl == null) {
	        System.out.println("Erro: Nenhum cliente logado.");
	        return;
	    }
	    
	    if (novoNome != null && !novoNome.trim().isEmpty()) {
	        cl.setName(novoNome);
	    }
	    
	    if (novoEmail != null && !novoEmail.trim().isEmpty()) {
	        cl.setEmail(novoEmail);
	    }
	    
	    if (novaSenha != null && !novaSenha.trim().isEmpty()) {
	        cl.setSenha(novaSenha);
	    }
	    
	    System.out.println("\nDados cadastrais atualizados com sucesso!");
	}
	
	/**
	 * faz o relacionamento entre a view e DAO para atualizar os dados do cliente apos o logout
	 * @param client
	 */
	public void updateClientAndAccounts(Client client) {
	    if (client == null) {
	        System.out.println("Nenhum cliente logado para salvar.");
	        return;
	    }
	    clientDAO.saveClientChanges(client);
	    System.out.println("\nAlterações salvas com sucesso no banco de dados!");
	}
	
	
	/**
	 * registra nova conta ao cliente
	 * @param cl
	 * @param tipoConta
	 * @return
	 */
	public boolean registerNewAccount(Client cl, int tipoConta) {
        if (cl == null) {
            System.out.println("Erro: Nenhum cliente logado.");
            return false;
        }

        String tipoString = "";
        switch (tipoConta) {
            case 1: tipoString = "CORRENTE"; break;
            case 2: tipoString = "CDI"; break;
            case 3: tipoString = "AUTO_INVESTIMENTO"; break;
            default: 
                System.out.println("Tipo de conta inválido.");
                return false;
        }
        
        // Delega a persistência exclusivamente para o DAO
        boolean sucesso = clientDAO.cadastrarConta(cl.getId(), tipoString);
        
        if (sucesso) {
            System.out.println("Conta cadastrada com sucesso no banco de dados!");
            // Opcional: Aqui você pode recarregar as contas do cliente na memória para sincronizar
            return true;
        }
        
        System.out.println("Não foi possível cadastrar a conta.");
        return false;
    }
}
 