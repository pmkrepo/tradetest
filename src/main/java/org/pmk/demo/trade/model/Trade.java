package org.pmk.demo.trade.model;

import java.util.Objects;


public class Trade implements Comparable<Trade> {
	private String tradeId;
	private int tradeVersion;
	private String counterPartyId;
	private String bookId;
	//Date time should always be stored and processed by the system in milliseconds, Date format and timezone should be used only for the display purpose
	private long maturityDateTimeStamp; 
	private long createdDateTimeStamp;
	private boolean expired;
	
	private static final String EXCEPTION_NULL_TRADE_ID = "Trade ID Cannot be NULL";
		
	private Trade(String tradeId, int tradeVersion) {
		this.setTradeId(tradeId);
		this.setTradeVersion(tradeVersion);
	}
	
	//Getter Setters
	public String getTradeId() {
		return tradeId;
	}
	public void setTradeId(String tradeId) {
		if (tradeId == null) {
			throw new IllegalArgumentException(EXCEPTION_NULL_TRADE_ID);
		}
		this.tradeId = tradeId;
	}
	public int getTradeVersion() {
		return tradeVersion;
	}
	public void setTradeVersion(int tradeVersion) {
		this.tradeVersion = tradeVersion;
	}
	public String getCounterPartyId() {
		return counterPartyId;
	}
	public void setCounterPartyId(String counterPartyId) {
		this.counterPartyId = counterPartyId;
	}
	public String getBookId() {
		return bookId;
	}
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}
	public long getMaturityDateTimeStamp() {
		return maturityDateTimeStamp;
	}

	public void setMaturityDateTimeStamp(long maturityDateTimeStamp) {
		this.maturityDateTimeStamp = maturityDateTimeStamp;
	}
	public long getCreatedDateTimeStamp() {
		return createdDateTimeStamp;
	}
	public void setCreatedDateTimeStamp(long createdDateTimeStamp) {
		this.createdDateTimeStamp = createdDateTimeStamp;
	}
	public boolean isExpired() {
		return expired;
	}
	public void setExpired(boolean expired) {
		this.expired = expired;
	}
	
	//hashCode, equals and toString
	@Override
	public int hashCode() {
		return Objects.hash(bookId, counterPartyId, createdDateTimeStamp, expired, maturityDateTimeStamp, tradeId,
				tradeVersion);
	}	
	@Override
	public boolean equals(Object otherTradeObj) {		
		boolean isEqual = false;
		if (otherTradeObj instanceof Trade) {
			Trade otherTrade = (Trade) otherTradeObj;
			if (Objects.equals(bookId, otherTrade.bookId) 
					&& Objects.equals(counterPartyId, otherTrade.counterPartyId)
					&& createdDateTimeStamp == otherTrade.createdDateTimeStamp 
					&& expired == otherTrade.expired
					&& maturityDateTimeStamp == otherTrade.maturityDateTimeStamp 
					&& Objects.equals(tradeId, otherTrade.tradeId)
					&& tradeVersion == otherTrade.tradeVersion) {
				isEqual = true;
			}				
		}						
		return isEqual;
	}		
	@Override
	public String toString() {
		return "Trade [tradeId=" + tradeId + ", tradeVersion=" + tradeVersion + ", counterPartyId=" + counterPartyId
				+ ", bookId=" + bookId + ", maturityDateTimeStamp=" + maturityDateTimeStamp + ", createdDateTimeStamp="
				+ createdDateTimeStamp + ", expired=" + expired + "]";
	}

	//compareTo method is used for Collection to sort the objects 
	//we need to sort by tradeId (ascending) and then tradeVersion (descending)
	@Override
	public int compareTo(Trade otherTrade) {		
		if (otherTrade == null) {
			return -1;
		}
		
		int compareTradeId = tradeId.compareTo(otherTrade.getTradeId());
		if (compareTradeId != 0) {
			return compareTradeId;
		} else {
			return Integer.compare(otherTrade.getTradeVersion(), tradeVersion);
		}		
	}		
	
	//Factory method to create Trade pojo with less verbosity
	public static Trade createTrade(String tradeId, int tradeVersion, String counterPartyId, String bookId, 
			long createdDateTs, long maturityDateTs,  boolean expired) {
		Trade theTrade = new Trade(tradeId, tradeVersion);
		theTrade.setCounterPartyId(counterPartyId);
		theTrade.setBookId(bookId);
		theTrade.setMaturityDateTimeStamp(maturityDateTs);
		theTrade.setCreatedDateTimeStamp(createdDateTs);
		theTrade.setExpired(expired);		
		return theTrade;
	}
}
