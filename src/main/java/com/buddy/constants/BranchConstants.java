package com.buddy.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchConstants
{

	public final static Map<String, String> branch = new HashMap<String, String>() 
	{
		{
            put("AR", "Arkansas");
            put("CA", "California");
            put("FL", "Florida");
            put("GA", "Georgia");
            put("KS", "Kansas");
            put("NJ", "New Jersey");
            put("NC", "North Carolina");
            put("TX", "Texas");
		}
	};
	
	public final static List<String> listOfUSBranchCode = new ArrayList<>(branch.keySet());
	public final static List<String> listOfUSBranchName = new ArrayList<>(branch.values());
}
