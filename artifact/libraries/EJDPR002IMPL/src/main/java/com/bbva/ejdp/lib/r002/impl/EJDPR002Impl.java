package com.bbva.ejdp.lib.r002.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * The EJDPR002Impl class...
 */
public class EJDPR002Impl extends EJDPR002Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPR002Impl.class);
	private static final String SERVICE_ID = "apiTimeDeposit";
	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		// TODO - Implementation of business logic
	}

	@Override
	public List<TimeDepositDTO> executeGetAllTimeDeposits() {
		List<TimeDepositDTO> deposits = new ArrayList<>();

		try {
			LOGGER.info("Llamando a API externa: {}", SERVICE_ID);

			ResponseEntity<List<TimeDepositDTO>> response = this.internalApiConnector.exchange(
					SERVICE_ID,
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<List<TimeDepositDTO>>() {}
			);

			if (response != null && response.getStatusCode().is2xxSuccessful()) {
				List<TimeDepositDTO> body = response.getBody();
				if (body != null && !body.isEmpty()) {
					deposits = body;
					LOGGER.info("Depósitos obtenidos exitosamente: {} elementos", deposits.size());
				} else {
					LOGGER.warn("La API devolvió una lista vacía de depósitos.");
				}
			} else {
				LOGGER.error("Error en respuesta de API. Status: {}",
						response != null ? response.getStatusCode() : "Respuesta nula");
				throw new BusinessException(
						BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(),
						BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getHasRollback(),
						BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getMessage());
			}

		} catch (BusinessException ex) {
			throw ex; // Re-lanzar si ya es una excepción conocida
		} catch (Exception ex) {
			LOGGER.error("Error técnico al consumir la API de depósitos", ex);
			throw new BusinessException(
					BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(),
					BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getHasRollback(),
					BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getMessage());
		}

		return deposits;
	}
}
