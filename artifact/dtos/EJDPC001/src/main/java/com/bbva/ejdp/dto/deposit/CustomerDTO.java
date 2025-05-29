package com.bbva.ejdp.dto.deposit;

import java.io.Serializable;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The CustomerDTO class...
 */
public class CustomerDTO implements Serializable  {
	private static final long serialVersionUID = 2931699728946643245L;

	private Long id;
	private String accountNumber;
	private String customerName;




	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	@Override
	public String toString() {
		return "CustomerDTO{" +
				"id=" + id +
				", accountNumber='" + accountNumber + '\'' +
				", customerName='" + customerName + '\'' +
				'}';
	}

	public CustomerDTO(Long id, String accountNumber, String customerName) {
		this.id = id;
		this.accountNumber = accountNumber;
		this.customerName = customerName;
	}
	public CustomerDTO() {
		super();
	}

}
