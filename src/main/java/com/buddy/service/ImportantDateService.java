package com.buddy.service;

import com.buddy.domain.ImportantDate;

public interface ImportantDateService
{
	ImportantDate findById(Long id);

	void removeById(Long id);
}
