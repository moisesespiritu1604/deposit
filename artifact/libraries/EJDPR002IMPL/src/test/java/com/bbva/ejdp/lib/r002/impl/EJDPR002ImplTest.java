package com.bbva.ejdp.lib.r002.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.dto.deposit.mock.CustomerDepositMock;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.utility.api.connector.APIConnector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import com.bbva.apx.exception.io.network.TimeoutException;

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

    // --------------------------------------
    // Tests for executeGetAllTimeDeposits()
    // --------------------------------------

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
        ResponseEntity<List<TimeDepositDTO>> response = new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
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
            Assert.assertEquals(BusinessExceptionEJDP.NO_DEPOSITS_FOUND.getCode(), ex.getAdviceCode());
        }
    }

    @Test
    public void testExecuteGetAllTimeDeposits_NullResponse() {
        // Configurar mock para devolver respuesta nula
        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(null);

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debería lanzar BusinessException para respuesta nula");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getMessage(), ex.getMessage());
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
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(), ex.getAdviceCode());
        }
    }

    @Test
    public void testExecuteGetAllTimeDeposits_RestClientException() {
        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new RestClientException("Error de cliente REST"));

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debe lanzar BusinessException");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_UNAVAILABLE.getCode(), ex.getAdviceCode());
        }
    }
    @Test
    public void testExecuteGetAllTimeDeposits_HttpClientError() {
        // Configurar mock para lanzar error 4xx
        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debería lanzar BusinessException");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getMessage(), ex.getMessage());
        }
    }

    @Test
    public void testExecuteGetAllTimeDeposits_HttpServerError() {
        // Configurar mock para lanzar error 5xx
        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debería lanzar BusinessException");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getMessage(), ex.getMessage());
        }
    }
    @Test
    public void testExecuteGetAllTimeDeposits_Timeout() {
        // Configurar mock para lanzar TimeoutException
        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenThrow(new TimeoutException("Timeout simulado"));

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debería lanzar BusinessException");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_TIMEOUT.getMessage(), ex.getMessage());
        }
    }
    @Test
    public void testExecuteRegisterTimeDeposit_NullResponseBody() {
        DepositRequestDTO request = new DepositRequestDTO();

        // Configurar mock para devolver respuesta con body nulo
        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(), ex.getAdviceCode());
            Assert.assertTrue(ex.getMessage().contains("no se recibió el depósito registrado"));
        }
    }
    @Test
    public void testExecuteGetAllTimeDeposits_SuccessWithNullBody() {
        // Configurar mock para devolver respuesta exitosa pero con body nulo
        ResponseEntity<List<TimeDepositDTO>> response =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debería lanzar BusinessException para body nulo");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.NO_DEPOSITS_FOUND.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.NO_DEPOSITS_FOUND.getMessage(), ex.getMessage());
        }
    }
    // --------------------------------------------
    // Tests for executeRegisterTimeDeposit()
    // --------------------------------------------

    @Test
    public void testExecuteRegisterTimeDeposit_Success() {
        DepositRequestDTO request = new DepositRequestDTO();
        CustomerDepositMock mockResponse = new CustomerDepositMock();

        // Crear un TimeDepositDTO (que es lo que realmente espera CustomerDepositMock)
        DepositResponseDTO deposit = new DepositResponseDTO();
        deposit.setId(1L);

        // Configurar la lista de depósitos en el mock
        mockResponse.setDeposits(Collections.singletonList(deposit));

        // Configurar el mock del API connector
        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.CREATED));

        // Ejecutar el método bajo prueba
        CustomerDepositMock result = ejdpR002.executeRegisterTimeDeposit(request);

        // Verificaciones
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.getDeposits().size());
        Assert.assertEquals(Long.valueOf(1), result.getDeposits().get(0).getId());
    }

    @Test(expected = BusinessException.class)
    public void testExecuteRegisterTimeDeposit_NullResponse() {
        DepositRequestDTO request = new DepositRequestDTO();
        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(null);

        ejdpR002.executeRegisterTimeDeposit(request);
    }

    @Test(expected = BusinessException.class)
    public void testExecuteRegisterTimeDeposit_EmptyDeposits() {
        DepositRequestDTO request = new DepositRequestDTO();
        CustomerDepositMock mockResponse = new CustomerDepositMock();
        mockResponse.setDeposits(Collections.emptyList());

        ResponseEntity<CustomerDepositMock> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.CREATED);

        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(responseEntity);

        ejdpR002.executeRegisterTimeDeposit(request);
    }

    @Test(expected = BusinessException.class)
    public void testExecuteRegisterTimeDeposit_ClientError() {
        DepositRequestDTO request = new DepositRequestDTO();

        ResponseEntity<CustomerDepositMock> responseEntity =
                new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(responseEntity);

        ejdpR002.executeRegisterTimeDeposit(request);
    }

    @Test(expected = BusinessException.class)
    public void testExecuteRegisterTimeDeposit_Conflict409() {
        DepositRequestDTO request = new DepositRequestDTO();

        HttpStatusCodeException conflictException = new HttpStatusCodeException(HttpStatus.CONFLICT) {};
        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenThrow(conflictException);

        ejdpR002.executeRegisterTimeDeposit(request);
    }
    @Test
    public void testExecuteRegisterTimeDeposit_409Conflict() {
        DepositRequestDTO request = new DepositRequestDTO();

        // Configurar mock para lanzar error 409 Conflict
        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.CONFLICT));

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException para error 409");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getMessage(), ex.getMessage());
        }
    }
    @Test(expected = BusinessException.class)
    public void testExecuteRegisterTimeDeposit_Timeout() {
        DepositRequestDTO request = new DepositRequestDTO();
        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenThrow(new TimeoutException("timeout"));

        ejdpR002.executeRegisterTimeDeposit(request);
    }

    @Test(expected = BusinessException.class)
    public void testExecuteRegisterTimeDeposit_RestClientException() {
        DepositRequestDTO request = new DepositRequestDTO();
        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenThrow(new RestClientException("fallo"));

        ejdpR002.executeRegisterTimeDeposit(request);
    }
    @Test
    public void testExecuteRegisterTimeDeposit_ConflictError() {
        DepositRequestDTO request = new DepositRequestDTO();

        // Configurar mock para lanzar error 409 Conflict
        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.CONFLICT));

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getMessage(), ex.getMessage());
        }
    }
    @Test
    public void testExecuteRegisterTimeDeposit_EmptyDepositList() {
        DepositRequestDTO request = new DepositRequestDTO();
        CustomerDepositMock mockResponse = new CustomerDepositMock();
        mockResponse.setDeposits(Collections.emptyList());

        // Configurar mock para devolver respuesta con lista vacía
        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.INCOMPLETE_DEPOSIT_DATA.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.INCOMPLETE_DEPOSIT_DATA.getMessage(), ex.getMessage());
        }
    }
    @Test
    public void testExecuteRegisterTimeDeposit_4xxErrorResponse() {
        DepositRequestDTO request = new DepositRequestDTO();

        // Configurar mock para devolver respuesta con error 4xx
        ResponseEntity<CustomerDepositMock> errorResponse =
                new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(errorResponse);

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException para error 4xx");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getMessage(), ex.getMessage());
        }
    }
    @Test
    public void testExecuteGetAllTimeDeposits_ResponseWith5xxError() {
        // Configurar mock para devolver respuesta con error 5xx
        ResponseEntity<List<TimeDepositDTO>> errorResponse =
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);

        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(errorResponse);

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debería lanzar BusinessException para error 5xx");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_SERVER_ERROR.getMessage(), ex.getMessage());
        }
    }
    @Test
    public void testExecuteRegisterTimeDeposit_NullDepositsList() {
        DepositRequestDTO request = new DepositRequestDTO();
        CustomerDepositMock mockResponse = new CustomerDepositMock();
        mockResponse.setDeposits(null); // Lista de depósitos nula

        // Configurar mock para devolver respuesta exitosa pero con deposits nulo
        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.CREATED));

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException para deposits nulo");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.INCOMPLETE_DEPOSIT_DATA.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.INCOMPLETE_DEPOSIT_DATA.getMessage(), ex.getMessage());
        }
    }
    // 1. Excepción 409 CONFLICT
    @Test
    public void testExecuteRegisterTimeDeposit_HttpStatusCodeException_Conflict() {
        DepositRequestDTO request = new DepositRequestDTO();
        HttpStatusCodeException conflictException = new HttpStatusCodeException(HttpStatus.CONFLICT) {};
        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenThrow(conflictException);

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException para 409 Conflict");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(), ex.getAdviceCode());
        }
    }

    // 2. Excepción 4xx distinta de 409
    @Test
    public void testExecuteRegisterTimeDeposit_HttpStatusCodeException_Other4xx() {
        DepositRequestDTO request = new DepositRequestDTO();
        HttpStatusCodeException badRequestException = new HttpStatusCodeException(HttpStatus.BAD_REQUEST) {};
        when(internalApiConnector.postForEntity(
                anyString(),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenThrow(badRequestException);

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debería lanzar BusinessException para error 4xx");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(), ex.getAdviceCode());
        }
    }

    // 3. Respuesta 4xx en el response (no excepción)

    @Test
    public void testExecuteGetAllTimeDeposits_Response4xxClientError() {
        // Simula una respuesta 4xx (por ejemplo, 404 Not Found)
        ResponseEntity<List<TimeDepositDTO>> response =
                new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);

        when(internalApiConnector.exchange(
                eq("apiTimeDeposit"),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(response);

        try {
            ejdpR002.executeGetAllTimeDeposits();
            Assert.fail("Debe lanzar BusinessException para error 4xx");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(), ex.getAdviceCode());
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getMessage(), ex.getMessage());
        }
    }
    // Test para error 4xx (cliente)
    @Test
    public void testExecuteRegisterTimeDeposit_Response4xxClientError() {
        DepositRequestDTO request = new DepositRequestDTO();
        ResponseEntity<CustomerDepositMock> response =
                new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400

        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(response);

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debe lanzar BusinessException para error 4xx");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.DEPOSIT_API_CLIENT_ERROR.getCode(), ex.getAdviceCode());
        }
    }

    // Test para error 5xx (servidor)
    @Test
    public void testExecuteRegisterTimeDeposit_Response5xxServerError() {
        DepositRequestDTO request = new DepositRequestDTO();
        ResponseEntity<CustomerDepositMock> response =
                new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500

        when(internalApiConnector.postForEntity(
                eq("apiTimeDepositRegister"),
                any(HttpEntity.class),
                eq(CustomerDepositMock.class)
        )).thenReturn(response);

        try {
            ejdpR002.executeRegisterTimeDeposit(request);
            Assert.fail("Debe lanzar BusinessException para error 5xx");
        } catch (BusinessException ex) {
            Assert.assertEquals(BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(), ex.getAdviceCode());
        }
    }
}
