package com.bbva.ejdp.dto.deposit;

import java.io.Serializable;
import java.math.BigDecimal;

public class CustomerDepositDTO implements Serializable {
    private CustomerDTO customer;
    private DepositResponseDTO deposit;

    public CustomerDepositDTO() {
        super();
    }

    public CustomerDepositDTO(CustomerDTO customer, DepositResponseDTO deposit) {
        this.customer = customer;
        this.deposit = deposit;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public DepositResponseDTO getDeposit() {
        return deposit;
    }

    public void setDeposit(DepositResponseDTO deposit) {
        this.deposit = deposit;
    }

    @Override
    public String toString() {
        return "CustomerDepositDTO{" +
                "customer=" + customer +
                ", deposit=" + deposit +
                '}';
    }
}
