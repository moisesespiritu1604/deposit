// archivo: artifact/libraries/EJDPR002IMPL/src/test/java/com/bbva/ejdp/lib/r002/impl/EJDPR002ImplTest.java
package com.bbva.ejdp.lib.r002.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.utility.api.connector.APIConnector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import javax.validation.constraints.Null;
import java.util.*;

import static org.mockito.Mockito.*;

public class EJDPR002ImplTest {
	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@InjectMocks
	private EJDPR002Impl ejdpR002;

	@Mock
	private APIConnector internalApiConnector;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testExecuteGetAllTimeDeposits_SuccessWithDeposits() {
		List<TimeDepositDTO> mockList = new ArrayList<>();
		TimeDepositDTO deposit = new TimeDepositDTO();
		deposit.setId(1L);
		mockList.add(deposit);

		ResponseEntity<List<TimeDepositDTO>> response = new ResponseEntity<>(mockList, HttpStatus.OK);

		when(internalApiConnector.exchange(
				anyString(),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		)).thenReturn(response);

		List<TimeDepositDTO> result = ejdpR002.executeGetAllTimeDeposits();
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(Long.valueOf(1), result.get(0).getId());
	}

	@Test
	public void testExecuteGetAllTimeDeposits_SuccessEmptyList() {
		ResponseEntity<List<TimeDepositDTO>> response = new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);

		when(internalApiConnector.exchange(
				anyString(),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		)).thenReturn(response);

		List<TimeDepositDTO> result = ejdpR002.executeGetAllTimeDeposits();
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void testExecuteGetAllTimeDeposits_NullResponse() {
		when(internalApiConnector.exchange(
				anyString(),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		)).thenReturn(null);

		try {
			ejdpR002.executeGetAllTimeDeposits();
			Assert.fail("Debe lanzar BusinessException");
		} catch (BusinessException ex) {
			Assert.assertEquals(BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(), ex.getAdviceCode());
		}
	}

	@Test
	public void testExecuteGetAllTimeDeposits_ErrorStatus() {
		ResponseEntity<List<TimeDepositDTO>> response = new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

		when(internalApiConnector.exchange(
				anyString(),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		)).thenReturn(response);

		try {
			ejdpR002.executeGetAllTimeDeposits();
			Assert.fail("Debe lanzar BusinessException");
		} catch (BusinessException ex) {
			Assert.assertEquals(BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(), ex.getAdviceCode());
		}
	}

	@Test
	public void testExecuteGetAllTimeDeposits_TechnicalException() {
		when(internalApiConnector.exchange(
				anyString(),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		)).thenThrow(new RuntimeException("Error técnico"));

		try {
			ejdpR002.executeGetAllTimeDeposits();
			Assert.fail("Debe lanzar BusinessException");
		} catch (BusinessException ex) {
			Assert.assertEquals(BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(), ex.getAdviceCode());
		}
	}
	@Test
	public void testExecuteGetAllTimeDeposits_WithNonEmptyBody() {
		// 1. Preparar datos de prueba - lista con un elemento
		List<TimeDepositDTO> mockDeposits = new ArrayList<>();
		TimeDepositDTO deposit = new TimeDepositDTO();
		deposit.setId(1L);
		mockDeposits.add(deposit);

		// 2. Configurar mock para devolver lista no vacía
		when(internalApiConnector.exchange(
				eq("apiTimeDeposit"),
				eq(HttpMethod.GET),
				eq(null),
				any(ParameterizedTypeReference.class)
		)).thenReturn(new ResponseEntity<>(mockDeposits, HttpStatus.OK));

		// 3. Ejecutar método bajo prueba
		List<TimeDepositDTO> result = ejdpR002.executeGetAllTimeDeposits();

		// 4. Verificaciones simples
		Assert.assertNotNull("La lista resultante no debe ser nula", result);
		Assert.assertEquals("Debe contener 1 elemento", 1, result.size());
		Assert.assertEquals("ID del depósito debe coincidir", Long.valueOf(1), result.get(0).getId());
	}
}