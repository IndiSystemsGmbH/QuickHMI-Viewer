/*
	Copyright 2018 Indi.Systems GmbH
	
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

package de.indisystems.qhmiviewer.model;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;

import com.teamdev.jxbrowser.chromium.BeforeSendHeadersParams;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserException;
import com.teamdev.jxbrowser.chromium.HttpHeadersEx;
import com.teamdev.jxbrowser.chromium.events.ConsoleEvent;
import com.teamdev.jxbrowser.chromium.events.ConsoleListener;
import com.teamdev.jxbrowser.chromium.events.FailLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.FrameLoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadEvent;
import com.teamdev.jxbrowser.chromium.events.LoadListener;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.javafx.BrowserView;
import com.teamdev.jxbrowser.chromium.javafx.DefaultNetworkDelegate;

import de.indisystems.qhmiviewer.Main;
import de.indisystems.qhmiviewer.SceneManager;
import de.indisystems.qhmiviewer.data.DataHandler;
import de.indisystems.qhmiviewer.data.holder.Connection;
import de.indisystems.qhmiviewer.data.manager.ConfigManager;
import de.indisystems.qhmiviewer.data.manager.UiEventManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ModelController implements Initializable {

	private Stage stage;
	private Stage startViewStage;

	public Connection connection;

	private UiEventManager uiEventManager = new UiEventManager();
	@FXML
	private MenuItem menuItemCloseProject;
	@FXML
	private MenuItem menuItemClosePlayer;
	
	@FXML
	private VBox vboxMainContainer;
	
	@FXML
	private ProgressIndicator progressIndicator;

	private Browser browser;
	private BrowserView browserView;

	private void addListeners() {

		uiEventManager.addActionEvent(menuItemClosePlayer, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				onMenuItemCloseClick();
			}
		});

		uiEventManager.addActionEvent(menuItemCloseProject, new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				onMenuItemCloseProjectClick();
			}
		});
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			stage = null;
		} finally {
			super.finalize();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addListeners();
		
		int counter = 1;
		Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"), "chromium");
		
		while(browser == null || browser.getContext() == null) {
			try {
				if(counter > 100) {
					break;
				}
				BrowserContext browserContext = new BrowserContext(new BrowserContextParams(Paths.get(tempPath.toString(), "JxBrowser_" + counter++).toString()));
				browser = new Browser(browserContext);
			} catch(BrowserException ex) {
			}
		}
		
		browser.getContext().getNetworkService().setNetworkDelegate(new DefaultNetworkDelegate() {
		    @Override
		    public void onBeforeSendHeaders(BeforeSendHeadersParams params) {
		        HttpHeadersEx headers = params.getHeadersEx();
		        headers.setHeader("BrowserType", "JxBrowser");
		    }
		});
		
		browser.addLoadListener(new LoadListener() {
			
			@Override
			public void onStartLoadingFrame(StartLoadingEvent arg0) {
			}
			
			@Override
			public void onProvisionalLoadingFrame(ProvisionalLoadingEvent arg0) {
			}
			
			@Override
			public void onFinishLoadingFrame(FinishLoadingEvent arg0) {
				browserView.setVisible(true);
				progressIndicator.setVisible(false);
			}
			
			@Override
			public void onFailLoadingFrame(FailLoadingEvent arg0) {
				try{
					String errorTemplate = new String(Files.readAllBytes(Paths.get(ModelController.class.getResource("/resources/web/error.html").getPath().substring(1))));
					String errorMessage = String.format(resources.getString("server_not_available"), connection.getUrl());
					String error = errorTemplate.replace("{0}", errorMessage);
					browser.loadHTML(error);
				}catch(IOException ex){
					LogManager.getLogger().error(ex.getLocalizedMessage(), ex);
				}
			}
			
			@Override
			public void onDocumentLoadedInMainFrame(LoadEvent arg0) {
			}
			
			@Override
			public void onDocumentLoadedInFrame(FrameLoadEvent arg0) {
			}
		});
		
		browser.addConsoleListener(new ConsoleListener() {
			
			@Override
			public void onMessage(ConsoleEvent consoleEvent) {
				switch(consoleEvent.getLevel()) {
					case DEBUG:
						LogManager.getLogger().debug(consoleEvent.getMessage());
						break;
					case ERROR:
						LogManager.getLogger().error(consoleEvent.getMessage());
						break;
					case LOG:
						LogManager.getLogger().info(consoleEvent.getMessage());
						break;
					case WARNING:
						LogManager.getLogger().warn(consoleEvent.getMessage());
						break;
					default:
						break;
				}
			}
		});
		
        browserView = new BrowserView(browser);
        
        //Bind managed property to visible property. This way if made invisble it won't take any space in its parent
        browserView.managedProperty().bind(browserView.visibleProperty());
        progressIndicator.managedProperty().bind(progressIndicator.visibleProperty());
        
        browserView.setVisible(false);
        
        vboxMainContainer.getChildren().add(2, browserView);
        VBox.setVgrow(browserView, Priority.ALWAYS);
	}

	public void setStageAndImplementListeners(Connection connection, Stage stage, Stage startViewStage, DataHandler dataHandler) {
		this.startViewStage = startViewStage;
		this.stage = stage;
		this.connection = connection;
		this.stage.getIcons().add(Main.ICON);
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				shutDown();
			}
		});
		
		browser.loadURL(connection.getUrl());
	}
	
	public void shutDown() {
		stage.close();
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	@FXML
	private void onMenuItemCloseClick() {
		shutDown();
	}

	@FXML
	private void onMenuItemCloseProjectClick() {
		if (Environment.isWindows()) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					browser.dispose();
				}
			}).start();
	        BrowserCore.initialize();
	    } else {
			browser.dispose();
	    }
		
		loadStartView();
	}

	public void loadStartView() {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				close();
			}
		});
	}

	@FXML
	private void onBttInfoClick() {
		Stage infoStage = new Stage();
		infoStage.initModality(Modality.APPLICATION_MODAL);
		infoStage.getIcons().add(Main.ICON);
		infoStage.setScene(SceneManager.getScene("InfoView.fxml", ConfigManager.getLang()));
		infoStage.setTitle("Info");
		infoStage.setResizable(false);

		infoStage.show();
	}

	@FXML
	private void onBttForumClick() {
		String url = "http://forum.indi-systems.com/";

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("xdg-open " + url);
			} catch (IOException e) {
				LogManager.getLogger().error(e.getLocalizedMessage(), e);
			}
		}
	}

	private void close() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				uiEventManager.removeAllEvents();
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						stage.close();
						startViewStage.show();
					}
				});
			}
		}).start();
	}
}
