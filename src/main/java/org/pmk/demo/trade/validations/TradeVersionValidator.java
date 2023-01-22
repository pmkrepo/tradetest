/**
* Trade Version Validation
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
package org.pmk.demo.trade.validations;

import org.pmk.demo.trade.dao.TradeDao;
import org.pmk.demo.trade.exception.ValidationException;
import org.pmk.demo.trade.model.Trade;

public class TradeVersionValidator extends TradeValidator {
	
	public TradeVersionValidator(TradeValidator nextValidator) {
		super(nextValidator);
	}

	private static final String TRADE_VERSION_LOWER = "Trade with higher version exists";
	
	@Override
	protected void validateThis(Trade trade, TradeDao dao) throws ValidationException {
		Trade existingTrade = dao.getTradeByIdLatestVersion(trade.getTradeId());
		
		if (existingTrade != null && existingTrade.getTradeVersion() > trade.getTradeVersion()) {
			throw new ValidationException(TRADE_VERSION_LOWER);
		}
	}

}
