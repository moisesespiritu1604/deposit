package com.bbva.ejdp;

import com.bbva.ejdp.dto.deposit.CustomerDepositDTO;
import com.bbva.elara.transaction.AbstractTransaction;
import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractEJDPT00201PETransaction extends AbstractTransaction {

	protected AbstractEJDPT00201PETransaction(){
	}


	/**
	 * Set value for List<CustomerDepositDTO> output parameter CustomerList
	 */
	protected void setCustomerlist(final List<CustomerDepositDTO> field){
		this.addParameter("CustomerList", field);
	}
}
