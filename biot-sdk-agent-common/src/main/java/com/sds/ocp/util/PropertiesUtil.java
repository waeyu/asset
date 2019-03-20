package com.sds.ocp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.ResourceUtils;

public class PropertiesUtil {
	
	public static File getPropertiesFile(String dir , String fileName ) {
		String path = dir + File.separator + fileName;
		try {
			return ResourceUtils.getFile(path);
		}
		catch (IOException e) {
			File file = new File(path);
			return file;
		}
	}

	public static Properties fetchProperties(File file) {
		Properties properties = new Properties();
		try {
			InputStream in = new FileInputStream(file);
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static void saveProperties(File file, Properties props) {
		try {
			OutputStream out = new FileOutputStream(file);
			DefaultPropertiesPersister p = new DefaultPropertiesPersister();
			p.store(props, out, "apps update properties.");			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
