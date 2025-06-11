package com.bbva.ejdp.lib.r002.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.apx.exception.io.network.TimeoutException;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.dto.deposit.mock.CustomerDepositMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import java.util.List;

/**
 * The EJDPR002Impl class...
 */
public class EJDPR002Impl extends EJDPR002Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(EJDPR002Impl.class);
	private static final String SERVICE_ID = "apiTimeDeposit";
	private static final String SERVICE_REGISTER = "apiTimeDepositRegister";
	/**
	 * The execute method...
	 */
	@Override
	public void execute() {
		// TODO - Implementation of business logic
	}

	@Override
	public List<TimeDepositDTO> executeGetAllTimeDeposits() {
		List<TimeDepositDTO> deposits;

		try {
			LOGGER.info("Llamando a API externa: {}", SERVICE_ID);

			ResponseEntity<List<TimeDepositDTO>> response = this.internalApiConnector.exchange(
					SERVICE_ID,
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<>() {}
			);

			if (response != null && response.getStatusCode().is2xxSuccessful()) {
				List<TimeDepositDTO> body = response.getBody();
				if (body != null && !body.isEmpty()) {
					deposits = body;
					LOGGER.info("Depósitos obtenidos exitosamente: {} elementos", deposits.size());
				} else {
					LOGGER.warn("La API devolvió una lista vacía de depósitos.");
					throw new BusinessException(
							BusinessExceptionEJDP.NO_DEPOSITS_FOUND.getCode(),
							BusinessExceptionEJDP.NO_DEPOSITS_FOUND.getHasRollback(),
							BusinessExceptionEJDP.NO_DEPOSITS_FOUND.getMessage()
					);
				}
			} else {
				LOGGER.error("Error en respuesta de API. Status: {}",
						response != null ? response.getStatusCode() : "Respuesta nula");

				// Manejo diferenciado por tipo de error HTTP
				if (response != null && response.getStatusCode().is4xxClientError()) {
					throw new BusinessException(
							BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(),
							BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getHasRollback(),
							BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getMessage()
					);
				} else {
					throw new BusinessException(
							BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getCode(),
							BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getHasRollback(),
							BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getMessage()
					);
				}
			}

		} catch (HttpStatusCodeException ex) {
			LOGGER.error("Respuesta HTTP con error al consultar depósitos. Status: {}", ex.getStatusCode(), ex);

			// Diferenciación entre errores 4xx y 5xx
			if (ex.getStatusCode().is4xxClientError()) {
				throw new BusinessException(
						BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(),
						BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getHasRollback(),
						BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getMessage()
				);
			} else {
				throw new BusinessException(
						BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getCode(),
						BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getHasRollback(),
						BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getMessage()
				);
			}
		} catch (TimeoutException ex) {
			LOGGER.error("Timeout de APX al consultar la API de depósitos", ex);
			throw new BusinessException(
					BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getCode(),
					BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getHasRollback(),
					BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getMessage()
			);
		} catch (RestClientException ex) {
			LOGGER.error("Error de conectividad al consultar la API de depósitos", ex);
			throw new BusinessException(
					BusinessExceptionEJDP.DEPOSIT_API_UNAVAILABLE.getCode(),
					BusinessExceptionEJDP.DEPOSIT_API_UNAVAILABLE.getHasRollback(),
					BusinessExceptionEJDP.DEPOSIT_API_UNAVAILABLE.getMessage()
			);
		}

		return deposits;
	}

	@Override
	public CustomerDepositMock executeRegisterTimeDeposit(DepositRequestDTO requestDTO) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<DepositRequestDTO> httpEntity = new HttpEntity<>(requestDTO, headers);

			LOGGER.info("Llamando a API externa para registrar depósito: {}", SERVICE_REGISTER);

			ResponseEntity<CustomerDepositMock> response = this.internalApiConnector.postForEntity(
					SERVICE_REGISTER,
					httpEntity,
					CustomerDepositMock.class
			);

			if (response == null) {
				LOGGER.error("Error en respuesta de API al registrar depósito. Status: Respuesta nula");
				throw new BusinessException(
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(),
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getHasRollback(),
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getMessage()
				);
			}

			if (!response.getStatusCode().is2xxSuccessful()) {
				LOGGER.error("Error en respuesta de API al registrar depósito. Status: {}", response.getStatusCode());

				if (response.getStatusCode().is4xxClientError()) {
					throw new BusinessException(
							BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(),
							BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getHasRollback(),
							BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getMessage()
					);
				} else {
					throw new BusinessException(
							BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(),
							BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getHasRollback(),
							BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getMessage()
					);
				}
			}

			CustomerDepositMock responseBody = response.getBody();
			if (responseBody == null) {
				LOGGER.warn("La API devolvió un body nulo al registrar el depósito.");
				throw new BusinessException(
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(),
						BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getHasRollback(),
						"La respuesta de la API fue exitosa pero no se recibió el depósito registrado."
				);
			}

			// Validación de integridad de datos
			if (responseBody.getDeposits() == null || responseBody.getDeposits().isEmpty()) {
				LOGGER.warn("⚠️ La lista de depósitos vino vacía.");
				throw new BusinessException(
						BusinessExceptionEJDP.INCOMPLETE_DEPOSIT_DATA.getCode(),
						BusinessExceptionEJDP.INCOMPLETE_DEPOSIT_DATA.getHasRollback(),
						BusinessExceptionEJDP.INCOMPLETE_DEPOSIT_DATA.getMessage()
				);
			} else {
				LOGGER.info("✔️ Se recibieron {} depósitos. Ejemplo ID: {}",
						responseBody.getDeposits().size(),
						responseBody.getDeposits().get(0).getId()
				);
			}

			LOGGER.info("Cliente asociado: {}", responseBody.getCustomer());
			return responseBody;

		} catch (HttpStatusCodeException ex) {
			LOGGER.error("Error HTTP al registrar el depósito. Status: {}", ex.getStatusCode(), ex);

			// Diferenciación entre errores 4xx y 5xx

				// Manejo específico para conflictos (409) - cuentas duplicadas
			if (ex.getStatusCode()==HttpStatus.CONFLICT) {
				throw new BusinessException(
						BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(),
						BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getHasRollback(),
						BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getMessage()
					);
			} else {
				throw new BusinessException(
						BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(),
						BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getHasRollback(),
						BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getMessage());
			}

		} catch (TimeoutException ex) {
			LOGGER.error("Timeout de APX al registrar el depósito", ex);
			throw new BusinessException(
					BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getCode(),
					BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getHasRollback(),
					BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getMessage()
			);
		} catch (RestClientException ex) {
			LOGGER.error("Error de conectividad al registrar el depósito", ex);
			throw new BusinessException(
					BusinessExceptionEJDP.DEPOSIT_API_UNAVAILABLE.getCode(),
					BusinessExceptionEJDP.DEPOSIT_API_UNAVAILABLE.getHasRollback(),
					BusinessExceptionEJDP.DEPOSIT_API_UNAVAILABLE.getMessage()
			);
		}
	}



}
