//package com.bbva.ejdp.lib.r001.impl;
//
//import com.bbva.apx.exception.business.BusinessException;
//import com.bbva.ejdp.dto.deposit.DepositRequestDTO;
//import com.bbva.ejdp.dto.deposit.DepositResponseDTO;
//import com.bbva.elara.utility.jdbc.JdbcUtils;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import static org.mockito.Mockito.*;
//
//public class EJDPR001ImplTest {
//
//	@Mock
//	private JdbcUtils jdbcUtils;
//
//	@InjectMocks
//	private EJDPR001Impl ejdpR001;
//
//	@Before
//	public void setUp() {
//		MockitoAnnotations.openMocks(this);
//	}
//
//	@Test
//	public void executeRegisterDeposit_Success() {
//		// Arrange
//		DepositRequestDTO request = new DepositRequestDTO();
//		request.setAccountNumber("12345678");
//		request.setCustomerName("Juan Perez");
//		request.setAmount(1000.0);
//		request.setInterestRate(5.0);
//		request.setTermDays(30);
//
//		when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
//				.thenReturn(List.of());
//
//		Map<String, Object> customerIdMap = Map.of("NEXT_ID", 1);
//		Map<String, Object> depositIdMap = Map.of("NEXT_ID", 10);
//
//		when(jdbcUtils.queryForMap("EJDP.NEXT_CUSTOMER_ID")).thenReturn(customerIdMap);
//		when(jdbcUtils.queryForMap("EJDP.NEXT_DEPOSIT_ID")).thenReturn(depositIdMap);
//
//		doNothing().when(jdbcUtils).update(eq("EJDP.INSERT_CUSTOMER_SQL"), anyMap());
//		doNothing().when(jdbcUtils).update(eq("EJDP.QUERY_INSERT_DEPOSIT"), anyMap());
//
//		// Act
//		DepositResponseDTO response = ejdpR001.executeRegisterDeposit(request);
//
//		// Assert
//		Assert.assertNotNull(response);
//		Assert.assertEquals(1000.0, response.getAmount(), 0);
//		Assert.assertEquals(5.0, response.getInterestRate(), 0);
//		Assert.assertEquals(30, response.getTermDays());
//		Assert.assertEquals("ACTIVADO", response.getStatus());
//	}
//
//	@Test(expected = BusinessException.class)
//	public void executeRegisterDeposit_DuplicateAccountNumber_ThrowsException() {
//		// Arrange
//		DepositRequestDTO request = new DepositRequestDTO();
//		request.setAccountNumber("12345678");
//		request.setCustomerName("Juan Perez");
//		request.setAmount(1000.0);
//		request.setInterestRate(5.0);
//		request.setTermDays(30);
//
//		when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
//				.thenReturn(List.of(Map.of("ACCOUNT_NUMBER", "12345678")));
//
//		// Act
//		ejdpR001.executeRegisterDeposit(request);
//	}
//
//	@Test(expected = BusinessException.class)
//	public void executeRegisterDeposit_InvalidValues_ThrowsException() {
//		// Arrange
//		DepositRequestDTO request = new DepositRequestDTO();
//		request.setAccountNumber("12345678");
//		request.setCustomerName("Juan Perez");
//		request.setAmount(-1000.0); // Invalid amount
//		request.setInterestRate(150.0); // Invalid interest rate
//		request.setTermDays(-10); // Invalid term days
//
//		when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
//				.thenReturn(List.of());
//
//		// Act
//		ejdpR001.executeRegisterDeposit(request);
//	}
//
//	@Test(expected = BusinessException.class)
//	public void executeRegisterDeposit_InsertDepositFails_ThrowsException() {
//		// Arrange
//		DepositRequestDTO request = new DepositRequestDTO();
//		request.setAccountNumber("12345678");
//		request.setCustomerName("Juan Perez");
//		request.setAmount(1000.0);
//		request.setInterestRate(5.0);
//		request.setTermDays(30);
//
//		when(jdbcUtils.queryForList(eq("EJDP.CHECK_ACCOUNT_NUMBER"), anyMap()))
//				.thenReturn(List.of());
//
//		Map<String, Object> customerIdMap = Map.of("NEXT_ID", 1);
//		Map<String, Object> depositIdMap = Map.of("NEXT_ID", 10);
//
//		when(jdbcUtils.queryForMap("EJDP.NEXT_CUSTOMER_ID")).thenReturn(customerIdMap);
//		when(jdbcUtils.queryForMap("EJDP.NEXT_DEPOSIT_ID")).thenReturn(depositIdMap);
//
//		doNothing().when(jdbcUtils).update(eq("EJDP.INSERT_CUSTOMER_SQL"), anyMap());
//		doThrow(new RuntimeException("DB error")).when(jdbcUtils).update(eq("EJDP.QUERY_INSERT_DEPOSIT"), anyMap());
//
//		// Act
//		ejdpR001.executeRegisterDeposit(request);
//	}
//}
