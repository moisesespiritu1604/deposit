package com.bbva.ejdp;

import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.mock.CustomerDepositMock;
import com.bbva.elara.transaction.AbstractTransaction;

import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractEJDPT00301PETransaction extends AbstractTransaction {

	protected AbstractEJDPT00301PETransaction(){
	}


	protected String getAccountNumber() {
		return (String) this.getParameter("accountNumber");
	}

	protected String getCustomerName() {
		return (String) this.getParameter("customerName");
	}

	protected Double getAmount() {
		return (Double) this.getParameter("amount");
	}

	protected Double getInterestRate() {
		return (Double) this.getParameter("interestRate");
	}

	protected Long getTermDays() {
		return (Long) this.getParameter("termDays");
	}

	protected void setCustomerDepositMock(final CustomerDepositMock field) {
		this.addParameter("CustomerDepositMock", field);
	}
	protected void setCustomer(final CustomerDTO customer) {
		this.addParameter("customer", customer);
	}

	protected void setDeposits(final List<DepositResponseDTO> deposits) {
		this.addParameter("deposits", deposits);
	}
}
