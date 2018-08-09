package com.buddy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buddy.domain.ImportantDate;
import com.buddy.domain.WorkingDetail;
import com.buddy.repository.ImportantDateRepository;
import com.buddy.service.ImportantDateService;
import com.buddy.service.WorkingDetailService;

@Service
public class ImportantDateServiceImpl implements ImportantDateService
{

	@Autowired
	private ImportantDateRepository importantDateRepository;

	public ImportantDate findById(Long id)
	{
		return importantDateRepository.findOne(id);
	}

	public void removeById(Long id)
	{
		importantDateRepository.delete(id);
	}
}
