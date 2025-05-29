package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.lib.r001.EJDPR001;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EJDPT00101PETransactionTest {

    private Map<String, Object> parameters;
    private Map<Class<?>, Object> serviceLibraries;

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @Mock
    private CommonRequestHeader commonRequestHeader;

    @Mock
    private EJDPR001 ejdpr001;

    private EJDPT00101PETransaction transaction;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        parameters = new HashMap<>();
        serviceLibraries = new HashMap<>();

        transaction = new EJDPT00101PETransaction() {
            @Override
            protected void addParameter(String field, Object obj) {
                parameters.put(field, obj);
            }
            @Override
            protected Object getParameter(String field) {
                return parameters.get(field);
            }
            @Override
            protected <T> T getServiceLibrary(Class<T> serviceInterface) {
                return (T) serviceLibraries.get(serviceInterface);
            }
            @Override
            public String getProperty(String keyProperty) {
                return applicationConfigurationService.getProperty(keyProperty);
            }
            @Override
            protected CommonRequestHeader getRequestHeader() {
                return commonRequestHeader;
            }
            @Override
            protected void setSeverity(Severity severity) {
                parameters.put("severity", severity);
            }
        };
        transaction.setContext(new Context());
        setServiceLibrary(EJDPR001.class, ejdpr001);
    }

    private void setServiceLibrary(Class<?> clasz, Object mockObject) {
        serviceLibraries.put(clasz, mockObject);
    }

    private void setParameterToTransaction(String parameter, Object value) {
        parameters.put(parameter, value);
    }

    private Object getParameterFromTransaction(String parameter) {
        return parameters.get(parameter);
    }

    private DepositRequestDTO createValidRequest() {
        DepositRequestDTO request = new DepositRequestDTO();
        request.setAccountNumber("12345678");
        request.setCustomerName("Juan Perez");
        request.setAmount(1000.0);
        request.setInterestRate(5.0);
        request.setTermDays(30);
        return request;
    }

    @Test
    public void testExecute_AccountNumberInvalidFormat_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setAccountNumber("abc12345"); // No es numérico y no tiene 8 dígitos
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getCode(),
                transaction.getAdviceList().get(0).getCode());
        assertEquals(Severity.ENR, parameters.get("severity"));
    }

    @Test
    public void testExecute_AccountNumberWrongLength_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setAccountNumber("12375678"); // 7 dígitos
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_CustomerNameBlank_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setCustomerName("");
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_CustomerNameTooLong_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setCustomerName("Nombre demasiado largo para el campo permitido");
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_AmountNull_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setAmount(null);
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_AMOUNT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_AmountZero_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setAmount(0.0);
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_AMOUNT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_AmountTooLong_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setAmount(123456789.0); // 9 dígitos
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_AMOUNT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InterestRateNull_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setInterestRate(null);
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InterestRateZero_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setInterestRate(0.0);
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InterestRateTooHigh_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setInterestRate(101.0);
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_TermDaysNull_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setTermDays(null);
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_TERM_DAYS.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_TermDaysZero_ShouldAddValidationError() {
        DepositRequestDTO request = createValidRequest();
        request.setTermDays(0);
        setParameterToTransaction("depositrequest", request);
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.INVALID_TERM_DAYS.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_BusinessException_ShouldAddBusinessError() throws BusinessException {
        DepositRequestDTO request = createValidRequest();
        setParameterToTransaction("depositrequest", request);
        when(ejdpr001.executeRegisterDeposit(request))
                .thenThrow(new BusinessException(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(),
                        BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getHasRollback(),
                        "El número de cuenta ya existe"));
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(),
                transaction.getAdviceList().get(0).getCode());
        assertEquals(HttpResponseCode.HTTP_CODE_400, parameters.get("httpResponseCode"));
        assertEquals(Severity.EWR, parameters.get("severity"));
    }

    @Test
    public void testExecute_TechnicalException_ShouldAddBusinessError() throws BusinessException {
        DepositRequestDTO request = createValidRequest();
        setParameterToTransaction("depositrequest", request);
        when(ejdpr001.executeRegisterDeposit(request)).thenThrow(new RuntimeException("Error técnico"));
        transaction.execute();
        assertEquals(BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(),
                transaction.getAdviceList().get(0).getCode());
        assertEquals(HttpResponseCode.HTTP_CODE_400, parameters.get("httpResponseCode"));
        assertEquals(Severity.EWR, parameters.get("severity"));
    }

    @Test
    public void testExecute_SuccessfulRequest_ShouldSetSuccessResponse() throws BusinessException {
        DepositRequestDTO request = createValidRequest();
        DepositResponseDTO response = new DepositResponseDTO();
        response.setId(1L);
        response.setAmount(request.getAmount());
        response.setInterestRate(request.getInterestRate());
        response.setTermDays(request.getTermDays());
        setParameterToTransaction("depositrequest", request);
        when(ejdpr001.executeRegisterDeposit(request)).thenReturn(response);
        transaction.execute();
        assertTrue(transaction.getAdviceList().isEmpty());
        assertEquals(HttpResponseCode.HTTP_CODE_200, parameters.get("httpResponseCode"));
        assertEquals(Severity.OK, parameters.get("severity"));
        CustomerDTO customer = (CustomerDTO) getParameterFromTransaction("customer");
        assertNotNull(customer);
        assertEquals(request.getAccountNumber(), customer.getAccountNumber());
        assertEquals(request.getCustomerName(), customer.getCustomerName());
        DepositResponseDTO deposit = (DepositResponseDTO) getParameterFromTransaction("deposit");
        assertNotNull(deposit);
        assertEquals(Long.valueOf(1), deposit.getId());
    }
}