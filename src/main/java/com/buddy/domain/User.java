package com.buddy.domain;

import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import com.buddy.domain.security.Authority;
import com.buddy.domain.security.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * copyrights © 2018 - Veridic solutions
 * 
 * @author Tejeswar Velpucharla
 */

@Entity
public class User implements UserDetails
{

	private static final long serialVersionUID = 8650459563156151847L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;

	@Column(name = "email", nullable = false, updatable = false)
	@Email
	private String email;
	private String phone;
	
	private String gender;
	private String maritialStatus;
	private String ethnicity;
	private String livingAddress;
	private String fromBranch;
	private String technology;
	private String reffer;
	private String employeeStatus;
	private String reportingManager;
	private String payrollId;
	private String profilePictureExtension;
	
	private Date dateOfBirth;
	private Date dateOfJoining;
	private Date dateOfLeft;
	private Date payrollStartDate;
	private Date payrollEndDate;
	
	@Transient
	private MultipartFile profilePicture;
	
	private boolean enabled = true;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<ImportantDate> importantDateList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
	private List<WorkingDetail> workingDetailList;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<UserRole> userRoles = new HashSet<>();

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public Set<UserRole> getUserRoles()
	{
		return userRoles;
	}

	public void setUserRoles(Set<UserRole> userRoles)
	{
		this.userRoles = userRoles;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		Set<GrantedAuthority> authorites = new HashSet<>();
		userRoles.forEach(ur -> authorites.add(new Authority(ur.getRole().getName())));

		return authorites;
	}

	@Override
	public boolean isAccountNonExpired()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return enabled;
	}

	public List<WorkingDetail> getWorkingDetailList()
	{
		return workingDetailList;
	}

	public void setWorkingDetailList(List<WorkingDetail> workingDetailList)
	{
		this.workingDetailList = workingDetailList;
	}

	public List<ImportantDate> getImportantDateList()
	{
		return importantDateList;
	}

	public void setImportantDateList(List<ImportantDate> importantDateList)
	{
		this.importantDateList = importantDateList;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getMaritialStatus()
	{
		return maritialStatus;
	}

	public void setMaritialStatus(String maritialStatus)
	{
		this.maritialStatus = maritialStatus;
	}

	public String getEthnicity()
	{
		return ethnicity;
	}

	public void setEthnicity(String ethnicity)
	{
		this.ethnicity = ethnicity;
	}

	public String getLivingAddress()
	{
		return livingAddress;
	}

	public void setLivingAddress(String livingAddress)
	{
		this.livingAddress = livingAddress;
	}

	public String getFromBranch()
	{
		return fromBranch;
	}

	public void setFromBranch(String fromBranch)
	{
		this.fromBranch = fromBranch;
	}

	public String getTechnology()
	{
		return technology;
	}

	public void setTechnology(String technology)
	{
		this.technology = technology;
	}

	public String getReffer()
	{
		return reffer;
	}

	public void setReffer(String reffer)
	{
		this.reffer = reffer;
	}

	public String getReportingManager()
	{
		return reportingManager;
	}

	public void setReportingManager(String reportingManager)
	{
		this.reportingManager = reportingManager;
	}

	public String getPayrollId()
	{
		return payrollId;
	}

	public void setPayrollId(String payrollId)
	{
		this.payrollId = payrollId;
	}

	public Date getDateOfBirth()
	{
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth)
	{
		this.dateOfBirth = dateOfBirth;
	}

	public Date getDateOfJoining()
	{
		return dateOfJoining;
	}

	public void setDateOfJoining(Date dateOfJoining)
	{
		this.dateOfJoining = dateOfJoining;
	}

	public Date getPayrollStartDate()
	{
		return payrollStartDate;
	}

	public void setPayrollStartDate(Date payrollStartDate)
	{
		this.payrollStartDate = payrollStartDate;
	}

	public MultipartFile getProfilePicture()
	{
		return profilePicture;
	}

	public void setProfilePicture(MultipartFile profilePicture)
	{
		this.profilePicture = profilePicture;
	}

	public Date getDateOfLeft()
	{
		return dateOfLeft;
	}

	public void setDateOfLeft(Date dateOfLeft)
	{
		this.dateOfLeft = dateOfLeft;
	}

	public Date getPayrollEndDate()
	{
		return payrollEndDate;
	}

	public void setPayrollEndDate(Date payrollEndDate)
	{
		this.payrollEndDate = payrollEndDate;
	}

	public String getProfilePictureExtension()
	{
		return profilePictureExtension;
	}

	public void setProfilePictureExtension(String profilePictureExtension)
	{
		this.profilePictureExtension = profilePictureExtension;
	}

	public String getEmployeeStatus()
	{
		return employeeStatus;
	}

	public void setEmployeeStatus(String employeeStatus)
	{
		this.employeeStatus = employeeStatus;
	}
	

}
