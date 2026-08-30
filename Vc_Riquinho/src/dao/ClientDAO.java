package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;
import java.sql.CallableStatement;

import model.Client;
import model.CorporateClient;
import model.FixedIncomeProduct;
import model.IndividualClient;
import model.InvestmentProduct;
import model.VariableIncomeProduct;
import model.AutoInvestmentAccount;
import model.BaseAccount;
import model.CdiAccount;
import model.CheckingAccount;

public class ClientDAO extends BaseDAO {
	
	public void cadastrarCliente (Client client){
		String sql = "{CALL CADASTRO_CLIENTE(?, ?, ?, ?, ?)}";
	
		try (Connection conn = getConnection();
			 CallableStatement stmt = conn.prepareCall(sql)) {
			stmt.setString(1, client.getName());
			stmt.setString(2, client.getEmail());
			stmt.setString(3, client.getSenha());
			
			// utiliza do polimorfismo para mandar o tipo exato que deve ser criado no banco
			// Preenchendo os campos "PF" ou "PJ" que serão utilizados para criação no banco
			if (client instanceof IndividualClient) {
				stmt.setString(4, "PF");
				stmt.setString(5, ((IndividualClient) client).getCpf());
				
			} else if (client instanceof CorporateClient) {
				stmt.setString(4, "PJ");
				stmt.setString(5, ((CorporateClient) client).getCnpj());
			}
			stmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Client realizarLogin(String nome, String senha) {
	    String sql = "{CALL BUSCA_CLIENTE(?, ?)}";
	    Client cl = null;
	    
	    try (Connection conn = getConnection();
	         CallableStatement stmt = conn.prepareCall(sql)){
	        
	        stmt.setString(1, nome);
	        stmt.setString(2, senha);
	        
	        try (ResultSet rs = stmt.executeQuery()){
	            if (rs.next()) {
	                String id = rs.getString("id");
	                String nomeCl = rs.getString("nome");
	                String email = rs.getString("email");
	                String senhaCl = rs.getString("senha");
	                String tipo = rs.getString("tipo_cliente");
	                
	                if ("PF".equalsIgnoreCase(tipo)) {
	                    String cpf = rs.getString("cpf");
	                    cl = new IndividualClient(id, nomeCl, email, senhaCl, cpf);
	                } else if ("PJ".equalsIgnoreCase(tipo)) {
	                    String cnpj = rs.getString("cnpj");
	                    cl = new CorporateClient(id, nomeCl, email, senhaCl, cnpj);
	                }
	            }
	        }

	        // Se encontrou o cliente, delega a busca das contas para um método separado
	        if (cl != null) {
	            buscarContasDoCliente(cl);
	        }
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return cl;
	}

	// Método auxiliar para isolar a lógica de contas
	public void buscarContasDoCliente(Client cl) {
	    String sqlContas = "{CALL BUSCAR_CONTAS_DO_CLIENTE(?)}";
	    
	    cl.getAccounts().clear();
	    
	    try (Connection conn = getConnection();
		         CallableStatement stmtContas = conn.prepareCall(sqlContas)){
	        stmtContas.setString(1, cl.getId());
	        
	        try (ResultSet rsContas = stmtContas.executeQuery()){
	            while (rsContas.next()) {
	                String id = rsContas.getString("conta_id");
	                BigDecimal balance = rsContas.getBigDecimal("balance");
	                String tipo_conta = rsContas.getString("tipo_conta");
	                
	                BaseAccount acc = null;
	                
	                if ("CORRENTE".equalsIgnoreCase(tipo_conta)) {
	                    acc = new CheckingAccount(id, balance);
	                    
	                } else if ("CDI".equalsIgnoreCase(tipo_conta)) {
	                    acc = new CdiAccount(id, balance);
	                
	                } else if ("AUTO_INVESTIMENTO".equalsIgnoreCase(tipo_conta)) {
	                    AutoInvestmentAccount autoAcc = new AutoInvestmentAccount(id, balance);
	                    
	                    // Delega a busca dos produtos para outro método, passando o id da CONTA e não do cliente!
	                    buscarProdutosAutoInvestimento(conn, autoAcc, id);
	                    
	                    acc = autoAcc;
	                }
	                
	                if (acc != null) {
	                    cl.setAccount(acc);
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	// Método auxiliar para isolar a lógica de produtos de investimento
	private void buscarProdutosAutoInvestimento(Connection conn, AutoInvestmentAccount autoAcc, String idConta) {
	    String sqlAutoInvestment = "{CALL BUSCAR_PRODUTOS_INVESTIMENTO(?)}";
	    
	    try (CallableStatement stmtAutoInvestment = conn.prepareCall(sqlAutoInvestment)){
	        // Correção importante: a procedure espera o id da conta, não do cliente
	        stmtAutoInvestment.setString(1, idConta);
	        
	        try (ResultSet rsAutoInvestment = stmtAutoInvestment.executeQuery()){
	            while (rsAutoInvestment.next()) {
	                String idInvestment = rsAutoInvestment.getString("id");
	                String nameInvestment = rsAutoInvestment.getString("investment_name");
	                String descriptionInvestment = rsAutoInvestment.getString("investment_description");
	                BigDecimal monthlyYield = rsAutoInvestment.getBigDecimal("monthlyYield");
	                String tipo_investimento = rsAutoInvestment.getString("tipo_investimento");
	                
	                InvestmentProduct investment = null;
	                
	                if ("VARIABLE".equalsIgnoreCase(tipo_investimento)) {
	                    investment = new VariableIncomeProduct(idInvestment, nameInvestment, descriptionInvestment, monthlyYield);
	                } else if ("FIXED".equalsIgnoreCase(tipo_investimento)) {
	                    int gracePeriod = rsAutoInvestment.getInt("gracePeriodDays");
	                    investment = new FixedIncomeProduct(idInvestment, nameInvestment, descriptionInvestment, monthlyYield, gracePeriod);
	                }
	                
	                if (investment != null) {
	                    autoAcc.setInvestment(investment);
	                }
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * método que salva as alterações feitas pelo cliente durante o tempo logado
	 * @param client
	 */
	public void saveClientChanges(Client client) {
	    if (client == null) return;
	    
	    // 1. Atualiza os dados cadastrais do cliente
	    String sqlClient = "{CALL ATUALIZAR_CLIENTE(?, ?, ?, ?)}";
	    try (Connection conn = getConnection();
	         CallableStatement stmt = conn.prepareCall(sqlClient)) {
	         
	        stmt.setString(1, client.getId());
	        stmt.setString(2, client.getName());
	        stmt.setString(3, client.getEmail());
	        stmt.setString(4, client.getSenha());
	        stmt.execute();
	    } catch (SQLException e) {
	        System.out.println("Erro ao atualizar dados do cliente: " + e.getMessage());
	    }
	    
	    // 2. Atualiza o saldo de cada conta vinculada
	    String sqlAccount = "{CALL ATUALIZAR_CONTA(?, ?)}";
	    for (BaseAccount acc : client.getAccounts()) {
	        try (Connection conn = getConnection();
	             CallableStatement stmt = conn.prepareCall(sqlAccount)) {
	             
	            stmt.setString(1, acc.getId());
	            stmt.setBigDecimal(2, acc.getBalance());
	            stmt.execute();
	        } catch (SQLException e) {
	            System.out.println("Erro ao atualizar a conta ID " + acc.getId() + ": " + e.getMessage());
	        }
	    }
	}
	
	/**
	 * metodo para cadastrar uma nova conta do cliente
	 * @param clientId
	 * @param tipoConta
	 * @return
	 */
	public boolean cadastrarConta(String clientId, String tipoConta) {
        String sql = "{CALL CADASTRAR_CONTA(?, ?)}";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
             
            stmt.setString(1, clientId);
            stmt.setString(2, tipoConta);
            
            stmt.execute();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro no DAO ao cadastrar conta: " + e.getMessage());
            return false;
        }
    }
}

