package com.buddy.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.buddy.constants.BranchConstants;
import com.buddy.constants.CardTypeConstants;
import com.buddy.domain.ImportantDate;
import com.buddy.domain.User;
import com.buddy.domain.WorkingDetail;
import com.buddy.domain.security.PasswordResetToken;
import com.buddy.domain.security.Role;
import com.buddy.domain.security.UserRole;
import com.buddy.service.ImportantDateService;
import com.buddy.service.UserService;
import com.buddy.service.WorkingDetailService;
import com.buddy.service.impl.UserSecurityService;
import com.buddy.utility.MailConstructor;
import com.buddy.utility.SecurityUtility;

@Controller
public class HomeController
{
	
	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private MailConstructor mailConstructor;

	@Autowired
	private UserService userService;

	@Autowired
	private UserSecurityService userSecurityService;
	
	@Autowired
	private WorkingDetailService workingDetailService;
	
	@Autowired
	private ImportantDateService importantDateService;
	
	private static String PATH = "src/main/resources/static/image/";
	
	private static String EMPTY = " should not be empty.";
	
	private static StringBuilder feedBack =  new StringBuilder();

	@RequestMapping("/")
	public String index()
	{
		return "index";
	}

	@RequestMapping("/login")
	public String login(Model model)
	{
		model.addAttribute("classActiveLogin", true);
		return "myAccount";
	}
	
	@RequestMapping("/myProfilePage")
	public String myProfilePage(Model model, Principal principal)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("workingDetailList", user.getWorkingDetailList());
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("currentDesignation", user.getWorkingDetailList().get(0).getDesignation());
		return "myProfilePage";
	}

	@RequestMapping("/myProfile")
	public String myProfile(Model model, Principal principal)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("workingDetailList", user.getWorkingDetailList());
		model.addAttribute("importantDateList", user.getImportantDateList());

		model.addAttribute("listOfWorkingDetails", true);
		model.addAttribute("listOfImportantDates", true);

		model.addAttribute("classActiveEdit", true);
		listOfBranchDetails(model);

		return "myProfile";
	}

	private void listOfBranchDetails(Model model)
	{
		List<String> branchList = BranchConstants.listOfUSBranchName;
//		Collections.sort(branchList);
		model.addAttribute("branchList", branchList);
		
		List<String> cardTypeList = CardTypeConstants.listOfCardTypeName;
//		Collections.sort(cardTypeList);
		model.addAttribute("cardTypeList", cardTypeList);
	}

	@RequestMapping("/forgetPassword")
	public String forgetPassword(HttpServletRequest request, @ModelAttribute("email") String email, Model model)
	{

		model.addAttribute("classActiveForgetPassword", true);

		User user = userService.findByEmail(email);

		if (user == null)
		{
			model.addAttribute("emailNotExist", true);
			return "myAccount";
		}

		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		userService.save(user);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		String subject = "Veridic Solutions Account - Reset Password Request !!!";

		SimpleMailMessage newEmail = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user,
				password, subject);

		mailSender.send(newEmail);

		model.addAttribute("forgetPasswordEmailSent", "true");

		return "myAccount";
	}

	@RequestMapping(value = "/newUser", method = RequestMethod.POST)
	public String newUserPost(HttpServletRequest request, @ModelAttribute("email") String userEmail,
			@ModelAttribute("username") String username, Model model) throws Exception
	{
		model.addAttribute("classActiveNewAccount", true);
		model.addAttribute("email", userEmail);
		model.addAttribute("username", username);

		if (userService.findByUsername(username) != null)
		{
			model.addAttribute("usernameExists", true);

			return "myAccount";
		}

		if (userService.findByEmail(userEmail) != null)
		{
			model.addAttribute("emailExists", true);

			return "myAccount";
		}

		User user = new User();
		user.setUsername(username);
		user.setEmail(userEmail);

		String password = SecurityUtility.randomPassword();

		String encryptedPassword = SecurityUtility.passwordEncoder().encode(password);
		user.setPassword(encryptedPassword);

		Role role = new Role();
		role.setRoleId(1);
		role.setName("ROLE_USER");
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, role));
		userService.createUser(user, userRoles);

		String token = UUID.randomUUID().toString();
		userService.createPasswordResetTokenForUser(user, token);

		String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

		String subject = "Veridic solutions Account created !!!";

		SimpleMailMessage email = mailConstructor.constructResetTokenEmail(appUrl, request.getLocale(), token, user,
				password, subject);

		mailSender.send(email);

		model.addAttribute("emailSent", "true");

		return "myAccount";
	}

	@RequestMapping("/newUser")
	public String newUser(Locale locale, @RequestParam("token") String token, Model model)
	{
		PasswordResetToken passToken = userService.getPasswordResetToken(token);

		if (passToken == null)
		{
			String message = "Your temporary Password got expired.";
			model.addAttribute("message", message);
			return "badRequestPage";
		}
		
		Date passTokenExpriryDate = passToken.getExpiryDate();
		Date currentDate = new Date();
		if (passTokenExpriryDate.before(currentDate))
		{
			String message = "Your temporary Password got expired.";
			model.addAttribute("message", message);
			return "badRequestPage";
		}

		User user = passToken.getUser();
		String username = user.getUsername();

		UserDetails userDetails = userSecurityService.loadUserByUsername(username);

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);

		model.addAttribute("user", user);

		model.addAttribute("classActiveEdit", true);
		return "myProfile";
	}

	@RequestMapping("/listOfWorkingDetails")
	public String listOfWorkingDetails(Model model, Principal principal, HttpServletRequest request)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		model.addAttribute("listOfImportantDates", true);
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("workingDetailList", user.getWorkingDetailList());

		model.addAttribute("listOfWorkingDetails", true);
		model.addAttribute("classActiveWorkingDetails", true);
		listOfBranchDetails(model);

		return "myProfile";
	}

	@RequestMapping("/addWorkingDetail")
	public String addWorkingDetail(Model model, Principal principal)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		
		WorkingDetail workingDetail = new WorkingDetail();
		if (null == workingDetail.getEndDate())
		{
			java.sql.Date endDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
			workingDetail.setEndDate(endDate);
		}
		if (null == workingDetail.getStartDate())
		{
			java.sql.Date startDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
			workingDetail.setStartDate(startDate);
		}
		model.addAttribute("workingDetail", workingDetail);

		model.addAttribute("addWorkingDetail", true);
		model.addAttribute("classActiveWorkingDetails", true);
		model.addAttribute("listOfImportantDates", true);
		model.addAttribute("importantDateList", user.getImportantDateList());
		listOfBranchDetails(model);

		return "myProfile";
	}

	@RequestMapping(value = "/addWorkingDetail", method = RequestMethod.POST)
	public String addWorkingDetailPost(@ModelAttribute("workingDetail") WorkingDetail workingDetail,
			Principal principal, Model model)
	{
		User user = userService.findByUsername(principal.getName());
		
		if (isWorkingDetailFieldInvalid(workingDetail, feedBack))
		{
			model.addAttribute("emptyRecord", true);
			
			model.addAttribute("user", user);
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());

			model.addAttribute("classActiveWorkingDetails", true);
			model.addAttribute("addWorkingDetail", "true");

			model.addAttribute("invalidWorkingDateField", true);
			model.addAttribute("feedBack", feedBack);
			listOfBranchDetails(model);
			
			return "myProfile";
		}
		else
		{
			userService.updateWorkingDetail(workingDetail, user);

			model.addAttribute("user", user);
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());

			model.addAttribute("classActiveWorkingDetails", true);
			model.addAttribute("listOfWorkingDetails", true);
			listOfBranchDetails(model);

			return "myProfile";
		}
	}

	private boolean isWorkingDetailFieldInvalid(WorkingDetail workingDetail, StringBuilder feedback)
	{
		if (workingDetail.getClientAddress().trim().equals("") ||
			workingDetail.getClientName().trim().equals("") )
		{
			feedBack.setLength(0);
			feedBack.append("Client Name/Address" + EMPTY);
			return true;
		}
		else if (workingDetail.getVendorName().trim().equals("") ||
				 workingDetail.getVendorMail().trim().equals("") ||
				 workingDetail.getVendorPhone().trim().equals(""))
		{
			feedBack.setLength(0);
			feedBack.append("Vendor Name/Mail/Phone" + EMPTY);
			return true;
		}
		else if (workingDetail.getDesignation().trim().equals("") ||
				 workingDetail.getWorkMail().trim().equals("") ||
				 workingDetail.getWorkPhone().trim().equals(""))
		{
			feedBack.setLength(0);
			feedBack.append("Designation/Mail/Phone" + EMPTY);
			return true;
		}
		
		Date startDate = workingDetail.getStartDate();
		Date endDate = workingDetail.getEndDate();
		
		if (endDate.before(startDate))
		{
			feedBack.setLength(0);
			feedBack.append("End Date should be greater than Start Date.");
			return true;
		}
		
		return false;
	}

	@RequestMapping("/updateWorkingDetail")
	public String updateWorkingDetail(@ModelAttribute("id") Long id, Principal principal, Model model)
	{
		User user = userService.findByUsername(principal.getName());
		Optional<WorkingDetail> workingDetail = workingDetailService.findById(id);
		
		if(user.getId() != workingDetail.get().getUser().getId()) 
		{
			return "badRequestPage";
		}
		else
		{
			model.addAttribute("user", user);
			model.addAttribute("workingDetail", workingDetail);
			model.addAttribute("classActiveWorkingDetails", true);
			model.addAttribute("addWorkingDetail", true);
			
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());
			listOfBranchDetails(model);
			
			return "myProfile";
		}
	}
	
	@RequestMapping("/removeWorkingDetail")
	public String removeWorkingDetail(@ModelAttribute("id") Long id, Principal principal, Model model)
	{
		User user = userService.findByUsername(principal.getName());
		Optional<WorkingDetail> workingDetail = workingDetailService.findById(id);
		
		if(user.getId() != workingDetail.get().getUser().getId())
		{
			String message = "Something went wrong. Contact Admin!!!";
			model.addAttribute("message", message);
			return "badRequestPage";
		} 
		else 
		{
			model.addAttribute("user", user);
			workingDetailService.removeById(id);
			
			model.addAttribute("classActiveWorkingDetails", true);
			model.addAttribute("listOfWorkingDetails", true);
			
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());
			listOfBranchDetails(model);
			
			return "myProfile";
		}
	}
	
	@RequestMapping("/addImportantDate")
	public String addImportantDate(Model model, Principal principal)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);

		ImportantDate importantDate = new ImportantDate();
		if (null == importantDate.getEndDate())
		{
			java.sql.Date endDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
			importantDate.setEndDate(endDate);
		}
		if (null == importantDate.getStartDate())
		{
			java.sql.Date startDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
			importantDate.setStartDate(startDate);
		}
		model.addAttribute("importantDate", importantDate);
		
		model.addAttribute("classActiveImportantDates", true);
		model.addAttribute("addImportantDate", true);
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("listOfWorkingDetails", true);
		model.addAttribute("workingDetailList", user.getWorkingDetailList());
		listOfBranchDetails(model);

		return "myProfile";
	}
	
	@RequestMapping(value = "/addImportantDate", method = RequestMethod.POST)
	public String addImportantDatesPost(@ModelAttribute("importantDate") ImportantDate importantDate,
			Principal principal, Model model) throws Exception
	{
		User user = userService.findByUsername(principal.getName());
		
		MultipartFile cardDocument = importantDate.getCardDocument();
		String cardDocumentExtention = getExtensionOfFile(cardDocument);
		System.out.println("Original FileName " + cardDocument.getOriginalFilename());
		System.out.println("Original contentType " + cardDocument.getContentType());
		
		// Check for Blank Fields || Check for Blank document.
		if(isImportantDateFieldsInvalid(importantDate, cardDocument, feedBack))
		{
			model.addAttribute("classActiveImportantDates", true);
			model.addAttribute("user", user);
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());
			model.addAttribute("addImportantDate", true);
			
			model.addAttribute("invalidImportantDateField", true);
			model.addAttribute("feedBack", feedBack);
			listOfBranchDetails(model);

			return "myProfile";
		}
		
		importantDate.setCardExtention(cardDocumentExtention);
		// Save the Important Table.
		userService.updateImportantDates(user, importantDate);
		
		try
		{
			byte[] bytes = cardDocument.getBytes();
			String name = importantDate.getCardType() + cardDocumentExtention;
			String filePath = createOrRetrieve( PATH + user.getId());
			
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(filePath + "/" + name)));
			stream.write(bytes);
			stream.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		model.addAttribute("classActiveImportantDates", true);
		model.addAttribute("user", user);
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("workingDetailList", user.getWorkingDetailList());
		model.addAttribute("listOfImportantDates", true);
		listOfBranchDetails(model);

		return "redirect:listOfImportantDates";
	}
	
	public static String getExtensionOfFile(MultipartFile file)
	{
		String fileExtension="";
		// Get file Name first
		String fileName=file.getOriginalFilename();
		
		// If fileName do not contain "." or starts with "." then it is not a valid file
		if(fileName.contains(".") && fileName.lastIndexOf(".")!= 0)
		{
			fileExtension = "." + fileName.substring(fileName.lastIndexOf(".")+1);
		}
		
		return fileExtension;
	}
	
	private boolean isImportantDateFieldsInvalid(ImportantDate importantDate, MultipartFile cardDocument, StringBuilder feedBack)
	{
		if (importantDate.getCardType().trim().equals("") ||
			importantDate.getCardNumber().trim().equals("") )
		{
			feedBack.setLength(0);
			feedBack.append("Card Type/Number should not be empty Field.");
			return true;
		}
		
		Date startDate = importantDate.getStartDate();
		Date endDate = importantDate.getEndDate();
		
		if (endDate.before(startDate))
		{
			feedBack.setLength(0);
			feedBack.append("End Date should be greater than Start Date.");
			return true;
		}
		
		if (cardDocument.isEmpty())
		{
			feedBack.setLength(0);
			feedBack.append("Document should not be empty !");
			return true;
		}
		return false;
	}
	
	private String createOrRetrieve(final String target) throws IOException
	{
	    final Path path = Paths.get(target);

	    if(Files.notExists(path))
	    {
//	        LOG.info("Target file \"" + target + "\" will be created.");
	        return Files.createDirectories(path).toString();
	    }
	    
//	    LOG.info("Target file \"" + target + "\" will be retrieved.");
	    return path.toString();
	}
	
	@RequestMapping("/listOfImportantDates")
	public String listOfImportantDates(Model model, Principal principal, HttpServletRequest request)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);

		// Important Dates tab
		model.addAttribute("classActiveImportantDates", true);
		model.addAttribute("listOfImportantDates", true);
		model.addAttribute("importantDateList", user.getImportantDateList());
		
		// Working Details tab
		model.addAttribute("listOfWorkingDetails", true);
		model.addAttribute("workingDetailList", user.getWorkingDetailList());
		listOfBranchDetails(model);
		
		return "myProfile";
	}
	
	@RequestMapping("/updateImportantDate")
	public String updateImportantDate(@ModelAttribute("id") Long id, Principal principal, Model model)
	{
		User user = userService.findByUsername(principal.getName());
		Optional<ImportantDate> importantDate = importantDateService.findById(id);
		
		if(user.getId() != importantDate.get().getUser().getId()) 
		{
			return "badRequestPage";
		}
		else
		{
			model.addAttribute("user", user);
			model.addAttribute("importantDate", importantDate);
			model.addAttribute("classActiveImportantDates", true);
			model.addAttribute("addImportantDate", true);
			
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());
			listOfBranchDetails(model);
			
			return "myProfile";
		}
	}
	
	@RequestMapping("/removeImportantDate")
	public String removeImportantDate(@ModelAttribute("id") Long id, Principal principal, Model model)
	{
		User user = userService.findByUsername(principal.getName());
		Optional<ImportantDate> importantDate = importantDateService.findById(id);
		
		if(user.getId() != importantDate.get().getUser().getId())
		{
			String message = "Something went wrong. Contact Admin!!!";
			model.addAttribute("message", message);
			return "badRequestPage";
		} 
		else 
		{
			model.addAttribute("user", user);
			importantDateService.removeById(id);
			
			try
			{
				String name = importantDate.get().getCardType() + importantDate.get().getCardExtention();
				String filePath = createOrRetrieve( PATH + user.getId());
				Files.delete(Paths.get(filePath + "/" + name));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			model.addAttribute("classActiveImportantDates", true);
			model.addAttribute("listOfImportantDates", true);
			
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());
			listOfBranchDetails(model);
			
			return "myProfile";
		}
	}

	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public String updateUserInfo(@ModelAttribute("user") User user, @ModelAttribute("newPassword") String newPassword,
			Model model) throws Exception
	{
		Optional<User> currentUser = userService.findById(user.getId());

		if (currentUser == null)
		{
			throw new Exception("User not found");
		}

		model.addAttribute("classActiveEdit", true);

		/* check email already exists */
		if (userService.findByEmail(user.getEmail()) != null)
		{
			if (userService.findByEmail(user.getEmail()).getId() != currentUser.get().getId())
			{
				model.addAttribute("emailExists", true);
				return "myProfile";
			}
		}

		/* check username already exists */
		if (userService.findByUsername(user.getUsername()) != null)
		{
			if (userService.findByUsername(user.getUsername()).getId() != currentUser.get().getId())
			{
				model.addAttribute("usernameExists", true);
				return "myProfile";
			}
		}

		/* check phoneNumber already exists */
		if (!("").equals(user.getPhone().trim()))
		{
			if (userService.findByPhone(user.getPhone()) != null)
			{
				if (userService.findByPhone(user.getPhone()).getId() != currentUser.get().getId())
				{
					model.addAttribute("phoneExists", true);
					return "myProfile";
				}
			}
		} else
		{
			// TODO: Fix this
			model.addAttribute("needUpdate", true);
			model.addAttribute("needUpdateValue", " Enter Valid Mobile Number. ");

			return "myProfile";
		}

		// update password
		if (newPassword != null && !newPassword.isEmpty() && !newPassword.equals(""))
		{
			BCryptPasswordEncoder passwordEncoder = SecurityUtility.passwordEncoder();
			String dbPassword = currentUser.get().getPassword();
			if (passwordEncoder.matches(user.getPassword(), dbPassword))
			{
				currentUser.get().setPassword(passwordEncoder.encode(newPassword));
			} else
			{
				model.addAttribute("incorrectPassword", true);

				return "myProfile";
			}
		}

		currentUser.get().setFirstName(user.getFirstName());
		currentUser.get().setLastName(user.getLastName());
		currentUser.get().setUsername(user.getUsername());
		currentUser.get().setEmail(user.getEmail());
		currentUser.get().setPhone(user.getPhone());

		userService.save(currentUser.get());

		model.addAttribute("updateSuccess", true);
		model.addAttribute("user", currentUser);
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("workingDetailList", user.getWorkingDetailList());
		model.addAttribute("listOfWorkingDetails", true);
		model.addAttribute("listOfImportantDates", true);
		listOfBranchDetails(model);

		UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.get().getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		// model.addAttribute("orderList", user.getOrderList());

		return "redirect:myProfile";
	}
	
	@RequestMapping(value = "/addPersonalDetail", method = RequestMethod.POST)
	public String addPersonalDetailPost(@ModelAttribute("user") User user,
			Principal principal, Model model) throws Exception
	{
		User currentUser = userService.findByUsername(principal.getName());
		
		MultipartFile profilePicture = user.getProfilePicture();
		String profilePictureExtention = getExtensionOfFile(profilePicture);
		System.out.println("Original FileName " + profilePicture.getOriginalFilename());
		System.out.println("Original contentType " + profilePicture.getContentType());
		
		// Check for Blank Fields || Check for Blank document.
		/*if(isImportantDateFieldsInvalid(user, profilePicture, feedBack))
		{
			model.addAttribute("classActiveImportantDates", true);
			model.addAttribute("user", user);
			model.addAttribute("importantDateList", user.getImportantDateList());
			model.addAttribute("workingDetailList", user.getWorkingDetailList());
			model.addAttribute("addImportantDate", true);
			
			model.addAttribute("invalidImportantDateField", true);
			model.addAttribute("feedBack", feedBack);

			return "myProfile";
		}
*/		
		user.setProfilePictureExtension(profilePictureExtention);
		// Save the Important Table.
		currentUser.setGender(user.getGender());
		currentUser.setMaritialStatus(user.getMaritialStatus());
		currentUser.setDateOfBirth(user.getDateOfBirth());
		currentUser.setEthnicity(user.getEthnicity());
		currentUser.setLivingAddress(user.getLivingAddress());
		currentUser.setFromBranch(user.getFromBranch());
		currentUser.setTechnology(user.getTechnology());
		currentUser.setReffer(user.getReffer());
		currentUser.setDateOfJoining(user.getDateOfJoining());
		currentUser.setEmployeeStatus(user.getEmployeeStatus());
		currentUser.setReportingManager(user.getReportingManager());
		currentUser.setPayrollId(user.getPayrollId());
		currentUser.setPayrollStartDate(user.getPayrollStartDate());
		currentUser.setProfilePictureExtension(user.getProfilePictureExtension());

		try
		{
			byte[] bytes = profilePicture.getBytes();
			String name = currentUser.getUsername() + profilePictureExtention;
			String filePath = createOrRetrieve( PATH + currentUser.getId());
			
			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(new File(filePath + "/" + name)));
			stream.write(bytes);
			stream.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		userService.save(currentUser);
		
		model.addAttribute("currentDesignation", currentUser.getWorkingDetailList().get(0).getDesignation());
		listOfBranchDetails(model);

		return "redirect:myProfilePage";
	}
}
