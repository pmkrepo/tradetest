package org.pmk.demo.trade.service;
/**
* Main Trade Service which is used to interact with Trade management system
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.pmk.demo.trade.dao.TradeDao;
import org.pmk.demo.trade.exception.ValidationException;
import org.pmk.demo.trade.model.Trade;
import org.pmk.demo.trade.validations.TradeValidator;


public class TradeService {
	private TradeValidator validatorChain;
	private TradeDao tradeDao;
	private ScheduledExecutorService scheduler; 

	//Constructor with TradeDao dependency injection
	//TradeService is de-coupled from TradeDao and Validation chain
	//If we use spring then we can use @Autowired for dependency injection
	public TradeService(TradeDao tradeDao, TradeValidator validatorChain) {
		this.tradeDao = tradeDao;
		this.validatorChain = validatorChain;	
	}
	
	//
	public void addNewTrade(Trade trade) throws ValidationException {
		validatorChain.validate(trade, tradeDao);
		
		Trade existingTrade = tradeDao.getTradeByIdLatestVersion(trade.getTradeId());
		if (existingTrade != null && existingTrade.getTradeVersion() == trade.getTradeVersion()) {
			tradeDao.updateTrade(existingTrade, trade);		
		} else {
			tradeDao.addTrade(trade);
		}
	}
	
	public Collection<Trade> getTrades () {
		return tradeDao.getAllTrades();
	}
	
	public void updateTradeExpiry() {
		Collection<Trade> expiredTradeList = tradeDao.findTradesLessThanMaturityDate(System.currentTimeMillis()+20000);
		expiredTradeList.forEach(trade -> trade.setExpired(true));		
	}
	
	//When integrated with Spring, @Scheduled can be used
	public void startTradeExpiryScheduler(long firstRunSeconds, long afterEverySeconds) {				
		stopTradeExpiryScheduler();
		scheduler = Executors.newScheduledThreadPool(1);            
		scheduler.scheduleAtFixedRate(
			() -> updateTradeExpiry(),
			firstRunSeconds,
			afterEverySeconds,
		    TimeUnit.SECONDS);		
	}
	
	public void stopTradeExpiryScheduler() {				
		if (scheduler != null) {
			scheduler.shutdown();
			try {
				scheduler.awaitTermination(1000, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				//Ignore
			}
		}
	}
}
