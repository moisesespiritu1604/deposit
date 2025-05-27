package com.bbva.ejdp.dto.deposit;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The DepositRequestDTO class...
 */
public class DepositRequestDTO implements Serializable  {
	private static final long serialVersionUID = 2931699728946643245L;
	private String accountNumber;
	private String customerName;
	private Double amount;
	private Double interestRate;
	private Integer termDays;

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public Integer getTermDays() {
		return termDays;
	}

	public void setTermDays(Integer termDays) {
		this.termDays = termDays;
	}

	@Override
	public String toString() {
		return "DepositRequestDTO{" +
				"accountNumber='" + accountNumber + '\'' +
				", customerName='" + customerName + '\'' +
				", amount=" + amount +
				", interestRate=" + interestRate +
				", termDays=" + termDays +
				'}';
	}
}
