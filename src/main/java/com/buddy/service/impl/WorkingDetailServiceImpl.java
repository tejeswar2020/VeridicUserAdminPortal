package com.buddy.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.buddy.domain.WorkingDetail;
import com.buddy.repository.WorkingDetailRepository;
import com.buddy.service.WorkingDetailService;

@Service
public class WorkingDetailServiceImpl implements WorkingDetailService
{

	@Autowired
	private WorkingDetailRepository workingDetailRepository;

	public Optional<WorkingDetail> findById(Long id)
	{
		return workingDetailRepository.findById(id);
	}

	public void removeById(Long id)
	{
		workingDetailRepository.deleteById(id);
	}
}
