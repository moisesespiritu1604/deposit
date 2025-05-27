package com.bbva.ejdp;

import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.elara.transaction.AbstractTransaction;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractEJDPT00101PETransaction extends AbstractTransaction {

	protected AbstractEJDPT00101PETransaction(){
	}


	/**
	 * Return value for input parameter DepositRequest
	 */
	protected DepositRequestDTO getDepositrequest(){
		return (DepositRequestDTO)this.getParameter("DepositRequest");
	}

	/**
	 * Set value for CustomerDTO output parameter Customer
	 */
	protected void setCustomer(final CustomerDTO field){
		this.addParameter("Customer", field);
	}

	/**
	 * Set value for DespositResponseDTO output parameter Deposit
	 */
	protected void setDeposit(final DepositResponseDTO field){
		this.addParameter("Deposit", field);
	}
}
