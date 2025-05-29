package com.bbva.ejdp.lib.r001;

import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.CustomerDepositDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;

import java.util.List;

/**
 * The  interface EJDPR001 class...
 */
public interface EJDPR001 {

	/**
	 * The execute method...
	 */
	DepositResponseDTO executeRegisterDeposit(DepositRequestDTO depositRequestDTO);
	List<CustomerDepositDTO> executeGetAllCustomerDeposits();
}
