package com.buddy.service;

import com.buddy.domain.WorkingDetail;

public interface WorkingDetailService
{
	WorkingDetail findById(Long id);

	void removeById(Long id);
}
