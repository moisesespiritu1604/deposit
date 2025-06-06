package com.bbva.ejdp.lib.r002.impl;

import com.bbva.ejdp.lib.r002.EJDPR002;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class EJDPR002Abstract extends AbstractLibrary implements EJDPR002 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected APIConnector internalApiConnector;


	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param internalApiConnector the this.internalApiConnector to set
	*/
	public void setInternalApiConnector(APIConnector internalApiConnector) {
		this.internalApiConnector = internalApiConnector;
	}

}