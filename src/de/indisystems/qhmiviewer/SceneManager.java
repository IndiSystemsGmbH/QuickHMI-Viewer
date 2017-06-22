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

package de.indisystems.qhmiviewer;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class SceneManager {	
	private static Object lastController;
	
	public static Scene getScene(String view, Locale locale){
		try{
			FXMLLoader loader = new FXMLLoader();
			if(locale == null){
				loader.setResources(ResourceBundle.getBundle("de.indisystems.qhmiviewer.resources.Resources", new Locale("en")));
			}else{
				loader.setResources(ResourceBundle.getBundle("de.indisystems.qhmiviewer.resources.Resources", locale));
			}
			loader.setLocation(Main.class.getResource("/de/indisystems/qhmiviewer/view/"+view));
			
		    Pane root;
			try {
				root = (Pane) loader.load();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
			lastController = loader.getController();
			return new Scene(root);
		}catch(Exception e){
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
			return null;
		}
	}
	
	public static Object getLastController(){
		try{
			return lastController;
		}finally{
			lastController = null;
		}
	}
}
