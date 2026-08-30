package model;

import java.math.BigDecimal;

public abstract class InvestmentProduct {
    protected String id;
    protected String name;
    protected String description;
    protected BigDecimal monthlyYield; 
    
    public InvestmentProduct(String id, String name, String description, BigDecimal monthlyYield) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.monthlyYield = monthlyYield;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getMonthlyYield() { return monthlyYield; }
}