package model;

public class IndividualClient extends Client {
	private String cpf;

	
	/**
	 * 
	 * @param name
	 * @param email
	 * @param cpf
	 */
	public IndividualClient(String name, String email, String senha, String cpf) {
		super(name, email, senha);
		this.cpf = cpf;
	}
	
	// temporario para testes
	public IndividualClient(String id, String name, String email, String senha, String cpf) {
		super(id, name, email, senha);
		this.cpf = cpf;
	}

	public String getCpf() {
		return cpf;
	}
	
	
}
