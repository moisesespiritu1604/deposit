package com.bbva.ejdp;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.ejdp.dto.deposit.CustomerDepositDTO;
import com.bbva.ejdp.lib.r001.EJDPR001;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.mockito.Mockito.*;

public class EJDPT00201PETransactionTest {

    private Map<String, Object> parameters;

    private Map<Class<?>, Object> serviceLibraries;

    private final EJDPT00201PETransaction transaction = new EJDPT00201PETransaction() {
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
    };

    private EJDPR001 ejdpR001;

    @Before

    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // INICIALIZA PRIMERO LOS MAPAS
        parameters = new HashMap<>();
        serviceLibraries = new HashMap<>();
        ejdpR001 = mock(EJDPR001.class);
        serviceLibraries.put(EJDPR001.class, ejdpR001);

        // LUEGO el context
        transaction.setContext(new Context());
    }


    @Test
    public void execute_success() {
        List<CustomerDepositDTO> mockDeposits = Arrays.asList(new CustomerDepositDTO(), new CustomerDepositDTO());
        when(ejdpR001.executeGetAllCustomerDeposits()).thenReturn(mockDeposits);

        transaction.execute();

        Assert.assertEquals(mockDeposits, parameters.get("CustomerList"));
        Assert.assertEquals(Severity.OK, transaction.getSeverity());
    }

    @Test
    public void execute_businessException() {
        BusinessException businessException = new BusinessException("EJDP00000001", true, "Error al listar");
        when(ejdpR001.executeGetAllCustomerDeposits()).thenThrow(businessException);

        transaction.execute();


        Assert.assertEquals(Severity.EWR, transaction.getSeverity());
        Assert.assertEquals(1, transaction.getAdviceList().size());
        Assert.assertEquals("EJDP00000001", transaction.getAdviceList().get(0).getCode());
    }
}
