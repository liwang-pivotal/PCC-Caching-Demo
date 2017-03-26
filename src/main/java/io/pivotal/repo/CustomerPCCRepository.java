package io.pivotal.repo;

import io.pivotal.domain.Customer;

import org.springframework.data.gemfire.repository.GemfireRepository;

public interface CustomerPCCRepository extends GemfireRepository<Customer, String> {
}
