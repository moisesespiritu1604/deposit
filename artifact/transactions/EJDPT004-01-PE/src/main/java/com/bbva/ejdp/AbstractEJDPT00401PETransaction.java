package com.bbva.ejdp;

import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.elara.transaction.AbstractTransaction;
import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractEJDPT00401PETransaction extends AbstractTransaction {

	protected AbstractEJDPT00401PETransaction(){
	}


	/**
	 * Set value for List<TimeDepositDTO> output parameter Deposits
	 */
	protected void setDeposits(final List<TimeDepositDTO> field){
		this.addParameter("Deposits", field);
	}
}
