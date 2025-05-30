// archivo: artifact/transactions/EJDPT001-01-PE/src/test/java/com/bbva/ejdp/EJDPT00101PETransactionTest.java
package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.lib.r001.EJDPR001;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.elara.domain.transaction.Severity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class EJDPT00101PETransactionTest {

    private Map<String, Object> parameters;
    private Map<Class<?>, Object> serviceLibraries;

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @Mock
    private CommonRequestHeader commonRequestHeader;

    @Mock
    private EJDPR001 ejdpR001;

    private EJDPT00101PETransaction transaction;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        parameters = new HashMap<>();
        serviceLibraries = new HashMap<>();

        transaction = new EJDPT00101PETransaction() {
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
        serviceLibraries.put(EJDPR001.class, ejdpR001);
    }
    @Test
    public void testExecute_NullRequest() {
        parameters.put("DepositRequest", null);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
        Assert.assertEquals(Severity.ENR, transaction.getSeverity());
    }
    @Test
    public void testExecute_MissingAccountNumber() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setCustomerName("Test Customer");
        dto.setAmount(1000.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(30);

        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_MissingAccountNumber2() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678");
        dto.setAmount(1000.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(30);

        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }@Test
    public void testExecute_MissingAccountNumber3() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678");
        dto.setCustomerName("Test Customer");
        dto.setInterestRate(5.0);
        dto.setTermDays(30);

        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_MissingAccountNumber4() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678");
        dto.setCustomerName("Test Customer");
        dto.setAmount(1000.0);
        dto.setTermDays(30);

        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_MissingAccountNumber5() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678");
        dto.setCustomerName("Test Customer");
        dto.setAmount(1000.0);
        dto.setInterestRate(5.0);

        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.MANDATORY_FIELD_MISSING.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_InvalidAccountNumber() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12AB"); // inválido
        dto.setCustomerName("Cliente Test");
        dto.setAmount(1000.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(30);
        parameters.put("DepositRequest", dto);
        transaction.execute();
        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(
                BusinessExceptionEJDP.INVALID_ACCOUNT_NUMBER_FORMAT.getCode(),
                transaction.getAdviceList().get(0).getCode()
        );
    }
    @Test
    public void testExecute_InvalidCustomerNameLength() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678"); // inválido
        dto.setCustomerName("Nombre de cliente demasiado largo para el campo");
        dto.setAmount(1000.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(30);
        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_CUSTOMER_NAME.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_InvalidAmountZero(){
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678"); // inválido
        dto.setCustomerName("Moises Espiritu");
        dto.setAmount(0.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(30);
        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_AMOUNT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_InvalidAmountTooLarge(){
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678"); // inválido
        dto.setCustomerName("Moises Espiritu");
        dto.setAmount(10000002200.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(30);
        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_AMOUNT.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }

    @Test
    public void testExecute_InvalidInterestRateZero(){
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678"); // inválido
        dto.setCustomerName("Moises Espiritu");
        dto.setAmount(1000.0);
        dto.setInterestRate(0.0);
        dto.setTermDays(30);
        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_InvalidInterestRateTooHigh(){
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678"); // inválido
        dto.setCustomerName("Moises Espiritu");
        dto.setAmount(10000.0);
        dto.setInterestRate(105.0);
        dto.setTermDays(30);
        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_INTEREST_RATE.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_InvalidTermDays(){
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678"); // inválido
        dto.setCustomerName("Moises Espiritu");
        dto.setAmount(10000.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(0);
        parameters.put("DepositRequest", dto);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.INVALID_TERM_DAYS.getCode(),
                transaction.getAdviceList().get(0).getCode());
    }
    @Test
    public void testExecute_SuccessfulDeposit() throws BusinessException {
        DepositRequestDTO request = createValidRequest();
        DepositResponseDTO mockResponse = createMockResponse();

        when(ejdpR001.executeRegisterDeposit(request)).thenReturn(mockResponse);

        parameters.put("DepositRequest", request);
        transaction.execute();

        // Verificar salidas
        CustomerDTO customer = (CustomerDTO) parameters.get("Customer");
        DepositResponseDTO deposit = (DepositResponseDTO) parameters.get("Deposit");

        Assert.assertNotNull(customer);
        Assert.assertNotNull(deposit);
        Assert.assertEquals(request.getAccountNumber(), customer.getAccountNumber());
        Assert.assertEquals(mockResponse.getId(), deposit.getId());
        Assert.assertEquals(Severity.OK, transaction.getSeverity());
    }
    @Test
    public void testExecute_ServiceThrowsBusinessException() throws BusinessException {
        DepositRequestDTO request = createValidRequest();

        when(ejdpR001.executeRegisterDeposit(request))
                .thenThrow(new BusinessException("TEST_ERROR", true));

        parameters.put("DepositRequest", request);
        transaction.execute();

        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals("TEST_ERROR", transaction.getAdviceList().get(0).getCode());
        Assert.assertEquals(Severity.EWR, transaction.getSeverity());
    }
    private DepositRequestDTO createValidRequest() {
        DepositRequestDTO dto = new DepositRequestDTO();
        dto.setAccountNumber("12345678");
        dto.setCustomerName("Cliente Valido");
        dto.setAmount(1000.0);
        dto.setInterestRate(5.0);
        dto.setTermDays(30);
        return dto;
    }

    private DepositResponseDTO createMockResponse() {
        DepositResponseDTO response = new DepositResponseDTO();
        response.setId(1L);
        response.setAmount(1000.0);
        response.setInterestRate(5.0);
        response.setTermDays(30);
        response.setStatus("ACTIVE");
        // Set other required fields
        return response;
    }
}