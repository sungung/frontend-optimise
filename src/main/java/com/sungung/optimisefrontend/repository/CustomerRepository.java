package com.sungung.optimisefrontend.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.sungung.optimisefrontend.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	Collection<Customer> findByLastName(String lastName);
	Optional<Customer> findOneByEmail(String email);
}
