package com.bbva.ejdp;

import com.bbva.ejdp.dto.deposit.CustomerDepositDTO;
import com.bbva.ejdp.lib.r001.EJDPR001;
import com.bbva.elara.domain.transaction.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Mostrar lista de usuarios y los depositos
 *
 */
public class EJDPT00201PETransaction extends AbstractEJDPT00201PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPT00201PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		LOGGER.info("[EJDPT00102PETransaction] - Inicio de ejecución");
		EJDPR001 ejdpR001 = this.getServiceLibrary(EJDPR001.class);
		// TODO - Implementation of business logic

		List<CustomerDepositDTO> customerDepositList = ejdpR001.executeGetAllCustomerDeposits();

		this.setCustomerlist(customerDepositList);
		this.setSeverity(Severity.OK);

		LOGGER.info("[EJDPT00102PETransaction] - Fin de ejecución");
	}

}
