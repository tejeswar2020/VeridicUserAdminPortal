package com.buddy.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.buddy.service.UserService;

public class FeatureCatalog
{
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(FeatureCatalog.class.getName());
	
	public static File createOrRetrieve(final String target) throws IOException
	{
	    final Path path = Paths.get(target);

	    if(Files.notExists(path))
	    {
	        LOG.info("Target file \"" + target + "\" will be created.");
	        return Files.createFile(Files.createDirectories(path)).toFile();
	    }
	    
	    LOG.info("Target file \"" + target + "\" will be retrieved.");
	    return path.toFile();
	}
}
