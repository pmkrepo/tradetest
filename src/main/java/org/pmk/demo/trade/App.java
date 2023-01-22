/**
* Trade Application startup class
* For the test assignment Input and output are provided from console
* In real project it may only be Springboot application startup calss
* And then input output can be e.g. REST services 
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/

package org.pmk.demo.trade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.pmk.demo.trade.dao.InMemoryTradeDao;
import org.pmk.demo.trade.exception.ValidationException;
import org.pmk.demo.trade.model.Trade;
import org.pmk.demo.trade.service.TradeService;
import org.pmk.demo.trade.validations.TradeMaturityDateValidator;
import org.pmk.demo.trade.validations.TradeVersionValidator;


public class App 
{
	public static final long TRADE_EXPIRY_SCHEDULER_FIRST_START_SECONDS = 3;
	public static final long TRADE_EXPIRY_SCHEDULER_RATE_AFTER_FIRST = 10;
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	
	private static TradeService service = new TradeService(new InMemoryTradeDao(),  new TradeMaturityDateValidator(new TradeVersionValidator(null)));
    public static void main( String[] args ) throws IOException
    {		
    	System.out.println("Trade expiry scheduler will run in " + TRADE_EXPIRY_SCHEDULER_FIRST_START_SECONDS 
    			+ " second and then every " + TRADE_EXPIRY_SCHEDULER_RATE_AFTER_FIRST + " seconds");
    	service.startTradeExpiryScheduler(TRADE_EXPIRY_SCHEDULER_FIRST_START_SECONDS, TRADE_EXPIRY_SCHEDULER_RATE_AFTER_FIRST);
    	
		System.out.print("Do you want to add pre-defined test data? (Y/N): ");
    	BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in));
    	boolean runPredefined = "Y".equalsIgnoreCase(reader.readLine());	
    	
    	if (runPredefined) {
    		testPredefinedInputAndTradeExpiry();
    	}

    	boolean exit = false;
    	while (!exit) {
    		System.out.println("--------------------------------------------");
    		System.out.println("Please run the command: add | show | exit");    		
        	String command = reader.readLine();
        	
        	switch(command) {
        		case "add" : addTradeFromConsole(); break;
        		case "show" : showTrades(); break;
        		case "exit" : exit = true; break;
        		default: System.out.println("Invalid command");
        	}
			
    	}
    	System.out.println("Stopping Trade Expiry Scheduler");

		service.stopTradeExpiryScheduler();
		
		System.out.println("Exiting----");
    }
    
    private static void addTrade(Trade trade) {		
    	System.out.println("Adding Trade: [" + trade + "]");
		try {
			service.addNewTrade(trade);
			System.out.println("Trade Added ------------------");
		} catch (ValidationException e) {
			System.out.println("Trade Not Added due to ValidationException: " + e.getMessage());
		}
		System.out.println();
    }
    
    private static void showTrades() {	
    	System.out.println("Current Trades in the System (formatted): ");
    	service.getTrades().forEach( (theTrade) -> {  
    		System.out.println("Trade Id: " + theTrade.getTradeId() 
    				+ ", Trade Version: " + theTrade.getTradeVersion()
    				+ ", Counter Party Id: " + theTrade.getCounterPartyId()
    				+ ", Book Id: " + theTrade.getCounterPartyId() 
    				+ ", Maturiy Date: " + new Date(theTrade.getMaturityDateTimeStamp() )
    				+ ", Created Date: " + new Date(theTrade.getCreatedDateTimeStamp() )
    				+ ", Expired: " + (theTrade.isExpired() ? "Y" : "N"));
    	}  );
    }
    
    private static void addTradeFromConsole() {	
    	BufferedReader reader = new BufferedReader(
        new InputStreamReader(System.in));
    	System.out.println("Plase provide data for Trade: ");
  		try {
  	    	System.out.print("Trade Id (e.g. T3): ");
			String tradeId = reader.readLine();
			
  	    	System.out.print("Trade Version (e.g. 1): ");
			int tradeVer = Integer.parseInt(reader.readLine());

			System.out.print("Counter Party Id (e.g. CP-1): ");
			String counterPartyId = reader.readLine();	

			System.out.print("Book Id (e.g. B2): ");
			String bookId = reader.readLine();
			
			System.out.print("Maturity Date (Froamt: " + DATE_FORMAT + "): ");
			String maturityDateStr = reader.readLine();			
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			Date maturityDate = dateFormat.parse(maturityDateStr);
			long maturityDateTs = maturityDate.getTime();							
			long createdDate = System.currentTimeMillis();		
			
			Trade trade = Trade.createTrade(tradeId, tradeVer, counterPartyId, bookId, createdDate, maturityDateTs, false);			
			addTrade(trade);
		} catch (Exception e) {
			System.out.println("Wrong Input: " + e.toString());
		}
    }
    
    private static void testPredefinedInputAndTradeExpiry() {
		Trade tradeT31 = Trade.createTrade("T3", 1, "CP-1", "B59", 
				System.currentTimeMillis(), System.currentTimeMillis()+TimeUnit.DAYS.toMillis(1), false);
		Trade tradeT32 = Trade.createTrade("T3", 2, "CP-4", "B40", 
				System.currentTimeMillis(), System.currentTimeMillis()+10000, false);
		Trade tradeT11 = Trade.createTrade("T1", 1, "CP-1", "B40", 
				System.currentTimeMillis(), System.currentTimeMillis()+20000, false);
		Trade tradeT22 = Trade.createTrade("T2", 2, "CP-3", "B33", 
				System.currentTimeMillis(), System.currentTimeMillis()+TimeUnit.DAYS.toMillis(3), false);
		Trade tradeT33 = Trade.createTrade("T3", 3, "CP-1", "B40", 
				System.currentTimeMillis(), System.currentTimeMillis()+20000, false);
		//Maturity Date is lower than current date
		Trade tradeT12 = Trade.createTrade("T1", 1, "CP-1", "B40", 
				System.currentTimeMillis(), System.currentTimeMillis()-20000, false);
		//Higher version exists
		Trade tradeT21 = Trade.createTrade("T2", 1, "CP-1", "B33", 
				System.currentTimeMillis(), System.currentTimeMillis()+20000, false);

		addTrade(tradeT31);
		addTrade(tradeT32);
		addTrade(tradeT11);
		addTrade(tradeT22);
		addTrade(tradeT33);
		
		System.out.println("Trying to add trade where maturity date is passed");		
		addTrade(tradeT12);
		
		System.out.println("Trying to add trade with lower version than existing trade");
		addTrade(tradeT21);
		
		//Wait few second for expired trades to be updated
		System.out.println("Waiting for Thread expiry scheduler to kick-in ...");
		try {
			Thread.sleep(TimeUnit.SECONDS.toMillis(3));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		showTrades();		
    }
}
