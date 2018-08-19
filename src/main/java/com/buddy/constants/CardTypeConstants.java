package com.buddy.constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardTypeConstants
{
	public final static Map<String, String> cardType = new HashMap<String, String>() 
	{
		{
			put("NONE", "NONE");
			put("CPT", "CPT");
            put("OPT", "OPT");
            put("OPT - Ext", "OPT - Extenstion");
            put("H1B", "H1B");
            put("L1", "L1");
            put("H4", "H4");
            put("GC - EAD", "GC - EAD");
            put("GC", "GC");
		}
	};
	
	public final static List<String> listOfCardTypeCode = new ArrayList<>(cardType.keySet());
	public final static List<String> listOfCardTypeName = new ArrayList<>(cardType.values());
}
