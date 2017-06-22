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
	
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import de.indisystems.qhmiviewer.data.DataHandler;
import de.indisystems.qhmiviewer.data.manager.ConfigManager;
import de.indisystems.qhmiviewer.model.StartViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Main extends Application {
	public static final Image ICON = new Image(Main.class.getResourceAsStream("/resources/images/icon.png"));
	
	public static Version currentVersion = new Version("5.5.0");
	
	public static boolean paramLocalhost = false;
	public static String paramAddress = null;
	public static int paramPort = 7072;
	public static boolean paramSsl = true;
	public static String paramConnection = null;
	
	@Override
	public void start(Stage primaryStage) {
		try {				
			List<String> params = getParameters().getUnnamed();
			
			for(String param : params){
				if(param.equals("-localhost")) {
					paramLocalhost = true;
				} else if(param.startsWith("-address=")){
					paramAddress = param.substring(9);
				} else if(param.startsWith("-port=")){
					paramPort = Integer.parseInt(param.substring(6));
				} else if(param.startsWith("-ssl=")){
					paramSsl = Boolean.parseBoolean(param.substring(5));
				} else if(param.startsWith("-connection=")){
					paramConnection = param.substring(12);
				}
			}
			
			DataHandler dataHandler = new DataHandler();
			ConfigManager.init(dataHandler);
			Scene scene = SceneManager.getScene("StartView.fxml", ConfigManager.getLang());
			primaryStage.setScene(scene);
			primaryStage.setTitle("QuickHMI Viewer");
			primaryStage.getIcons().add(Main.ICON);
			primaryStage.show();
			
			StartViewController controller = (StartViewController)SceneManager.getLastController();
			controller.implementListeners(primaryStage, dataHandler);
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent arg0) {
					Platform.exit();
					System.exit(0);
				}
			});
			
		} catch(Exception e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
