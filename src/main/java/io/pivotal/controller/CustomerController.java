package io.pivotal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.dao.CustomerDAO;
import io.pivotal.domain.Customer;
import io.pivotal.repo.CustomerPCCRepository;
import io.pivotal.service.CustomerSearchService;

@RestController
public class CustomerController {
	
	@Autowired
	private CustomerPCCRepository repository;
	
	@Autowired
	CustomerSearchService customerSearchService;
	
	@Autowired
	CustomerDAO customerDao;
	
	@RequestMapping("/")
	public String home() {
		return "Customer Search Service -- Available APIs: <br/>"
				+ "<br/>"
				+ "GET /api/showcache    	               - get all customer info in PCC<br/>"
				+ "GET /api/clearcache                     - remove all customer info in PCC<br/>"
				+ "GET /api/showdb  	                   - get all customer info in MySQL<br/>"
				+ "GET /api/cleardb                        - remove all customer info in MySQL<br/>"
				+ "GET /api/loaddb                         - load 500 customer info into MySQL<br/>"
				+ "GET /api/customerSearch?email={email}   - get specific customer info<br/>";
	}

	@RequestMapping(method = RequestMethod.GET, path = "/showcache")
	@ResponseBody
	public String show() throws Exception {
		StringBuilder result = new StringBuilder();
		
		repository.findAll().forEach(item->result.append(item+"<br/>"));

		return result.toString();
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/clearcache")
	@ResponseBody
	public String clearCache() throws Exception {
		repository.deleteAll();
		return "Region cleared";
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/showdb")
	@ResponseBody
	public String showDB() throws Exception {
		StringBuilder result = new StringBuilder();
		
		customerDao.getAll().forEach(item->result.append(item+"<br/>"));
		
		return result.toString();
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/loaddb")
	@ResponseBody
	public String loadDB() throws Exception {
		
		customerDao.save(500);
		
		return "New 500 customers successfully saved into Database";
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/cleardb")
	@ResponseBody
	public String clearDB() throws Exception {
		
		customerDao.removeAll();
		
		return "Database cleared";
	}
	
	@RequestMapping(value = "/customerSearch", method = RequestMethod.GET)
	public String searchCustomerByEmail(@RequestParam(value = "email", required = true) String email) {
		
		long startTime = System.currentTimeMillis();
		Customer customer = customerSearchService.getCustomerByEmailId(email);
		long elapsedTime = System.currentTimeMillis();

		return String.format("\"%1$s\"<br/>Cache Miss [%2$s]<br/>Elapsed Time [%3$s ms]%n", customer, customerSearchService.isCacheMiss(), (elapsedTime - startTime));
	}
}
