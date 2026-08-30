package model;

import java.math.BigDecimal;

public abstract class BaseAccount implements Account {
	protected String id;
	protected BigDecimal balance;
	
	/**
	 * 
	 * @param id
	 * @param balance
	 * @param client
	 */
	public BaseAccount(String id, BigDecimal balance) {
		
		this.id = id;
		this.balance = balance;
	}
	
	public BaseAccount() {}
	
	
	public BigDecimal calculateYield(int days) {
		return BigDecimal.ZERO;
	}
	public BigDecimal calculateServiceFee(BigDecimal earnedYield) {
		return BigDecimal.ZERO;
	}
	
	public String getId() {
		return id;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
    public BigDecimal getBalance() {
    	return balance;
    }
	
	@Override
    public String getDetails() {
    	return "Tipo: " + getClass().getSimpleName();
    }
}
