package com.rs.multirediscacheservice.model;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface PolicyRepo extends PagingAndSortingRepository<Policy, PolicyKey> {
}
