//package com.bbva.ejdp;
//
//import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
//import com.bbva.elara.domain.transaction.Context;
//import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
//import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.MockitoAnnotations;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//public class EJDPT00101PETransactionTest {
//
//	private Map<String, Object> parameters;
//
//	private Map<Class<?>, Object> serviceLibraries;
//
//	@Mock
//	private ApplicationConfigurationService applicationConfigurationService;
//
//	@Mock
//	private CommonRequestHeader commonRequestHeader;
//
//	private final EJDPT00101PETransaction transaction = new EJDPT00101PETransaction() {
//		@Override
//		protected void addParameter(String field, Object obj) {
//			if (parameters != null) {
//				parameters.put(field, obj);
//			}
//		}
//
//		@Override
//		protected Object getParameter(String field) {
//			return parameters.get(field);
//		}
//
//		@Override
//		protected <T> T getServiceLibrary(Class<T> serviceInterface) {
//			return (T) serviceLibraries.get(serviceInterface);
//		}
//
//		@Override
//		public String getProperty(String keyProperty) {
//			return applicationConfigurationService.getProperty(keyProperty);
//		}
//
//		@Override
//		protected CommonRequestHeader getRequestHeader() {
//			return commonRequestHeader;
//		}
//	};
//
//	@Before
//	public void setUp() {
//		MockitoAnnotations.initMocks(this);
//		initializeTransaction();
//		// TODO - Set the mocks created from the libraries to the transaction, e.g.:
//		// setServiceLibrary(QWAIR001.class, qwaiR001);
//	}
//
//	private void initializeTransaction() {
//		transaction.setContext(new Context());
//		parameters = new HashMap<>();
//		serviceLibraries = new HashMap<>();
//	}
//
//	private void setServiceLibrary(Class<?> clasz, Object mockObject) {
//		serviceLibraries.put(clasz, mockObject);
//	}
//
//	private void setParameterToTransaction(String parameter, Object value) {
//		parameters.put(parameter, value);
//	}
//
//	private Object getParameterFromTransaction(String parameter) {
//		return parameters.get(parameter);
//	}
//
//	@Test
//	public void executeTest() {
//		// when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.COUNTRYCODE)).thenReturn("ES");
//		// when(applicationConfigurationService.getProperty("config.property")).thenReturn("value");
//		// when(qwaiR001.execute()).thenReturn(listCustomerDTO);
//		// setParameterToTransaction("customerIn", new CustomerDTO());
//		transaction.execute();
//		Assert.assertEquals(0, transaction.getAdviceList().size());
//	}
//}
