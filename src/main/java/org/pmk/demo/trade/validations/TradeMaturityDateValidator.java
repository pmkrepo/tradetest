/**
* Trade Maturity Date Validation
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
package org.pmk.demo.trade.validations;

import org.pmk.demo.trade.dao.TradeDao;
import org.pmk.demo.trade.exception.ValidationException;
import org.pmk.demo.trade.model.Trade;

public class TradeMaturityDateValidator extends TradeValidator {
	
	private static final String TRADE_MATURITY_DATE_LOWER = "Maturity Date of the Trade cannot be lower than the Current Date";

	public TradeMaturityDateValidator(TradeValidator nextValidator) {
		super(nextValidator);
	}
	
	@Override
	protected void validateThis(Trade trade, TradeDao dao) throws ValidationException {
		if (System.currentTimeMillis() > trade.getMaturityDateTimeStamp()) {
			throw new ValidationException(TRADE_MATURITY_DATE_LOWER);
		}
	}

}
