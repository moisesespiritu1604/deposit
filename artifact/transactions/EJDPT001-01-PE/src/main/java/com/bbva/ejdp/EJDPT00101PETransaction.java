package com.bbva.ejdp;

import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.lib.r001.EJDPR001;
import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.elara.domain.transaction.Severity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Expone el servicio para registrar solicitudes de depósitos a plazo
 *
 */
public class EJDPT00101PETransaction extends AbstractEJDPT00101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPT00101PETransaction.class);

	@Override
	public void execute() {
		LOGGER.info("[EJDPT00101PETransaction] - Inicio de transacción");

		// Obtener servicio
		EJDPR001 ejdpR001 = this.getServiceLibrary(EJDPR001.class);

		// Obtener datos de entrada
		DepositRequestDTO depositRequestDTO = this.getDepositrequest();
		LOGGER.info("[EJDPT00101PETransaction] - Datos recibidos: {}", depositRequestDTO);

		// Crear CustomerDTO sin ID (el servicio lo asigna)
		CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setAccountNumber(depositRequestDTO.getAccountNumber());
		customerDTO.setCustomerName(depositRequestDTO.getCustomerName());

		// Ejecutar lógica de negocio
		DepositResponseDTO depositResponseDTO = new DepositResponseDTO();
		DepositResponseDTO result = ejdpR001.executeRegisterDeposit(customerDTO, depositRequestDTO, depositResponseDTO);

		// Setear salidas
		this.setCustomer(customerDTO);
		this.setDeposit(result);
		this.setSeverity(Severity.OK);

		LOGGER.info("[EJDPT00101PETransaction] - Transacción completada con éxito");
	}
}

