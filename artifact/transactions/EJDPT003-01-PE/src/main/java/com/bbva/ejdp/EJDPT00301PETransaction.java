package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.dto.deposit.mock.CustomerDepositMock;
import com.bbva.ejdp.lib.r002.EJDPR002;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Prueba
 *
 */
public class EJDPT00301PETransaction extends AbstractEJDPT00301PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPT00301PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		LOGGER.info("[EJDPT00301PETransaction] - Inicio de transacción de registro de depósito a plazo");

		DepositRequestDTO request = new DepositRequestDTO();
		request.setAccountNumber(this.getAccountNumber());
		request.setCustomerName(this.getCustomerName());
		request.setAmount(this.getAmount());
		request.setInterestRate(this.getInterestRate());
		request.setTermDays(this.getTermDays() != null ? this.getTermDays().intValue() : null);

		// Validación de campos obligatorios
		if ( StringUtils.isBlank(request.getAccountNumber()) ||
				StringUtils.isBlank(request.getCustomerName()) ||
				request.getAmount() == null ||
				request.getInterestRate() == null ||
				request.getTermDays() == null) {

			LOGGER.info("Error: {}", BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getMessage());
			this.addAdvice(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

		// Validación: número de cuenta debe ser de 8 dígitos
		if (!request.getAccountNumber().matches("\\d{8}")) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

		// Validación: nombre de cliente máximo 20 caracteres
		if (request.getCustomerName().length() > 20) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

		// Validación: monto > 0 y máximo 8 dígitos
		if (request.getAmount() <= 0 || String.valueOf(request.getAmount().intValue()).length() > 8) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_AMOUNT.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_AMOUNT.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

		// Validación: tasa de interés entre 0 y 100
		if (request.getInterestRate()<= 0 || request.getInterestRate() > 100) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_INTEREST_RATE.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

		// Validación: plazo en días > 0
		if (request.getTermDays() <= 0) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_TERM_DAYS.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_TERM_DAYS.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

		try {
			LOGGER.info("[EJDPT00301PETransaction] - Datos recibidos para registro: {}", request);

			EJDPR002 ejdpR002 = this.getServiceLibrary(EJDPR002.class);
			CustomerDepositMock response = ejdpR002.executeRegisterTimeDeposit(request);

			LOGGER.info("[EJDPT00301PETransaction] - Registro de depósito exitoso: {}", response);

			this.setCustomer(response.getCustomer());
			this.setDeposits(response.getDeposits());
			this.setCustomerDepositMock(response);

			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200);
			this.setSeverity(Severity.OK);

		} catch (BusinessException ex) {
			LOGGER.error("[EJDPT00301PETransaction] - Error de negocio al registrar depósito: {}", ex.getMessage());
			this.addAdvice(ex.getAdviceCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.EWR);
		}

		LOGGER.info("[EJDPT00301PETransaction] - Fin de transacción de registro de depósito a plazo");
	}
}
