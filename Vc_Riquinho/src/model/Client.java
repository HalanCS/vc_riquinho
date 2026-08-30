package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public abstract class Client {
    protected String id;
    protected String name;
    protected String email;
    protected String senha;
    protected List<BaseAccount> accounts = new ArrayList<>();
    
    
    // this constructor is used to insert in data base
    public Client(String name, String email, String senha) {
        this.name = name;
        this.email = email;
        this.senha = senha;
    }
    
    
    // constructor to populate with resultSet
    public Client(String id, String name, String email, String senha, List<BaseAccount> accounts) {
        this.id = id;
    	this.name = name;
        this.email = email;
        this.senha = senha;
        this.accounts = accounts;
    }
    
    // temporario para testes
    public Client(String id, String name, String email, String senha) {
        this.id = id;
    	this.name = name;
        this.email = email;
        this.senha = senha;
    }


	public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
    	return senha;
    }
    
    public void setSenha(String senha) {
    	this.senha = senha;
    }

    public List<BaseAccount> getAccounts() {
        return accounts;
    }
    


    public void setAccount(BaseAccount account) {
        this.accounts.add(account);
    }
    
    public void deleteClient() {

    }
    
    
}