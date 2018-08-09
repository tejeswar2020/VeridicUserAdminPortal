package com.buddy.repository;

import org.springframework.data.repository.CrudRepository;

import com.buddy.domain.ImportantDate;

public interface ImportantDateRepository extends CrudRepository<ImportantDate, Long>
{

}
