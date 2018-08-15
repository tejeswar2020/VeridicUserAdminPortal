package com.buddy.service;

import java.util.Optional;

import com.buddy.domain.WorkingDetail;

public interface WorkingDetailService
{
	Optional<WorkingDetail> findById(Long id);

	void removeById(Long id);
}
