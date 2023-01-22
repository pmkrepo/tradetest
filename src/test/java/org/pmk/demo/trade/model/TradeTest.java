/**
* Trade Model Unit Test
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
package org.pmk.demo.trade.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class TradeTest {
	
	private static final String EXCEPTION_NULL_TRADE_ID = "Trade ID Cannot be NULL";
	
	private Trade theTrade;
	
	String tradeId;
	int tradeVersion;
	String counterPartyId;
	String bookId;
	long maturityDateTs;
	long createdDateTs;
	boolean expired;	
	
	@BeforeEach
	//Setup trade data to test
	public void setUp () {

		tradeId = "T1";
		tradeVersion = 2;
		counterPartyId = "CP-1";
		bookId = "B1";
		expired = false;
		maturityDateTs = 1672572824030L;
		createdDateTs = 1672572824030L;	
		
		theTrade = Trade.createTrade(tradeId, tradeVersion, counterPartyId, bookId, createdDateTs, maturityDateTs, expired);
	}
	
	@AfterEach
	//Rest data before next test
	public void tearDown () {
		theTrade = null;
		tradeId = null;
		tradeVersion = 0;
		counterPartyId = null;
		bookId = null;
		expired = false;
		maturityDateTs = 0;
		createdDateTs = 0;
	}
	
    @Test
    //Test Trade object creation, setters, getters
    public void testGetterSetters() {
    	
        //Call Getters and verify
    	assertEquals( theTrade.getTradeId(), tradeId );
        assertEquals( theTrade.getTradeVersion(), tradeVersion );
        assertEquals( theTrade.getCounterPartyId(), counterPartyId );
    	assertEquals( theTrade.getBookId(), bookId );
        assertEquals( theTrade.isExpired(), expired );
        assertEquals( theTrade.getCreatedDateTimeStamp(), createdDateTs);
        assertEquals( theTrade.getMaturityDateTimeStamp(), maturityDateTs);        
    }
    
    @Test
    //Successful equality test 
    public void testEqualsTrue () {
    	Trade eqaulTrade = Trade.createTrade(tradeId, tradeVersion, counterPartyId, bookId, createdDateTs, maturityDateTs, expired);    	
    	assertTrue(eqaulTrade.equals(eqaulTrade));
    }   
    
    @ParameterizedTest
    @CsvSource({"T2,2,CP-1,B1,false,1672572824030,1672572824030",  	//Different tradeId
    			"T1,3,CP-1,B1,false,1672572824030,1672572824030",		//Different tradeVersion
    			"T1,2,CP-2,B1,false,1672572824030,1672572824030",		//Different Counter party id
    			"T1,2,CP-1,B2,false,1672572824030,1672572824030",		//Different Book id
    			"T1,2,CP-1,B1,true,1672572824030,1672572824030",		//Different expired
    			"T1,2,CP-1,B1,false,1672572822999,1672572824030",		//Different created date
    			"T1,2,CP-1,B1,false,1672572824030,1672572856789",		//Different Maturity date  			
    		   })
    //Trade equality test is false
    //Parameterized test - Input parameters has one argument mis-match with the values in setup()
    public void testEqualsFalse (String tradeId, int tradeVersion, String counterPartyId, 
    		String bookId, boolean expired, long createdDateTs, long maturityDateTs) {    	

    	Trade otherTrade = Trade.createTrade(tradeId, tradeVersion, counterPartyId, bookId, 
    			createdDateTs, maturityDateTs, expired);
    	assertFalse(theTrade.equals(otherTrade));  	
    }
    
    @Test 
    //Test exception when Trade is created with null tradeId
    public void testNullId() {
	    Throwable exception = assertThrows( IllegalArgumentException.class,
			   ()->{Trade.createTrade(null, 0, null, null, 0, 0, false);
	    } );
	   
	    assertEquals(exception.getMessage(), EXCEPTION_NULL_TRADE_ID);
    } 
    
    @Test 
    public void testEqualsNull() {
    	assertFalse(theTrade.equals(null));
    }
    
    @Test 
    public void testHashCode() {
    	int expectedHash = Objects.hash(bookId, counterPartyId, createdDateTs, expired, maturityDateTs, tradeId,
				tradeVersion);
    	assertEquals(expectedHash, theTrade.hashCode());
    }
    
    @Test 
    public void testToString() {
    	String expectedString = "Trade [tradeId=" + tradeId + ", tradeVersion=" + tradeVersion + ", counterPartyId=" + counterPartyId
				+ ", bookId=" + bookId + ", maturityDateTimeStamp=" + maturityDateTs + ", createdDateTimeStamp="
				+ createdDateTs + ", expired=" + expired + "]";
    	assertEquals(expectedString, theTrade.toString());
    }   
    
    @ParameterizedTest
    @CsvSource({"T1,2,0",  		//Id and Version are equal
    			"T2,2,-1",		//Id is bigger version is equal
    			"T1,3,1",		//Id is equal version is higher
    			"T0,2,1",		//Id is smaller version is equal
    			"T1,1,-1",		//Id is equal version is smaller
    		})
    //compareTo is used by TreeSet to sort objects based on tradeId (low to high) and then trade version (high to low)
    //Test that compareTo returns right response (-1, 0, 1)
    public void testCompareTo (String otherTradeId, int otherTradeVersion, int expectedResult) {
    	
    	Trade otherTrade = Trade.createTrade(otherTradeId, otherTradeVersion, counterPartyId, bookId, 
    			createdDateTs, maturityDateTs, expired);   	
    	assertEquals(theTrade.compareTo(otherTrade),expectedResult);  	
    }
    
    @Test
    public void testCompareToNull () {
     	assertEquals(theTrade.compareTo(null),-1);  	
    }
}