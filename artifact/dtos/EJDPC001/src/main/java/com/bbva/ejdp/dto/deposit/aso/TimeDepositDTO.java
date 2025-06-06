package com.bbva.ejdp.dto.deposit.aso;

import java.io.Serializable;
import java.util.Date;

public class TimeDepositDTO implements Serializable {
    private Long id;
    private String accountNumber;
    private String customerName;
    private Double amount;
    private Double interestRate;
    private Integer termDays;
    private Date applicationDate;
    private Date maturityDate;
    private Double interestEarned;
    private String status;

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
        return "TimeDepositDTO{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", customerName='" + customerName + '\'' +
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
