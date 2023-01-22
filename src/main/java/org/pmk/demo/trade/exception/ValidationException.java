/**
* ValidationException when Trade validation fails
* @author  pmk
* @version 1.0
* @since   2023-01-21
*/
package org.pmk.demo.trade.exception;

public class ValidationException extends Exception {
		
	private static final long serialVersionUID = 1L;

	public ValidationException(String message) {
		super(message);
	}

}
