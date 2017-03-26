package io.pivotal.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import io.pivotal.domain.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class CustomerDAO {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	Fairy fairy = Fairy.create();

	public Customer getCustomerDetails(String email) {

		Customer customer = null;
		try {
			String SQL = "SELECT * FROM customer WHERE email = ?";
			customer = jdbcTemplate.queryForObject(SQL, new Object[]{email}, new CustomerMapper());
		} catch (EmptyResultDataAccessException dataAccessException) {
			customer = new Customer();
		}
        return customer;
	}
	
	public List<Customer> getAll() {

		List<Customer> customers = null;
		
		String SQL = "SELECT * FROM customer";
		customers = jdbcTemplate.query(SQL, new CustomerMapper());
		
        return customers;
	}
	
	public void removeAll() {
		
		jdbcTemplate.execute("TRUNCATE TABLE customer");
		
	}
	
	public void save(int num) {
		
		String SQL = "INSERT INTO customer (id, name, email, address, birthday) VALUES (?, ?, ?, ?, ?)";

		jdbcTemplate.batchUpdate(SQL, new BatchPreparedStatementSetter() {
	                @Override
	                public void setValues(PreparedStatement ps, int i)
	                        throws SQLException {
	                	Person person = fairy.person();
	            		Customer customer = new Customer(person.passportNumber(), person.fullName(), person.email(), person.getAddress().toString(), person.dateOfBirth().toString());
	            		
	                    ps.setString(1, customer.getId());
	                    ps.setString(2, customer.getName());
	                    ps.setString(3, customer.getEmail());
	                    ps.setString(4, customer.getAddress());
	                    ps.setString(5, customer.getBirthday());
	                }

	                @Override
	                public int getBatchSize() {
	                    return num;
	                }
	            });
	}

}

class CustomerMapper implements RowMapper<Customer> {

	public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
		  Customer customer = new Customer();
		  customer.setId(rs.getString("id"));
		  customer.setName(rs.getString("name"));
		  customer.setEmail(rs.getString("email"));
		  customer.setAddress(rs.getString("address"));
		  customer.setBirthday(rs.getString("birthday"));
	      return customer;
	  }

}
