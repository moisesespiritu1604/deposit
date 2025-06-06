package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.lib.r002.EJDPR002;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Transaccion para consumir una api interna
 *
 */
public class EJDPT00401PETransaction extends AbstractEJDPT00401PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPT00401PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		LOGGER.info("[EJDPT00401PETransaction] - Inicio de transacción de consulta de depósitos");

		try {
			LOGGER.info("[EJDPT00401PETransaction] - Iniciando recuperación de depósitos a plazo");

			// Obtener la instancia de la librería
			EJDPR002 ejdpR002 = this.getServiceLibrary(EJDPR002.class);

			// Ejecutar la llamada a la librería
			List<TimeDepositDTO> timeDepositList = ejdpR002.executeGetAllTimeDeposits();

			LOGGER.info("[EJDPT00401PETransaction] - {} depósitos recuperados", timeDepositList.size());

			// Log opcional por cada depósito
			for (TimeDepositDTO deposit : timeDepositList) {
				LOGGER.debug("Depósito: ID={}, Cuenta={}, Cliente={}, Monto={}, Estado={}",
						deposit.getId(), deposit.getAccountNumber(),
						deposit.getCustomerName(), deposit.getAmount(),
						deposit.getStatus());
			}
			this.setDeposits(timeDepositList);
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200);
			this.setSeverity(Severity.OK);

		} catch (BusinessException ex) {
			LOGGER.error("[EJDPT00401PETransaction] - Error de negocio al recuperar depósitos: {}", ex.getMessage());
			this.addAdvice(ex.getAdviceCode());
			this.setDeposits(new ArrayList<>());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.EWR);

		} catch (Exception e) {
			LOGGER.error("[EJDPT00401PETransaction] - Error inesperado al recuperar depósitos", e);
			this.addAdvice(BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode());
			this.setDeposits(new ArrayList<>());
			this.setSeverity(Severity.EWR);
		}

	}

}
