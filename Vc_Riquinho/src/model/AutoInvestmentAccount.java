package model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class AutoInvestmentAccount extends BaseAccount {
    // this account have some Investment product
    // started as a single investment, changed to list to follow the requirements
    // to guarantee that the investment is unique, changed to Set
    private Set<InvestmentProduct> investments = new HashSet<>();
    
    public AutoInvestmentAccount(String id, BigDecimal balance) {
        super(id, balance);
    }
    
    public AutoInvestmentAccount() {
    	super();
    }
    
    /**
     * Verify duplicate entries of an investment product by id
     * @param iv
     * @return
     */
    public Boolean verifyDuplicateEntry(InvestmentProduct iv) {
        for (InvestmentProduct existing : investments) {
            if (existing.getId().equals(iv.getId())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    /**
     * Insert a new investment product to client throwing duplicates
     * @param product
     * @throws DuplicatedInvestmentException
     */
    public void setInvestment(InvestmentProduct product) throws DuplicatedInvestmentException {
        if (verifyDuplicateEntry(product)) {
            throw new DuplicatedInvestmentException();
        }
        this.investments.add(product);
    }
    
    @Override
    public BigDecimal calculateYield(int days) {
        // Inicializa o acumulador de rendimentos com BigDecimal.ZERO
        BigDecimal totalYieldInDays = BigDecimal.ZERO;
        BigDecimal trintaDias = new BigDecimal("30");
        
        // going through all investments products and calculating the yield
        for (InvestmentProduct investment : investments) {
            if (investment == null) {
                continue; 
            }
            
            BigDecimal monthlyYield = investment.getMonthlyYield(); 
            
            BigDecimal dailyYield = monthlyYield.divide(trintaDias, 10, RoundingMode.HALF_EVEN);
            
            BigDecimal base = BigDecimal.ONE.add(dailyYield);
            
            BigDecimal powResult = base.pow(days, MathContext.DECIMAL128);
            
            BigDecimal fatorRendimento = powResult.subtract(BigDecimal.ONE);
            
            BigDecimal yieldForThisInvestment = this.balance.multiply(fatorRendimento);
            
            totalYieldInDays = totalYieldInDays.add(yieldForThisInvestment);
        }
        
        // Retorna o valor total arredondado para 2 casas decimais (centavos)
        return totalYieldInDays.setScale(2, RoundingMode.HALF_EVEN);
    }
    
    @Override
    public BigDecimal calculateServiceFee(BigDecimal earnedYield) {
        return BigDecimal.ZERO;
    }
    

}