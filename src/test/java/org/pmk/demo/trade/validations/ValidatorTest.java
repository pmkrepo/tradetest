/**
* Unit Test for all TradeValidators and ValidationException
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/

package org.pmk.demo.trade.validations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pmk.demo.trade.dao.TradeDao;
import org.pmk.demo.trade.exception.ValidationException;
import org.pmk.demo.trade.model.Trade;

public class ValidatorTest {
	private static final String TRADE_MATURITY_DATE_LOWER = "Maturity Date of the Trade cannot be lower than the Current Date";
	private static final String TRADE_VERSION_LOWER = "Trade with higher version exists";
	private static final int TRADE_DAO_VERSION = 3;

	
	private TradeDao tradeDao;	
	Trade trade;
	
	@BeforeEach
	//Create TradeService and InMemoryTradeDao and first Trade object
	public void setUp () {		
		tradeDao = new TradeDao() {
			public void addTrade(Trade theTrade) {}
			public void updateTrade(Trade oldTrade, Trade newTrade) {}
			public Collection<Trade> getAllTrades() {return null;}
			public Trade getTradeByIdLatestVersion(String tradeId) {
				
				Trade existingTrade = Trade.createTrade(tradeId, TRADE_DAO_VERSION,
						trade.getCounterPartyId(), trade.getBookId(), 
						trade.getCreatedDateTimeStamp(), trade.getMaturityDateTimeStamp(), trade.isExpired());
				return existingTrade;
			}
			public Collection<Trade> findTradesLessThanMaturityDate(long maturityDateTs) {return null;}			
		};		
		
		trade = Trade.createTrade("T1", TRADE_DAO_VERSION - 1,
				"CP-1", "B13", 
				System.currentTimeMillis(), System.currentTimeMillis() + 20000, false);
	}
	
	@AfterEach
	//Reset before next test
	public void tearDown () {
		tradeDao = null;
		trade = null;
	}
	
    @Test
    //TradeMaturityDateValidator
    public void testTradeMaturityDateValidatorException() {
		trade.setMaturityDateTimeStamp(	System.currentTimeMillis() - 20000);
	    Throwable exception = assertThrows( ValidationException.class,
				   ()->{new TradeMaturityDateValidator(null).validate(trade, tradeDao);
		    } );		   
		 assertEquals(exception.getMessage(), TRADE_MATURITY_DATE_LOWER);       
    }
    
    @Test
    //TradeMaturityDateValidator
    public void testTradeMaturityDateValidatorNoException() {
		trade.setMaturityDateTimeStamp(	System.currentTimeMillis() + 20000);
	    assertDoesNotThrow( 
				   ()->{new TradeMaturityDateValidator(null).validate(trade, tradeDao);
		    } );		   
    }
    
    @Test
    //Test TradeVersionValidator
    public void testTradeVersionValidatorLowerVersion() {
    	trade.setTradeVersion(TRADE_DAO_VERSION - 1); //Version less than DAO version
	    Throwable exception = assertThrows( ValidationException.class,
				   ()->{new TradeVersionValidator(null).validate(trade, tradeDao);
		    } );		   
		 assertEquals(exception.getMessage(), TRADE_VERSION_LOWER);       
    }
    
    @Test
    //TradeMaturityDateValidator
    public void testTradeVersionValidatorSameVersion() {
    	trade.setTradeVersion(TRADE_DAO_VERSION); //Same version as in DAO
	    assertDoesNotThrow( 
				   ()->{new TradeVersionValidator(null).validate(trade, tradeDao);
		    } );		   
    }
    
    @Test
    //TradeMaturityDateValidator
    public void testtestTradeVersionValidatorHigherVersion() {
    	trade.setTradeVersion(TRADE_DAO_VERSION + 1); //Version higher than DAO version
	    assertDoesNotThrow( 
				   ()->{new TradeVersionValidator(null).validate(trade, tradeDao);
		    } );		   
    }
}
