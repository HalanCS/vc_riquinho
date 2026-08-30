package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class CdiAccount extends BaseAccount {
	private static final BigDecimal CDI_ANNUAL_REFERENCE = BigDecimal.valueOf(0.12);
	private static final BigDecimal CDI_SERVICE_FEE = BigDecimal.valueOf(0.0007);
	
	public CdiAccount(String id, BigDecimal balance) {
        super(id, balance); 
    }
	
	public CdiAccount() {
		super();
	}

	public BigDecimal calculateYield(int days) {
	    BigDecimal diasNoAno = new BigDecimal("365");
	    BigDecimal trintaDias = new BigDecimal("30");
	    
	    BigDecimal dailyCdi = CDI_ANNUAL_REFERENCE.divide(diasNoAno, 10, RoundingMode.HALF_EVEN);
	    
	    BigDecimal fracaoMes = BigDecimal.ONE.divide(trintaDias, 10, RoundingMode.HALF_EVEN);
	    BigDecimal effectiveDailyCdi = dailyCdi.multiply(fracaoMes);
	    
	    BigDecimal base = BigDecimal.ONE.add(effectiveDailyCdi);
	    
	    BigDecimal powResult = base.pow(days, MathContext.DECIMAL128);
	    
	    BigDecimal fatorRendimento = powResult.subtract(BigDecimal.ONE);
	    
	    return this.balance.multiply(fatorRendimento).setScale(2, RoundingMode.HALF_EVEN);
	}
	
	@Override
	public BigDecimal calculateServiceFee(BigDecimal earnedYield) {
	    return earnedYield.multiply(CDI_SERVICE_FEE).setScale(2, RoundingMode.HALF_EVEN);
	}
}
