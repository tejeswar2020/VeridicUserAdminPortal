package com.buddy.controller;

import java.security.Principal;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
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

import com.buddy.domain.ImportantDate;
import com.buddy.domain.User;
import com.buddy.domain.WorkingDetail;
import com.buddy.domain.security.PasswordResetToken;
import com.buddy.domain.security.Role;
import com.buddy.domain.security.UserRole;
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

		return "myProfile";
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

		return "myProfile";
	}

	@RequestMapping("/addWorkingDetail")
	public String addWorkingDetail(Model model, Principal principal)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);
		
		WorkingDetail workingDetail = new WorkingDetail();
		model.addAttribute("workingDetail", workingDetail);

		model.addAttribute("addWorkingDetail", true);
		model.addAttribute("classActiveWorkingDetails", true);
		model.addAttribute("listOfImportantDates", true);
		model.addAttribute("importantDateList", user.getImportantDateList());

		return "myProfile";
	}

	@RequestMapping(value = "/addWorkingDetail", method = RequestMethod.POST)
	public String addWorkingDetailPost(@ModelAttribute("workingDetail") WorkingDetail workingDetail,
			Principal principal, Model model)
	{

		User user = userService.findByUsername(principal.getName());
		userService.updateWorkingDetail(workingDetail, user);

		model.addAttribute("user", user);
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("workingDetailList", user.getWorkingDetailList());

		model.addAttribute("classActiveWorkingDetails", true);
		model.addAttribute("listOfWorkingDetails", true);

		return "redirect:listOfWorkingDetails";
	}

	@RequestMapping("/updateWorkingDetail")
	public String updateCreditCard(@ModelAttribute("id") Long id, Principal principal, Model model)
	{
		User user = userService.findByUsername(principal.getName());
		WorkingDetail workingDetail = workingDetailService.findById(id);
		
		if(user.getId() != workingDetail.getUser().getId()) 
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
			
			return "myProfile";
		}
	}
	
	@RequestMapping("/removeWorkingDetail")
	public String removeCreditCard(@ModelAttribute("id") Long id, Principal principal, Model model)
	{
		User user = userService.findByUsername(principal.getName());
		WorkingDetail workingDetail = workingDetailService.findById(id);
		
		if(user.getId() != workingDetail.getUser().getId())
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
			
			return "myProfile";
		}
	}
	@RequestMapping("/addImportantDate")
	public String addImportantDate(Model model, Principal principal)
	{
		User user = userService.findByUsername(principal.getName());
		model.addAttribute("user", user);

		model.addAttribute("classActiveImportantDates", true);
		model.addAttribute("addImportantDate", true);
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("listOfWorkingDetails", true);
		model.addAttribute("workingDetailList", user.getWorkingDetailList());

		return "myProfile";
	}
	
	@RequestMapping(value = "/addImportantDate", method = RequestMethod.POST)
	public String addImportantDatesPost(@ModelAttribute("importantDates") ImportantDate importantDate,
			Principal principal, Model model) throws Exception
	{
		User user = userService.findByUsername(principal.getName());
		userService.updateImportantDates(user, importantDate);

		model.addAttribute("classActiveImportantDates", true);
		model.addAttribute("user", user);
		model.addAttribute("importantDateList", user.getImportantDateList());
		model.addAttribute("workingDetailList", user.getWorkingDetailList());
		model.addAttribute("listOfImportantDates", true);

		return "redirect:listOfImportantDates";
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
		
		return "myProfile";
	}

	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public String updateUserInfo(@ModelAttribute("user") User user, @ModelAttribute("newPassword") String newPassword,
			Model model) throws Exception
	{
		User currentUser = userService.findById(user.getId());

		if (currentUser == null)
		{
			throw new Exception("User not found");
		}

		model.addAttribute("classActiveEdit", true);

		/* check email already exists */
		if (userService.findByEmail(user.getEmail()) != null)
		{
			if (userService.findByEmail(user.getEmail()).getId() != currentUser.getId())
			{
				model.addAttribute("emailExists", true);
				return "myProfile";
			}
		}

		/* check username already exists */
		if (userService.findByUsername(user.getUsername()) != null)
		{
			if (userService.findByUsername(user.getUsername()).getId() != currentUser.getId())
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
				if (userService.findByPhone(user.getPhone()).getId() != currentUser.getId())
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
			String dbPassword = currentUser.getPassword();
			if (passwordEncoder.matches(user.getPassword(), dbPassword))
			{
				currentUser.setPassword(passwordEncoder.encode(newPassword));
			} else
			{
				model.addAttribute("incorrectPassword", true);

				return "myProfile";
			}
		}

		currentUser.setFirstName(user.getFirstName());
		currentUser.setLastName(user.getLastName());
		currentUser.setUsername(user.getUsername());
		currentUser.setEmail(user.getEmail());
		currentUser.setPhone(user.getPhone());

		userService.save(currentUser);

		model.addAttribute("updateSuccess", true);
		model.addAttribute("user", currentUser);

		model.addAttribute("listOfShippingAddresses", true);
		model.addAttribute("listOfCreditCards", true);

		UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
				userDetails.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		// model.addAttribute("orderList", user.getOrderList());

		return "myProfile";
	}
}
