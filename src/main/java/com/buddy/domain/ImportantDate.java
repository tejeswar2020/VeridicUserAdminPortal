package com.buddy.domain;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.web.multipart.MultipartFile;

/**
 * copyrights © 2018 - Veridic solutions
 * 
 * @author Tejeswar Velpucharla
 */

@Entity
public class ImportantDate
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String cardType;
	private String cardNumber;
	private Date startDate;
	private Date endDate;
	private String cardExtention;
	private boolean activeDate;
	
	@Transient
	private MultipartFile cardDocument;
	
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

	public String getCardType()
	{
		return cardType;
	}

	public void setCardType(String cardType)
	{
		this.cardType = cardType;
	}

	public String getCardNumber()
	{
		return cardNumber;
	}

	public void setCardNumber(String cardNumber)
	{
		this.cardNumber = cardNumber;
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

	public String getCardExtention()
	{
		return cardExtention;
	}

	public void setCardExtention(String cardExtention)
	{
		this.cardExtention = cardExtention;
	}

	public boolean isActiveDate()
	{
		return activeDate;
	}

	public void setActiveDate(boolean activeDate)
	{
		this.activeDate = activeDate;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	public MultipartFile getCardDocument()
	{
		return cardDocument;
	}

	public void setCardDocument(MultipartFile cardDocument)
	{
		this.cardDocument = cardDocument;
	}
	
	
}
