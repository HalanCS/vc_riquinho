package model;

import java.math.BigDecimal;

public class VariableIncomeProduct extends InvestmentProduct {

	private BigDecimal expectedMonthlyYield;
    public VariableIncomeProduct(String id, String name, String description, BigDecimal monthlyYield) {
        super(id, name, description, monthlyYield);
    }
}