package io.pivotal.service;

import io.pivotal.dao.CustomerDAO;
import io.pivotal.domain.Customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;


@Component
public class CustomerSearchService {
	
	protected final Logger log = LoggerFactory.getLogger(getClass().getName());
	
	private volatile boolean cacheMiss = false;
	
	public boolean isCacheMiss() {
		boolean cacheMiss = this.cacheMiss;
		this.cacheMiss = false;
		return cacheMiss;
	}

	protected void setCacheMiss() {
		this.cacheMiss = true;
	}
	
	@Autowired 
	CustomerDAO customerDAO;
	
	@Cacheable(value = "customer")
	public Customer getCustomerByEmailId(String emailId) {
		
		setCacheMiss();
		
		log.info("Cache Miss for Email :" + emailId);
		log.info("Retrieving Data From Backend For :" + emailId);

		Customer cust = customerDAO.getCustomerDetails(emailId);
		System.out.println("Returning customer: " + cust);

		return cust;

	}

}
