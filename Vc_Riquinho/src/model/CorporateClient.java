package model;

public class CorporateClient extends Client {
	private String cnpj;

	
	

	/**
	 * 
	 * @param name
	 * @param email
	 * @param cnpj
	 */
	public CorporateClient(String name, String email, String senha, String cnpj) {
		super(name, email, senha);
		this.cnpj = cnpj;
	}

	// temporario para teste
	public CorporateClient(String id, String name, String email, String senha, String cnpj) {
		super(id, name, email, senha);
		this.cnpj = cnpj;
	}
	
	
	public String getCnpj() {
		return cnpj;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	
}
