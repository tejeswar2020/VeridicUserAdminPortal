/*package com.buddy.utility;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;

public class FTPUtil 
{
	public static boolean makeDirectories(FTPClient ftpClient, String dirPath) throws IOException 
	{
        String[] pathElements = dirPath.split("/");
        
        if (pathElements != null && pathElements.length > 0) 
        {
            for (String singleDir : pathElements) 
            {
                boolean existed = ftpClient.changeWorkingDirectory(singleDir);
                
                if (!existed) 
                {
                    boolean created = ftpClient.makeDirectory(singleDir);
                    if (created) 
                    {
                        System.out.println("CREATED directory: " + singleDir);
                        ftpClient.changeWorkingDirectory(singleDir);
                    } 
                    else
                    {
                        System.out.println("COULD NOT create directory: " + singleDir);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}*/