package com.bbva.ejdp.lib.r001.impl;

import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.CustomerDepositDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
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
	public DepositResponseDTO executeRegisterDeposit(CustomerDTO customerDTO, DepositRequestDTO depositRequestDTO, DepositResponseDTO depositResponseDTO) {
		LOGGER.info("[EJDPR001] - Start deposit registration");

		// 1. Obtener ID del cliente
		Map<String, Object> customerIdMap = jdbcUtils.queryForMap(QUERY_NEXT_CUSTOMER_ID);
		Long customerId = ((Number) customerIdMap.get("NEXT_ID")).longValue();

		// 2. Insertar Cliente
		customerDTO.setId(customerId);
		customerDTO.setCustomerName(depositRequestDTO.getCustomerName());
		customerDTO.setAccountNumber(depositRequestDTO.getAccountNumber());

		Map<String, Object> customerParams = new HashMap<>();
		customerParams.put("ID", customerDTO.getId());
		customerParams.put("ACCOUNT_NUMBER", customerDTO.getAccountNumber());
		customerParams.put("CUSTOMER_NAME", customerDTO.getCustomerName());

		LOGGER.info("[EJDPR001] - Parámetros INSERT_CUSTOMER_SQL: {}", customerParams);
		jdbcUtils.update(QUERY_INSERT_CUSTOMER, customerParams);

		// 3. Preparar cálculo del depósito
		Date applicationDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(applicationDate);
		calendar.add(Calendar.DAY_OF_MONTH, depositRequestDTO.getTermDays());
		Date maturityDate = calendar.getTime();

		Double interestEarned = depositRequestDTO.getAmount()
				* depositRequestDTO.getInterestRate() / 100
				* depositRequestDTO.getTermDays() / 365.0;

		// 4. Obtener ID del depósito
		Map<String, Object> depositIdMap = jdbcUtils.queryForMap(QUERY_NEXT_DEPOSIT_ID);
		Long depositId = ((Number) depositIdMap.get("NEXT_ID")).longValue();

		// 5. Crear objeto respuesta depósito
		depositResponseDTO.setId(depositId);
		depositResponseDTO.setAmount(depositRequestDTO.getAmount());
		depositResponseDTO.setInterestRate(depositRequestDTO.getInterestRate());
		depositResponseDTO.setTermDays(depositRequestDTO.getTermDays());
		depositResponseDTO.setApplicationDate(applicationDate);
		depositResponseDTO.setMaturityDate(maturityDate);
		depositResponseDTO.setInterestEarned(interestEarned);
		depositResponseDTO.setStatus("ACTIVADO");

		Map<String, Object> depositParams = new HashMap<>();
		depositParams.put("ID", depositResponseDTO.getId());
		depositParams.put("CUSTOMER_ID", customerDTO.getId());
		depositParams.put("AMOUNT", depositResponseDTO.getAmount());
		depositParams.put("INTEREST_RATE", depositResponseDTO.getInterestRate());
		depositParams.put("TERM_DAYS", depositResponseDTO.getTermDays());
		depositParams.put("APPLICATION_DATE", depositResponseDTO.getApplicationDate());
		depositParams.put("MATURITY_DATE", depositResponseDTO.getMaturityDate());
		depositParams.put("INTEREST_EARNED", depositResponseDTO.getInterestEarned());
		depositParams.put("STATUS", depositResponseDTO.getStatus());

		LOGGER.info("[EJDPR001] - Parámetros INSERT_DEPOSIT_SQL: {}", depositParams);
		jdbcUtils.update(QUERY_INSERT_DEPOSIT, depositParams);

		LOGGER.info("[EJDPR001] - Output depositResponseDTO: {}", depositResponseDTO);
		LOGGER.info("[EJDPR001] - End deposit registration");

		return depositResponseDTO;
	}

	@Override
	public List<CustomerDepositDTO> executeGetAllCustomerDeposits() {
		LOGGER.info("[EJDPR002] - Iniciando consulta de clientes y depósitos");

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

		LOGGER.info("[EJDPR002] - Registros cliente-depósito encontrados: {}", result.size());
		return result;
	}



}
