/**
* Trade Integration Test
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/

package org.pmk.demo.trade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.pmk.demo.trade.dao.InMemoryTradeDao;
import org.pmk.demo.trade.exception.ValidationException;
import org.pmk.demo.trade.model.Trade;
import org.pmk.demo.trade.service.TradeService;
import org.pmk.demo.trade.validations.TradeMaturityDateValidator;
import org.pmk.demo.trade.validations.TradeVersionValidator;


public class TreadeIntegrationTest {

		private static final String TRADE_MATURITY_DATE_LOWER = "Maturity Date of the Trade cannot be lower than the Current Date";
		private static final String TRADE_VERSION_LOWER = "Trade with higher version exists";
		private static final int THE_TRADE_VERSION = 2;
	
		private TradeService tradeService;	
		Trade theTrade;
		
		@BeforeEach
		//Create TradeService and InMemoryTradeDao and first Trade object
		public void setUp () {
			//If we use spring then dependency injection will en automatic, but as we are not using spring, need to inject the dependencies
			//As this is a test assignment, we are using InMemoryTradeDao where data will be in-memory'
			//In actual project DAO can interact with DB for presistance and scale
			tradeService = new TradeService(new InMemoryTradeDao()
							, new TradeMaturityDateValidator(new TradeVersionValidator(null)));	
    		theTrade = Trade.createTrade("T1", THE_TRADE_VERSION, "CP-1", "B13", 
    						System.currentTimeMillis(), System.currentTimeMillis() + 20000, false);
		}
		
		@AfterEach
		//Reset before next test
		public void tearDown () {
			tradeService = null;
			theTrade = null;
		}

		@Test
	    //Test successful addition of new trade
	    public void testAddSuccess() throws ValidationException {
			//Add a first trade
    		tradeService.addNewTrade(theTrade);   		
    		Collection<Trade> trades = tradeService.getTrades();
    		assertTrue(trades.contains(theTrade));						        
	    }
		
		@Test
	    //Test ValidationException when trade being added has maturity date less than the current date
	    public void testMaturityDateValidation() {	    
			//Set maturity date to be lower than current date and try to add a Trade
    		theTrade.setMaturityDateTimeStamp(	System.currentTimeMillis() - 20000);
     	    Throwable exception = assertThrows( ValidationException.class,
    				   ()->tradeService.addNewTrade(theTrade)  );   		   
    	    assertEquals(exception.getMessage(), TRADE_MATURITY_DATE_LOWER);					        
	    }
		
		@Test
		//Test ValidationException when trade being added has version less than existing version
	    public void testTradeVersionValidation() throws ValidationException {	    	
			//Add first trade
    		tradeService.addNewTrade(theTrade);
    		
    		//Create another Trade with lower version  		
    		Trade otherTrade = Trade.createTrade(theTrade.getTradeId(), THE_TRADE_VERSION - 1, "CP-1", "B13"
    								,System.currentTimeMillis(), System.currentTimeMillis() + 20000, false);
    		    		
    	    Throwable exception = assertThrows( ValidationException.class,
 				   ()->tradeService.addNewTrade(otherTrade)  );
 		   
    	    assertEquals(exception.getMessage(), TRADE_VERSION_LOWER);					        
	    }
		
		
		@Test
	    //Add trade with version same as biggest version, it should update the existing trade
	    public void testTradeVersionSame() throws ValidationException {
			//Add first trade
    		tradeService.addNewTrade(theTrade);
    		
    		//Create another Trade with version same as first trade
    		//But change some other filelds like CounterPartyId and BookId
    		Trade otherTrade = Trade.createTrade(theTrade.getTradeId(), THE_TRADE_VERSION, "CP-2", "B-14", 
    								System.currentTimeMillis(), System.currentTimeMillis() + 20000, false);
    		tradeService.addNewTrade(otherTrade);    		
    		
    		//boolean is passed by the refernce to the function so need a wrapper
    		//As Boolean is immutable, need to use AtomicBoolean
    		AtomicBoolean hasOriginal = new AtomicBoolean(false);
    		AtomicBoolean hasNew = new AtomicBoolean(false);
    		
    		//Check if original or updated trade is present
    		//Original Trade should not be present, updated Trade should be present
    		Collection<Trade> trades = tradeService.getTrades();
    		trades.forEach((trade) -> {
    			if (trade.equals(otherTrade)) {
    				hasNew.set(true);
    			} else if (trade.equals(trade)) {
    				hasOriginal.set(true);
    			}
    		} );    		    		
    		assertTrue(hasNew.get());
    		assertFalse(hasOriginal.get());					        
	    }
		
		@Test
	    //TEst that Trade data is sorted by tradeId (ascending) and then tradeVersion (descending)
	    public void testTradeSorting() throws ValidationException {	 
			Trade tradeT31 = Trade.createTrade("T3", 1, "CP-1", "B59", 
								System.currentTimeMillis(), System.currentTimeMillis()+25000, false);
			Trade tradeT32 = Trade.createTrade("T3", 2, "CP-4", "B40", 
								System.currentTimeMillis(), System.currentTimeMillis()+10000, false);
			Trade tradeT11 = Trade.createTrade("T1", 1, "CP-1", "B40", 
								System.currentTimeMillis(), System.currentTimeMillis()+23000, false);
			Trade tradeT22 = Trade.createTrade("T2", 2, "CP-3", "B33", 
								System.currentTimeMillis(), System.currentTimeMillis()+30000, true);
						
    		tradeService.addNewTrade(tradeT31);	    	
    		tradeService.addNewTrade(tradeT32);	
    		tradeService.addNewTrade(tradeT11);
    		tradeService.addNewTrade(tradeT22);
    		
    		String expectedToString = "["
    				+ tradeT11 + ", "	//T1 is smallest trade id
    				+ tradeT22 + ", "	
    				+ tradeT32 + ", "	//T3 Version 2 is higher
    				+ tradeT31 + "]";   //T2 Version 1 is lower 
    		
    		Collection<Trade> trades = tradeService.getTrades();   		
    		assertEquals(expectedToString, trades.toString());		        
	    }
		
		@Test
	    //Test trade expiry scheduler, if maturity date time has passed, trade will automatically be marked as expired
	    public void testTradeExpirty() throws ValidationException, InterruptedException {	
			//Add a first trade with expiry as false, its maturity is few millsecond more than current timestamp for the test
    		theTrade.setMaturityDateTimeStamp(	System.currentTimeMillis() + 20000);
    		tradeService.addNewTrade(theTrade);
    		tradeService.startTradeExpiryScheduler(1, 10); //Start the scheduler
    		Thread.sleep(TimeUnit.SECONDS.toMillis(2)); //Wait couple of seconds for scheduler to kick-in 
    		
    		Collection<Trade> trades = tradeService.getTrades();
    		tradeService.stopTradeExpiryScheduler();  //Stop the thread as we have got the results 		
    		trades.forEach(
    			(updatedTrade) -> {
    				if (updatedTrade.compareTo(theTrade) == 0) {
    					assertEquals(true, updatedTrade.isExpired());
    				}
    			}
    		);
		}
}
