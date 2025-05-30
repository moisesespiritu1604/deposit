package com.bbva.ejdp.lib.r001.impl;

import com.bbva.apx.exception.business.BusinessException;
import com.bbva.apx.exception.db.DBException;
import com.bbva.apx.exception.db.DuplicateKeyException;
import com.bbva.ejdp.dto.deposit.CustomerDTO;
import com.bbva.ejdp.dto.deposit.CustomerDepositDTO;
import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
import com.bbva.ejdp.dto.deposit.enums.BusinessExceptionEJDP;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.utility.jdbc.JdbcUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EJDPR001ImplTest {
    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @Mock
    private JdbcUtils jdbcUtils; // Utilidad simulada para acceder a la base de datos

    @InjectMocks
    private EJDPR001Impl ejdpR001; // Clase bajo prueba con dependencias inyectadas

    private DepositRequestDTO depositRequestDTO; // DTO que se reutiliza en varios tests

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        // Inicializa un depósito válido como base para los tests
        depositRequestDTO = new DepositRequestDTO();
        depositRequestDTO.setAccountNumber("12345678");
        depositRequestDTO.setCustomerName("Cliente Test");
        depositRequestDTO.setAmount(1000.0);
        depositRequestDTO.setInterestRate(5.0);
        depositRequestDTO.setTermDays(30);
    }

    @Test
    public void executeRegisterDeposit_Success() {
        // Simula que la cuenta no existe previamente
        when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
                .thenReturn(Collections.emptyList());

        // Simula la obtención de nuevos IDs para cliente y depósito
        Map<String, Object> customerIdMap = new HashMap<>();
        customerIdMap.put("NEXT_ID", 100L);
        Map<String, Object> depositIdMap = new HashMap<>();
        depositIdMap.put("NEXT_ID", 200L);

        when(jdbcUtils.queryForMap("EJDP.NEXT_CUSTOMER_ID")).thenReturn(customerIdMap);
        when(jdbcUtils.queryForMap("EJDP.NEXT_DEPOSIT_ID")).thenReturn(depositIdMap);

        // Simula inserciones exitosas
        when(jdbcUtils.update(eq("EJDP.INSERT_CUSTOMER_SQL"), anyMap())).thenReturn(1);
        when(jdbcUtils.update(eq("EJDP.QUERY_INSERT_DEPOSIT"), anyMap())).thenReturn(1);

        // Ejecuta el método bajo prueba
        DepositResponseDTO response = ejdpR001.executeRegisterDeposit(depositRequestDTO);

        // Valida que la respuesta tenga los valores esperados
        assertNotNull(response);
        assertEquals(Long.valueOf(200L), response.getId());
        assertEquals(depositRequestDTO.getAmount(), response.getAmount(), 0.001);
        assertEquals("ACTIVADO", response.getStatus());
    }

    @Test
    public void executeRegisterDeposit_DuplicateAccountNumber() {
        // Simula que ya existe una cuenta con el mismo número
        List<Map<String, Object>> existingAccounts = new ArrayList<>();
        existingAccounts.add(new HashMap<>());

        when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
                .thenReturn(existingAccounts);

        // Verifica que se lanza la excepción esperada
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            ejdpR001.executeRegisterDeposit(depositRequestDTO);
        });

        // Confirma el código de error específico
        assertEquals(BusinessExceptionEJDP.DUPLICATE_ACCOUNT_NUMBER.getCode(), ex.getAdviceCode());
    }

    @Test
    public void executeGetAllCustomerDeposits_InterestEarnedCases() {
        // Simula dos depósitos: uno con interés calculado y otro sin
        List<Map<String, Object>> mockRows = new ArrayList<>();

        // Registro con interés ganado
        Map<String, Object> rowWithInterest = new HashMap<>();
        rowWithInterest.put("CUSTOMER_ID", 100L);
        rowWithInterest.put("ACCOUNT_NUMBER", "12345678");
        rowWithInterest.put("CUSTOMER_NAME", "Cliente Con Interés");
        rowWithInterest.put("DEPOSIT_ID", 200L);
        rowWithInterest.put("AMOUNT", 1000.0);
        rowWithInterest.put("INTEREST_RATE", 5.0);
        rowWithInterest.put("TERM_DAYS", 30);
        rowWithInterest.put("APPLICATION_DATE", new Date());
        rowWithInterest.put("MATURITY_DATE", new Date());
        rowWithInterest.put("INTEREST_EARNED", 25.50);
        rowWithInterest.put("STATUS", "ACTIVADO");
        mockRows.add(rowWithInterest);

        // Registro sin interés ganado
        Map<String, Object> rowWithoutInterest = new HashMap<>();
        rowWithoutInterest.put("CUSTOMER_ID", 101L);
        rowWithoutInterest.put("ACCOUNT_NUMBER", "87654321");
        rowWithoutInterest.put("CUSTOMER_NAME", "Cliente Sin Interés");
        rowWithoutInterest.put("DEPOSIT_ID", 201L);
        rowWithoutInterest.put("AMOUNT", 2000.0);
        rowWithoutInterest.put("INTEREST_RATE", 3.0);
        rowWithoutInterest.put("TERM_DAYS", 60);
        rowWithoutInterest.put("APPLICATION_DATE", new Date());
        rowWithoutInterest.put("MATURITY_DATE", new Date());
        rowWithoutInterest.put("INTEREST_EARNED", null);
        rowWithoutInterest.put("STATUS", "ACTIVADO");
        mockRows.add(rowWithoutInterest);

        // Simula retorno desde la base de datos
        when(jdbcUtils.queryForList("EJDP.QUERY_SELECT_ALL_CUSTOMER_DEPOSITS"))
                .thenReturn(mockRows);

        // Ejecuta y valida los resultados
        List<CustomerDepositDTO> result = ejdpR001.executeGetAllCustomerDeposits();
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(25.50, result.get(0).getDeposit().getInterestEarned(), 0.001);
        assertEquals(0.0, result.get(1).getDeposit().getInterestEarned(), 0.001);
    }

    @Test
    public void executeRegisterDeposit_ErrorCreatingDeposit() {
        // Simula flujo normal hasta el intento de insertar depósito
        when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
                .thenReturn(Collections.emptyList());

        Map<String, Object> customerIdMap = new HashMap<>();
        customerIdMap.put("NEXT_ID", 100L);
        Map<String, Object> depositIdMap = new HashMap<>();
        depositIdMap.put("NEXT_ID", 200L);

        when(jdbcUtils.queryForMap("EJDP.NEXT_CUSTOMER_ID")).thenReturn(customerIdMap);
        when(jdbcUtils.queryForMap("EJDP.NEXT_DEPOSIT_ID")).thenReturn(depositIdMap);

        // Inserción de cliente OK, pero falla en depósito
        when(jdbcUtils.update(eq("EJDP.INSERT_CUSTOMER_SQL"), anyMap())).thenReturn(1);
        when(jdbcUtils.update(eq("EJDP.QUERY_INSERT_DEPOSIT"), anyMap()))
                .thenThrow(new RuntimeException("Error de base de datos"));

        // Valida que se arroja excepción con el código correcto
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            ejdpR001.executeRegisterDeposit(depositRequestDTO);
        });

        assertEquals(BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(), ex.getAdviceCode());
    }

    @Test
    public void executeRegisterDeposit_ZeroRowsUpdatedForCustomer() {
        // Simula que no se insertó ninguna fila para el cliente
        when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
                .thenReturn(Collections.emptyList());

        Map<String, Object> customerIdMap = new HashMap<>();
        customerIdMap.put("NEXT_ID", 100L);

        when(jdbcUtils.queryForMap("EJDP.NEXT_CUSTOMER_ID")).thenReturn(customerIdMap);
        when(jdbcUtils.update(eq("EJDP.INSERT_CUSTOMER_SQL"), anyMap())).thenReturn(0);

        // Debe lanzar excepción por inserción fallida
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            ejdpR001.executeRegisterDeposit(depositRequestDTO);
        });

        assertEquals(BusinessExceptionEJDP.ERROR_CREATING_DEPOSIT.getCode(), ex.getAdviceCode());
    }

    @Test
    public void executeGetAllCustomerDeposits_Success() {
        // Simula una consulta exitosa con un solo depósito
        List<Map<String, Object>> mockRows = new ArrayList<>();
        Map<String, Object> row1 = new HashMap<>();
        row1.put("CUSTOMER_ID", 100L);
        row1.put("ACCOUNT_NUMBER", "12345678");
        row1.put("CUSTOMER_NAME", "Cliente 1");
        row1.put("DEPOSIT_ID", 200L);
        row1.put("AMOUNT", 1000.0);
        row1.put("INTEREST_RATE", 5.0);
        row1.put("TERM_DAYS", 30);
        row1.put("APPLICATION_DATE", new Date());
        row1.put("MATURITY_DATE", new Date());
        row1.put("INTEREST_EARNED", 12.33);
        row1.put("STATUS", "ACTIVADO");
        mockRows.add(row1);

        when(jdbcUtils.queryForList("EJDP.QUERY_SELECT_ALL_CUSTOMER_DEPOSITS"))
                .thenReturn(mockRows);

        // Verifica el resultado
        List<CustomerDepositDTO> result = ejdpR001.executeGetAllCustomerDeposits();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("12345678", result.get(0).getCustomer().getAccountNumber());
        assertEquals(200L, result.get(0).getDeposit().getId().longValue());
    }

    @Test
    public void executeGetAllCustomerDeposits_DBException() {
        // Simula error controlado en BD
        when(jdbcUtils.queryForList("EJDP.QUERY_SELECT_ALL_CUSTOMER_DEPOSITS"))
                .thenThrow(new DBException("Error de conexión"));

        // Debe lanzar excepción de negocio mapeada
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            ejdpR001.executeGetAllCustomerDeposits();
        });

        assertEquals(BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(), ex.getAdviceCode());
    }

    @Test
    public void executeGetAllCustomerDeposits_GenericException() {
        // Simula error inesperado en BD
        when(jdbcUtils.queryForList("EJDP.QUERY_SELECT_ALL_CUSTOMER_DEPOSITS"))
                .thenThrow(new RuntimeException("Error inesperado"));

        // Debe lanzar excepción de negocio mapeada
        BusinessException ex = assertThrows(BusinessException.class, () -> {
            ejdpR001.executeGetAllCustomerDeposits();
        });

        assertEquals(BusinessExceptionEJDP.ERROR_FETCHING_DEPOSITS.getCode(), ex.getAdviceCode());
    }


}
