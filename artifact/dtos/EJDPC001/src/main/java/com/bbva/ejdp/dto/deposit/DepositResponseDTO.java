package com.bbva.ejdp.dto.deposit;

import java.io.Serializable;
import java.util.Date;

/**
 * The DespositResponseDTO class...
 */
public class DepositResponseDTO implements Serializable  {
	private static final long serialVersionUID = 2931699728946643245L;

	private Long id;
	private Double amount;
	private Double interestRate;
	private Integer termDays;
	private Date applicationDate;
	private Date maturityDate;
	private Double interestEarned; // optional
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public Double getInterestEarned() {
		return interestEarned;
	}

	public void setInterestEarned(Double interestEarned) {
		this.interestEarned = interestEarned;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "DespositResponseDTO{" +
				"id=" + id +
				", amount=" + amount +
				", interestRate=" + interestRate +
				", termDays=" + termDays +
				", applicationDate=" + applicationDate +
				", maturityDate=" + maturityDate +
				", interestEarned=" + interestEarned +
				", status='" + status + '\'' +
				'}';
	}
}
