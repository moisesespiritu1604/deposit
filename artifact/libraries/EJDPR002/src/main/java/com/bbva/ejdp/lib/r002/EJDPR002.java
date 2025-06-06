package com.bbva.ejdp.lib.r002;

import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;

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
}
