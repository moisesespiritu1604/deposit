package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.lib.r001.EJDPR001;
import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Expone el servicio para registrar solicitudes de depósitos a plazo
 *
 */
public abstract class EJDPT00101PETransaction extends AbstractEJDPT00101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPT00101PETransaction.class);

	@Override
	public void execute() {
		LOGGER.info("[EJDPT00101PETransaction] - Inicio de transacción");

		EJDPR001 ejdpR001 = this.getServiceLibrary(EJDPR001.class);
		DepositRequestDTO request = this.getDepositrequest();

		// Validaciones personalizadas
		if (request == null) {
			addValidationError(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING);
			return;
		}

		// accountNumber
		// Validación combinada de accountNumber
		if (StringUtils.isBlank(request.getAccountNumber()) ||
				!request.getAccountNumber().matches("\\d{8}")) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getMessage());
			addValidationError(BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT);
			return;
		}
		// customerName
		if (StringUtils.isBlank(request.getCustomerName()) || request.getCustomerName().length() > 20) {
			addValidationError(BusinessExceptionEJDP.INVALID_CUSTOMER_NAME);
			return;
		}

		// amount
		if (request.getAmount() == null || request.getAmount() <= 0 || String.valueOf(request.getAmount()).length() > 8) {
			addValidationError(BusinessExceptionEJDP.INVALID_AMOUNT);
			return;
		}

		// interestRate
		if (request.getInterestRate() == null || request.getInterestRate() <= 0 || request.getInterestRate() > 100) {
			addValidationError(BusinessExceptionEJDP.INVALID_INTEREST_RATE);
			return;
		}

		// termDays
		if (request.getTermDays() == null || request.getTermDays() <= 0) {
			addValidationError(BusinessExceptionEJDP.INVALID_TERM_DAYS);
			return;
		}

		LOGGER.info("Request válido: {}", request);

		try {
			DepositResponseDTO deposit = ejdpR001.executeRegisterDeposit(request);
			this.setCustomer(new CustomerDTO(deposit.getId(), request.getAccountNumber(), request.getCustomerName()));
			this.setDeposit(deposit);
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200);
			this.setSeverity(Severity.OK);
		} catch (BusinessException ex) {
			LOGGER.info("Error técnico: {}", ex.getMessage());
			this.addAdvice(ex.getAdviceCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.EWR);
		}
	}
	private void addValidationError(BusinessExceptionEJDP error) {
		this.addAdvice(error.getCode());
		this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
		this.setSeverity(Severity.ENR);
	}
}

