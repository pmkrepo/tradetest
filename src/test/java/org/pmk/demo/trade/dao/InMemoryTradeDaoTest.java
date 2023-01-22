/**
* Unit Test for all TradeValidators and ValidationException
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/

package org.pmk.demo.trade.dao;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pmk.demo.trade.model.Trade;

public class InMemoryTradeDaoTest {
	private TradeDao tradeDao;	
	Trade tradeT31;
	Trade tradeT32;
	Trade tradeT11;
	Trade tradeT22;
	Trade tradeT22_updated;
	
	
	@BeforeEach
	//Create TradeService and InMemoryTradeDao and first Trade object
	public void setUp () {		
		tradeDao = new InMemoryTradeDao();
		
		tradeT31 = Trade.createTrade("T3", 1, "CP-1", "B59", System.currentTimeMillis(), System.currentTimeMillis()+25000, false);
		tradeT32 = Trade.createTrade("T3", 2, "CP-4", "B40", System.currentTimeMillis(), System.currentTimeMillis()+10000, false);
		tradeT11 = Trade.createTrade("T1", 1, "CP-1", "B40", System.currentTimeMillis(), System.currentTimeMillis()+23000, false);
		tradeT22 = Trade.createTrade("T2", 2, "CP-3", "B33", System.currentTimeMillis(), System.currentTimeMillis()+30000, true);
		tradeT22_updated = Trade.createTrade("T2", 2, "CP-5", "B65", System.currentTimeMillis(), System.currentTimeMillis()+30000, false);
	}
	
	@AfterEach
	//Reset before next test
	public void tearDown () {
		tradeDao = null;
		tradeT31 = null;
		tradeT32 = null;
		tradeT11 = null;
		tradeT22 = null;
		tradeT22_updated = null;
	}
	
    @Test
    //Test addTrade and getAll trades
    public void testAddTradeAndGetAllTrades() {
    	tradeDao.addTrade(tradeT31);
    	tradeDao.addTrade(tradeT11);
		Collection<Trade> trades = tradeDao.getAllTrades();
		assertTrue(trades.contains(tradeT31));      
		assertTrue(trades.contains(tradeT11));  
    }
    
    @Test
    //Test addTrade and getAll trades
    public void testUpdateTrade() {    
    	tradeDao.addTrade(tradeT22);
    	tradeDao.updateTrade(tradeT22, tradeT22_updated);
	
		//boolean is passed by the refernce to the function so need a wrapper
		//As Boolean is immutable, need to use AtomicBoolean
		AtomicBoolean hasOriginal = new AtomicBoolean(false);
		AtomicBoolean hasNew = new AtomicBoolean(false);
		
		//Check if original or updated trade is present
		//Original Trade should not be present, updated Trade should be present
		Collection<Trade> trades = tradeDao.getAllTrades();
		trades.forEach((trade) -> {
			if (trade.equals(tradeT22_updated)) {
				hasNew.set(true);
			} else if (trade.equals(tradeT22)) {
				hasOriginal.set(true);
			}
		} );    		    		
		assertTrue(hasNew.get());
		assertFalse(hasOriginal.get());	
    }
}
