/*
	Copyright 2019 Indi.Systems GmbH
	
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
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;

import de.indisystems.qhmiviewer.data.DataHandler;
import de.indisystems.qhmiviewer.data.manager.ConfigManager;
import de.indisystems.qhmiviewer.helper.KeyHook;
import de.indisystems.qhmiviewer.model.StartViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class Main extends Application {
	public static final Image ICON = new Image(Main.class.getResourceAsStream("/resources/images/icon.png"));
	
	public static Version currentVersion = new Version("7.3.1");
	
	public static boolean paramLocalhost = false;
	public static String paramAddress = null;
	public static int paramPort = 6062;
	public static boolean paramSsl = false;
	public static String paramConnection = null;
	public static boolean paramMachineMode = false;
	public static String paramMachineModePw = null;
	public static String lastInput = "";
	
	@Override
	public void init() throws Exception {
		System.setProperty("logfiledir", Paths.get(DataHandler.getAppDataDir(), "logs", "viewer").toString());
		
	    // On Mac OS X Chromium engine must be initialized in non-UI thread.
	    if (Environment.isMac()) {
	        BrowserCore.initialize();
	    }
	}
	
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
				} else if(param.equals("-ssl")){
					paramSsl = true;
				} else if(param.startsWith("-connection=")){
					paramConnection = param.substring(12);
				} else if(param.startsWith("-machinemode")) {
					paramMachineMode = true;
					
					if(param.length() >= 12) {
						paramMachineModePw = param.substring(13);
					}
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

			setMachineModeEnabled(paramMachineMode);
		} catch(Exception e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static void setMachineModeEnabled(boolean enabled) {
		paramMachineMode = enabled;
		
		if(paramMachineMode) {
			if(SceneManager.getStartViewController() != null) {
				SceneManager.getStartViewController().hideCloseMenu();
				
				Stage stage = SceneManager.getStartViewController().getStage();
				stage.setFullScreenExitHint("");
				stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
				stage.setFullScreen(true);
			}

			if(SceneManager.getModelController() != null) {
				SceneManager.getModelController().hideCloseMenu();
				
				Stage stage = SceneManager.getModelController().getStage();
				stage.setFullScreenExitHint("");
				stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
				stage.setFullScreen(true);
			}
			
            KeyHook.blockWindowsKey();
		} else {
			if(SceneManager.getStartViewController() != null) {
				SceneManager.getStartViewController().showCloseMenu();
				
				Stage stage = SceneManager.getStartViewController().getStage();
				stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.CONTROL_ANY));
				stage.setFullScreen(false);
			}

			if(SceneManager.getModelController() != null) {
				SceneManager.getModelController().showCloseMenu();
				
				Stage stage = SceneManager.getModelController().getStage();
				stage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.ESCAPE, KeyCombination.CONTROL_ANY));
				stage.setFullScreen(false);
			}
			
            KeyHook.unblockWindowsKey();
		}
	}
}
