/*
	Copyright 2017 Indi.Systems GmbH
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package de.indisystems.qhmiviewer.data.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;

import de.indisystems.qhmiviewer.data.DataHandler;

public class ConfigManager {
	public final static int UPDATE_ALWAYS = 0;
	public final static int UPDATE_NEVER = 1;
	public final static int UPDATE_ASK = 2;
	
	private static Properties config;
	private static File configFile;
	
	public static void init(DataHandler dataHandler){
		try{
			configFile = new File(dataHandler.appDataDir+"\\config\\default.properties");
			
			if(!configFile.exists()){
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			}
			
			FileInputStream in = new FileInputStream(configFile);
			config = new Properties();
			config.load(in);
			in.close();
		}catch(IOException e){
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void setLocale(Locale locale){
		setConfig("language", locale.getLanguage());
	}
	
	public static  Locale getLang(){
		String language = config.getProperty("language");
		
		if(language != null){
			return new Locale(language);
		}else{
			if(Locale.getDefault().getLanguage().toLowerCase().equals("de")){
				return Locale.getDefault();
			}else{
				return Locale.US;
			}
		}
	}
	
	public static void setDefaultConnection(long id){
		setConfig("default_connection", id+"");
	}
	
	public static long getDefaultConnection(){
		String defaultConnection = config.getProperty("default_connection");
		if(defaultConnection == null)
			return -1;
		else
			return Long.parseLong(defaultConnection);
	}
	
	private static void setConfig(String property, String value){
		config.setProperty(property, value);
		
		try {
			FileOutputStream out = new FileOutputStream(configFile);
			config.store(out, "");
			out.close();
		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
}
