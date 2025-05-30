package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.lib.r001.EJDPR001;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registro de clientes y depositos a plazo
 *
 */
public class EJDPT00101PETransaction extends AbstractEJDPT00101PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPT00101PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		EJDPR001 ejdpR001 = this.getServiceLibrary(EJDPR001.class);
		// TODO - Implementation of business logic
		LOGGER.info("[EJDPT00101PETransaction] - Inicio de transacción");

		DepositRequestDTO request = this.getDepositrequest();
		// Validate mandatory fields
		if (request == null ||
				StringUtils.isBlank(request.getAccountNumber()) ||
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

		// Validate account number format (must be 8 digits)
		if (!request.getAccountNumber().matches("\\d{8}")) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

	// Validate customer name length (max 20 chars)
		if (request.getCustomerName().length() > 20) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

	// Validate amount (greater than 0 and max 8 digits)
		if (request.getAmount() <= 0 || String.valueOf(request.getAmount()).length() > 8) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_AMOUNT.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_AMOUNT.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

	// Validate interest rate (greater than 0 and less than or equal to 100)
		if (request.getInterestRate() <= 0 || request.getInterestRate() > 100) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_INTEREST_RATE.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
			return;
		}

		// Validate term days (must be greater than 0)
		if (request.getTermDays() <= 0) {
			LOGGER.info("Error: {}", BusinessExceptionEJDP.INVALID_TERM_DAYS.getMessage());
			this.addAdvice(BusinessExceptionEJDP.INVALID_TERM_DAYS.getCode());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_400);
			this.setSeverity(Severity.ENR);
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
}
