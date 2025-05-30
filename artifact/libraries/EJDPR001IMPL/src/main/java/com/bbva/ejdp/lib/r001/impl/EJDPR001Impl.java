package com.bbva.ejdp.lib.r001.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.apx.exception.db.DBException;
import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.CustomerDepositDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The EJDPR001Impl class...
 */
public class EJDPR001Impl extends EJDPR001Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPR001Impl.class);

	private static final String QUERY_INSERT_CUSTOMER = "EJDP.INSERT_CUSTOMER_SQL";
	private static final String QUERY_INSERT_DEPOSIT = "EJDP.QUERY_INSERT_DEPOSIT";
	private static final String QUERY_NEXT_CUSTOMER_ID = "EJDP.NEXT_CUSTOMER_ID";
	private static final String QUERY_NEXT_DEPOSIT_ID = "EJDP.NEXT_DEPOSIT_ID";
	private static final String QUERY_SELECT_ALL_CUSTOMER_DEPOSITS = "EJDP.QUERY_SELECT_ALL_CUSTOMER_DEPOSITS";

	@Override
	public DepositResponseDTO executeRegisterDeposit(DepositRequestDTO depositRequestDTO) {
		LOGGER.info("[EJDPR001] - Start deposito");

		try {
			// Validación duplicado por base de datos
			List<Map<String, Object>> existing = jdbcUtils.queryForList(
					"EJDP.CHECK_ACCOUNT_NUMBER",
					Map.of("ACCOUNT_NUMBER", depositRequestDTO.getAccountNumber()));

			if (!existing.isEmpty()) {
				throw new BusinessException(
						BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(),
						BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getHasRollback(),
						BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getMessage());
			}

			// Obtener IDs
			Long customerId = ((Number) jdbcUtils.queryForMap(QUERY_NEXT_CUSTOMER_ID).get("NEXT_ID")).longValue();
			Long depositId = ((Number) jdbcUtils.queryForMap(QUERY_NEXT_DEPOSIT_ID).get("NEXT_ID")).longValue();

			// Insertar cliente
			Map<String, Object> customerParams = Map.of(
					"ID", customerId,
					"ACCOUNT_NUMBER", depositRequestDTO.getAccountNumber(),
					"CUSTOMER_NAME", depositRequestDTO.getCustomerName()
			);
			jdbcUtils.update(QUERY_INSERT_CUSTOMER, customerParams);

			// Fechas y cálculo
			Date applicationDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(applicationDate);
			calendar.add(Calendar.DAY_OF_MONTH, depositRequestDTO.getTermDays());
			Date maturityDate = calendar.getTime();

			Double interestEarned = depositRequestDTO.getAmount()
					* depositRequestDTO.getInterestRate() / 100
					* depositRequestDTO.getTermDays() / 365.0;

			Map<String, Object> depositParams = Map.of(
					"ID", depositId,
					"CUSTOMER_ID", customerId,
					"AMOUNT", depositRequestDTO.getAmount(),
					"INTEREST_RATE", depositRequestDTO.getInterestRate(),
					"TERM_DAYS", depositRequestDTO.getTermDays(),
					"APPLICATION_DATE", applicationDate,
					"MATURITY_DATE", maturityDate,
					"INTEREST_EARNED", interestEarned,
					"STATUS", "ACTIVADO"
			);

			// Intentar insertar depósito
			try {
				jdbcUtils.update(QUERY_INSERT_DEPOSIT, depositParams);
			} catch (Exception e) {
				LOGGER.error("Error al registrar depósito", e);
				throw new BusinessException(
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(),
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getHasRollback(),
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getMessage());
			}

			// Crear respuesta
			DepositResponseDTO depositResponseDTO = new DepositResponseDTO();
			depositResponseDTO.setId(depositId);
			depositResponseDTO.setAmount(depositRequestDTO.getAmount());
			depositResponseDTO.setInterestRate(depositRequestDTO.getInterestRate());
			depositResponseDTO.setTermDays(depositRequestDTO.getTermDays());
			depositResponseDTO.setApplicationDate(applicationDate);
			depositResponseDTO.setMaturityDate(maturityDate);
			depositResponseDTO.setInterestEarned(interestEarned);
			depositResponseDTO.setStatus("ACTIVADO");

			return depositResponseDTO;

		} catch (BusinessException ex) {
			throw ex;  // rethrow para manejar en la transacción
		} catch (Exception ex) {
			LOGGER.error("Error inesperado al registrar depósito", ex);
			throw new BusinessException(
					BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(),
					BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getHasRollback(),
					BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getMessage());
		}
	}



	@Override
	public List<CustomerDepositDTO> executeGetAllCustomerDeposits() {
		LOGGER.info("[EJDPR002] - Iniciando consulta de clientes y depósitos");

		try {
			List<Map<String, Object>> rows = jdbcUtils.queryForList(QUERY_SELECT_ALL_CUSTOMER_DEPOSITS);

			List<CustomerDepositDTO> result = rows.stream().map(row -> {
				CustomerDTO customer = new CustomerDTO();
				customer.setId(((Number) row.get("CUSTOMER_ID")).longValue());
				customer.setAccountNumber((String) row.get("ACCOUNT_NUMBER"));
				customer.setCustomerName((String) row.get("CUSTOMER_NAME"));

				DepositResponseDTO deposit = new DepositResponseDTO();
				deposit.setId(((Number) row.get("DEPOSIT_ID")).longValue());
				deposit.setAmount(((Number) row.get("AMOUNT")).doubleValue());
				deposit.setInterestRate(((Number) row.get("INTEREST_RATE")).doubleValue());
				deposit.setTermDays(((Number) row.get("TERM_DAYS")).intValue());
				deposit.setApplicationDate((Date) row.get("APPLICATION_DATE"));
				deposit.setMaturityDate((Date) row.get("MATURITY_DATE"));

				Object earned = row.get("INTEREST_EARNED");
				deposit.setInterestEarned(earned != null ? ((Number) earned).doubleValue() : 0.0);

				deposit.setStatus((String) row.get("STATUS"));

				return new CustomerDepositDTO(customer, deposit);
			}).collect(Collectors.toList());

			LOGGER.info("[EJDPR001] - Registros cliente-depósito encontrados: {}", result.size());
			return result;

			} catch (DBException ex) {
				LOGGER.error("[EJDPR001] - Error al consultar clientes y depósitos", ex);
				throw new BusinessException(
						BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(),
						BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getHasRollback(),
						BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getMessage());
			}
		catch (Exception e) {
		LOGGER.info("Unexpected error while creating client: {}", e.getMessage());
			throw new BusinessException(
					BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(),
					BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getHasRollback(),
					BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getMessage());
	}
	}

}
