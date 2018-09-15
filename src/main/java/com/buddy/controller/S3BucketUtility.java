package com.buddy.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3BucketUtility 
{
	private static final String SUFFIX = "/";
	private static final String bucketName = "veridicportal";
	
	private static AmazonS3 s3client = null;
	
	public S3BucketUtility()
	{
		super();
		LoginToAWS();
	}

	/*public static void main(String[] args) 
	{
		LoginToAWS();
		
		// create folder into bucket
		String folderName = "testfolder";
		createFolder(folderName);
		
		String fileName = folderName + SUFFIX + "test.xml";
		File file = new File("C:\\Users\\pavan\\Downloads\\web.xml");
		uploadFile(folderName, fileName, file);
		
//		deleteFolder(bucketName, folderName, s3client);
		deleteFile(folderName, fileName);
		
		// deletes bucket
//		s3client.deleteBucket(bucketName);
	}*/
	
	public void uploadFile(String folderName, String fileName, File file)
	{
		// upload file to folder and set it to public
		s3client.putObject(
				new PutObjectRequest(bucketName,
									fileName,
									file).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	@SuppressWarnings("deprecation")
	private void LoginToAWS()
	{
		// create a client connection based on credentials
		if (null == s3client)
		{
			// credentials object identifying user for authentication
			// user must have AWSConnector and AmazonS3FullAccess for 
			// this example to work
			AWSCredentials credentials = new BasicAWSCredentials("AKIAI5OULIO4O4OJPPJQ", "iA2cm8R+xYynHJSJz1rejxQvEHTK0wWwNJ0QIdOc");
			
			s3client = new AmazonS3Client(credentials);
		}
		
		// create bucket - name must be unique for all S3 users
        // String bucketName = "veridicportal";
		// s3client.createBucket(bucketName);
		
		// list buckets
		for (Bucket bucket : s3client.listBuckets()) 
		{
			System.out.println(" - " + bucket.getName());
		}
	}

	public void createFolder(String folderName) 
	{
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);

		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
				folderName + SUFFIX, emptyContent, metadata);

		// send request to S3 to create folder
		s3client.putObject(putObjectRequest);
	}

	/**
	 * This method first deletes all the files in given folder and than the
	 * folder itself
	 */
	public void deleteFolder(String folderName) 
	{
		List<S3ObjectSummary> fileList1 = s3client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList1) 
		{
			s3client.deleteObject(bucketName, file.getKey());
		}
		s3client.deleteObject(bucketName, folderName);
	}
	
	/**
	 * This method first deletes the file in the given folder.
	 */
	public void deleteFile(String folderName, String fileName) 
	{
		System.out.println("Enter in deleteFile()");
		List<S3ObjectSummary> fileList = s3client.listObjects(bucketName, folderName).getObjectSummaries();
		for (S3ObjectSummary file : fileList) 
		{
			if (fileName.equals(file.getKey()))
			{
				s3client.deleteObject(bucketName, file.getKey());
			}
		}
	}
}