/**
* Trade Validator Chain of Responsibility pattern
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
package org.pmk.demo.trade.validations;

import org.pmk.demo.trade.dao.TradeDao;
import org.pmk.demo.trade.exception.ValidationException;
import org.pmk.demo.trade.model.Trade;

public abstract class TradeValidator {
	private TradeValidator nextValidator;
	
	public TradeValidator(TradeValidator nextValidator) {
		this.nextValidator = nextValidator;
	}	
	
	public final void validate (Trade trade, TradeDao dao) throws ValidationException {
		validateThis (trade, dao);
		
		if (nextValidator != null) {
			nextValidator.validate(trade, dao);
		}		
	}
	
	protected abstract void validateThis(Trade trade, TradeDao dao) throws ValidationException;
}
