package com.bbva.ejdp.lib.r002;

import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.ejdp.dto.deposit.mock.CustomerDepositMock;

import java.util.List;

/**
 * The  interface EJDPR002 class...
 */
public interface EJDPR002 {

	/**
	 * The execute method...
	 */
	void execute();
	List<TimeDepositDTO> executeGetAllTimeDeposits();
	CustomerDepositMock executeRegisterTimeDeposit(DepositRequestDTO requestDTO);
}
