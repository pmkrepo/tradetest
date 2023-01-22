/**
* Trade DAO interface
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
package org.pmk.demo.trade.dao;

import java.util.Collection;

import org.pmk.demo.trade.model.Trade;

public interface TradeDao {
	public void addTrade(Trade theTrade);
	public void updateTrade(Trade oldTrade, Trade newTrade);
	public Collection<Trade> getAllTrades();
	public Trade getTradeByIdLatestVersion (String tradeId);
	Collection<Trade> findTradesLessThanMaturityDate(long maturityDateTs);
}
