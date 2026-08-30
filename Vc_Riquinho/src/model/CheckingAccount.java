package model;

import java.math.BigDecimal;

public class CheckingAccount extends BaseAccount {

	public CheckingAccount(String id, BigDecimal balance) {
		super(id, balance);
	}
	
	public CheckingAccount() {
		super();
	}
	
}
