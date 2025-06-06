package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.aso.TimeDepositDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.ejdp.lib.r002.EJDPR002;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class EJDPT00401PETransactionTest {

    private Map<String, Object> parameters;
    private Map<Class<?>, Object> serviceLibraries;

    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @Mock
    private CommonRequestHeader commonRequestHeader;

    @Mock
    private EJDPR002 ejdpR002;

    private final EJDPT00401PETransaction transaction = new EJDPT00401PETransaction() {
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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initializeTransaction();
        setServiceLibrary(EJDPR002.class, ejdpR002);
    }

    private void initializeTransaction() {
        transaction.setContext(new Context());
        parameters = new HashMap<>();
        serviceLibraries = new HashMap<>();
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

    @Test
    public void testExecute_SuccessWithDeposits() throws BusinessException {
        // Preparar datos de prueba
        List<TimeDepositDTO> mockDeposits = new ArrayList<>();
        TimeDepositDTO deposit1 = new TimeDepositDTO();
        deposit1.setId(1L);
        deposit1.setAccountNumber("12345");
        deposit1.setCustomerName("John Doe");
        deposit1.setAmount(1000.0);
        deposit1.setStatus("Active");
        mockDeposits.add(deposit1);

        // Configurar mocks
        when(ejdpR002.executeGetAllTimeDeposits()).thenReturn(mockDeposits);

        // Ejecutar
        transaction.execute();

        // Verificar
        Assert.assertEquals(0, transaction.getAdviceList().size());
        Assert.assertEquals(Severity.OK, transaction.getSeverity());
        Assert.assertEquals(mockDeposits, getParameterFromTransaction("Deposits"));
    }
    @Test
    public void testExecute_WithDeposits_EntersIfCondition() throws BusinessException {
        // 1. Preparar una lista con al menos un depósito
        List<TimeDepositDTO> mockDeposits = new ArrayList<>();
        TimeDepositDTO deposit = new TimeDepositDTO();
        deposit.setId(1L);
        mockDeposits.add(deposit);

        // 2. Configurar el mock para devolver esta lista
        when(ejdpR002.executeGetAllTimeDeposits()).thenReturn(mockDeposits);

        // 3. Ejecutar la transacción
        transaction.execute();

        // 4. Verificaciones simples para confirmar que entró al if
        // Verificar que se establecieron los depósitos
        Assert.assertNotNull(getParameterFromTransaction("Deposits"));
        // Verificar que no hay errores
        Assert.assertTrue(transaction.getAdviceList().isEmpty());
    }

    @Test
    public void testExecute_BusinessException() throws BusinessException {
        // Configurar mocks
        BusinessException mockException = new BusinessException("ERRR00000001", false);
        when(ejdpR002.executeGetAllTimeDeposits()).thenThrow(mockException);

        // Ejecutar
        transaction.execute();

        // Verificar
        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals("ERRR00000001", transaction.getAdviceList().get(0).getCode());
        Assert.assertEquals(Severity.EWR, transaction.getSeverity());
        Assert.assertEquals(new ArrayList<>(), getParameterFromTransaction("Deposits"));
    }

    @Test
    public void testExecute_GenericException() throws BusinessException {
        // Configurar mocks
        when(ejdpR002.executeGetAllTimeDeposits()).thenThrow(new RuntimeException("Error inesperado"));

        // Ejecutar
        transaction.execute();

        // Verificar
        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals(BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(),
                transaction.getAdviceList().get(0).getCode());
        Assert.assertEquals(Severity.EWR, transaction.getSeverity());
        Assert.assertEquals(new ArrayList<>(), getParameterFromTransaction("Deposits"));
    }
}