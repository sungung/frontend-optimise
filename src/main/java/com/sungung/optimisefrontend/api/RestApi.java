package com.sungung.optimisefrontend.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sungung.optimisefrontend.entity.Customer;
import com.sungung.optimisefrontend.repository.CustomerRepository;

@RestController
@RequestMapping("/api")
public class RestApi {
	
	private final static CacheControl maxAge365Days = CacheControl.maxAge(365, TimeUnit.DAYS);
	
	@Autowired
	private CustomerRepository customerRepository;

	@GetMapping("/hello")
	public String greet(){
		return "Hello World !!";
	}
	
	/**
	 * Insert new customer with POST method, it will generate 400 error
	 * when customer already existed.
	 * 
	 * @param customer
	 * @return
	 * @throws URISyntaxException 
	 */
	@PostMapping("/customer")
	public ResponseEntity<?> addCustomer(@RequestBody Customer customer) throws URISyntaxException{
		if (customer.getId() != null) {
			throw new CustomerEntityUpdateException("New customer cannot have Id '" + customer.getId() + "'.");
		}
		customer = customerRepository.save(customer);
		return ResponseEntity.created(new URI("/api/customer/"+customer.getId())).body(customer);
	}
	
	@PutMapping("/customer")
	public ResponseEntity<?> updateCustomer(@RequestBody Customer customer) throws URISyntaxException{
		if (customer.getId() == null) {
			return addCustomer(customer);
		}
		customer = customerRepository.save(customer);
		return ResponseEntity.ok(customer);
	}
	
	/**
	 * Get customer entity by customer entity id
	 * Return empty payload with 404 error when customer is not found by id
	 * 
	 * @param id
	 * @return
	 */
	@GetMapping("/customer/{id}")
	public ResponseEntity<?> getCustomer(@PathVariable Long id){		
		Optional<Customer> maybe = Optional.ofNullable(customerRepository.findOne(id));
		return maybe.map(customer -> ResponseEntity
										.ok()
										.cacheControl(maxAge365Days)
										.eTag(String.valueOf(customer.getVersion()))
										.body(customer))
					.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get customer entity by customer email
	 * Return exception payload with 404 error when customer not found by given email
	 * @param email
	 * @return
	 */
	@GetMapping("/customer/email/{email}")
	public ResponseEntity<?> getCustomer(@PathVariable String email){		
		Customer customer = customerRepository.findOneByEmail(email).orElseThrow(
				() -> new CustomerNotFoundException(email));
		
		return ResponseEntity
				.ok()
				.cacheControl(CacheControl.maxAge(365, TimeUnit.DAYS))
				.eTag(String.valueOf(customer.getVersion()))
				.body(customer);
	}
	
	@GetMapping("/customers")
	public Collection<Customer> getAll(){
		return StreamSupport.stream(customerRepository.findAll().spliterator(), false)
		.collect(Collectors.toList());
	}
	
	
}
