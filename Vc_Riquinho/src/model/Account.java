package model;

import java.math.BigDecimal;

public interface Account {
	/**
	 * Calculate the yield of a account having the quantity of days as parameter
	 * @param days
	 * @return
	 */
	BigDecimal calculateYield(int days);
	
	/**
	 * Calculate the service fee having the earned yield as parameter
	 * @param earnedYield
	 * @return
	 */
	BigDecimal calculateServiceFee(BigDecimal earnedYield);
	
	String getDetails();

}
