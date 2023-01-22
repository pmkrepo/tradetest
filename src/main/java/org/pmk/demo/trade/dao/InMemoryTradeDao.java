/**
* Trade DAO implementation for In-memory data
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
package org.pmk.demo.trade.dao;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.pmk.demo.trade.model.Trade;

public class InMemoryTradeDao implements TradeDao {
	
	//Used TreeSet as it sorts date using Comparable or Comparator
	//Trade bean implements compareTo which helps sorting data using tradeId (ascending) and tradeVersion (descending)
	//We can also use TreeMap with TradeKey (tradeId, tradeVersion) as key and Trade as value
	Set<Trade> tradeData = new TreeSet<>();

	@Override
	//Add a trade to the database (here - in-memory TreeSet)
	public synchronized void addTrade(Trade theTrade) {
		tradeData.add(theTrade);		
	}

	@Override
	//Update the existing trade in the database (here - in-memory TreeSet) 
	public synchronized void updateTrade(Trade oldTrade, Trade newTrade) {
		tradeData.remove(oldTrade);
		tradeData.add(newTrade);		
	}

	@Override
	//Return copy of the data, Do not expose original data store outside of DAO.
	public synchronized Collection<Trade> getAllTrades() {
		TreeSet<Trade> copyData = new TreeSet<>();
		copyData.addAll(tradeData);
		return copyData;
	}

	@Override
	//Get the latest trade by version Id from the database (here - in-memory TreeSet) 
	public synchronized Trade getTradeByIdLatestVersion(String tradeId) {		
		//As TreeSet data structure is used and it is already sorted, findFrist will provide the latest version
		Trade returnTrade = tradeData.stream().filter( trade -> trade.getTradeId().equals(tradeId) ).findFirst().orElse(null);				
		return returnTrade;
	}

	@Override
	//Find trades where maturity date is less than input parameter
	public synchronized Collection<Trade> findTradesLessThanMaturityDate(long maturityDateTs) {		
		//As TreeSet data structure is used and it is already sorted, findFrist will provide the latest version
		Set<Trade> returnList = tradeData.stream().filter( trade -> trade.getMaturityDateTimeStamp() < maturityDateTs ).collect(Collectors.toSet());				
		return returnList;
	}
}
