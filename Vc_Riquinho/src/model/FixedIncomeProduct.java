package model;

import java.math.BigDecimal;

public class FixedIncomeProduct extends InvestmentProduct {
    private int gracePeriodDays; 

    public FixedIncomeProduct(String id, String name, String description, BigDecimal monthlyYield, int gracePeriodDays) {
        super(id, name, description, monthlyYield);
        this.gracePeriodDays = gracePeriodDays;
    }

    public int getGracePeriodDays() {
        return gracePeriodDays;
    }
}