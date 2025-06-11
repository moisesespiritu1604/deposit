package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.dto.deposit.mock.CustomerDepositMock;
import com.bbva.ejdp.lib.r002.EJDPR002;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.elara.domain.transaction.Severity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class EJDPT00301PETransactionTest {

    private Map<String, Object> parameters;
    private Map<Class<?>, Object> serviceLibraries;

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @Mock
    private CommonRequestHeader commonRequestHeader;

    @Mock
    private EJDPR002 ejdpR002;

    private EJDPT00301PETransaction transaction;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        parameters = new HashMap<>();
        serviceLibraries = new HashMap<>();

        transaction = new EJDPT00301PETransaction() {
            @Override
            protected void addParameter(String field, Object obj) {
                if (parameters != null) {
                    parameters.put(field, obj);
                }
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

        };

        transaction.setContext(new Context());
        serviceLibraries.put(EJDPR002.class, ejdpR002);
    }

    // Tests for field validations
    @Test
    public void testExecute_MissingAccountNumber() {
        setParameterToTransaction("accountNumber", null);
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
        Assert.assertEquals(Severity.ENR, transaction.getSeverity());
    }

    @Test
    public void testExecute_MissingCustomerName() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", null);
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_MissingAmount() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", null);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_MissingInterestRate() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", null);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_MissingTermDays() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", null);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidAccountNumberFormat() {
        setParameterToTransaction("accountNumber", "123ABC");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidCustomerNameLength() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "This customer name is way too long for the field validation");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidAmountZero() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 0.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_AMOUNT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidAmountTooLarge() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 10000000000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_AMOUNT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidInterestRateZero() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 0.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidInterestRateTooHigh() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 101.0);
        setParameterToTransaction("termDays", 30L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidTermDays() {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 0L);

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_TERM_DAYS.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testSetCustomerDepositMock_Coverage() {
        CustomerDepositMock mock = new CustomerDepositMock();
        transaction.setCustomerDepositMock(mock);

        // Verifica que el parámetro se haya guardado con la clave correcta
        Assert.assertEquals(mock, parameters.get("CustomerDepositMock"));
    }
    // Tests for successful execution
    @Test
    public void testExecute_SuccessfulRegistration() throws BusinessException {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        CustomerDepositMock mockResponse = new CustomerDepositMock();
        DepositResponseDTO deposit = new DepositResponseDTO();
        deposit.setId(1L);
        mockResponse.setDeposits(new ArrayList<>());
        mockResponse.getDeposits().add(deposit);

        when(ejdpR002.executeRegisterTimeDeposit(any(DepositRequestDTO.class))).thenReturn(mockResponse);

        transaction.execute();

        Assert.assertEquals(0, transaction.getAdviceList().size());
        Assert.assertEquals(Severity.OK, transaction.getSeverity());

        CustomerDepositMock response = (CustomerDepositMock) getParameterFromTransaction("CustomerDepositMock");
        Assert.assertNotNull("La respuesta no debe ser null", response);
        Assert.assertNotNull("La lista de depósitos no debe ser null", response.getDeposits());
        Assert.assertEquals("Debería haber 1 depósito en la respuesta", 1, response.getDeposits().size());
    }

    @Test
    public void testExecute_ServiceThrowsBusinessException() throws BusinessException {
        setParameterToTransaction("accountNumber", "12345678");
        setParameterToTransaction("customerName", "Test Customer");
        setParameterToTransaction("amount", 1000.0);
        setParameterToTransaction("interestRate", 5.0);
        setParameterToTransaction("termDays", 30L);

        when(ejdpR002.executeRegisterTimeDeposit(any(DepositRequestDTO.class)))
                .thenThrow(new BusinessException("TEST_ERROR", true));

        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals("TEST_ERROR", transaction.getAdviceList().get(0).getCode());
        Assert.assertEquals(Severity.EWR, transaction.getSeverity());
    }

    private void setParameterToTransaction(String parameter, Object value) {
        parameters.put(parameter, value);
    }

    private Object getParameterFromTransaction(String parameter) {
        return parameters.get(parameter);
    }
}