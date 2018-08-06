package com.buddy.domain;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class WorkingDetail
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String clientName;
	private String clientAddress;
	private String vendorName;
	private String vendorPhone;
	private String vendorMail;
	private String designation;
	private String workPhone;
	private String workMail;
	private Date startDate;
	private Date endDate;
	
	@Transient
	private char isWorkingDetailActive;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getClientName()
	{
		return clientName;
	}

	public void setClientName(String clientName)
	{
		this.clientName = clientName;
	}

	public String getClientAddress()
	{
		return clientAddress;
	}

	public void setClientAddress(String clientAddress)
	{
		this.clientAddress = clientAddress;
	}

	public String getVendorName()
	{
		return vendorName;
	}

	public void setVendorName(String vendorName)
	{
		this.vendorName = vendorName;
	}

	public String getVendorPhone()
	{
		return vendorPhone;
	}

	public void setVendorPhone(String vendorPhone)
	{
		this.vendorPhone = vendorPhone;
	}

	public String getVendorMail()
	{
		return vendorMail;
	}

	public void setVendorMail(String vendorMail)
	{
		this.vendorMail = vendorMail;
	}

	public String getDesignation()
	{
		return designation;
	}

	public void setDesignation(String designation)
	{
		this.designation = designation;
	}

	public String getWorkPhone()
	{
		return workPhone;
	}

	public void setWorkPhone(String workPhone)
	{
		this.workPhone = workPhone;
	}

	public String getWorkMail()
	{
		return workMail;
	}

	public void setWorkMail(String workMail)
	{
		this.workMail = workMail;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public char getIsWorkingDetailActive()
	{
		char ch = 'A';
		return ch;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
	

}
