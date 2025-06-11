package com.bbva.ejdp.dto.deposit.mock;

import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;

import java.io.Serializable;
import java.util.List;

public class CustomerDepositMock implements Serializable {
    private CustomerDTO customer;
    private List<DepositResponseDTO> deposits;

    public CustomerDepositMock() {
        super();
    }

    public CustomerDepositMock(CustomerDTO customer, List<DepositResponseDTO> deposit) {
        this.customer = customer;
        this.deposits = deposit;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public List<DepositResponseDTO> getDeposits() {
        return deposits;
    }

    public void setDeposits(List<DepositResponseDTO> deposit) {
        this.deposits = deposit;
    }

    @Override
    public String toString() {
        return "CustomerDepositMock{" +
                "customer=" + customer +
                ", deposit=" + deposits +
                '}';
    }
}
