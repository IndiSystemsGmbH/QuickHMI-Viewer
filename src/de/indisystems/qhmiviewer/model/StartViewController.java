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

package de.indisystems.qhmiviewer.model;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;

import de.indisystems.qhmiviewer.Main;
import de.indisystems.qhmiviewer.SceneManager;
import de.indisystems.qhmiviewer.data.DataHandler;
import de.indisystems.qhmiviewer.data.holder.Connection;
import de.indisystems.qhmiviewer.data.manager.ConfigManager;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;

public class StartViewController implements Initializable {
	public static String TAG = "StartViewController";

	public static String DEBUG_PHRASE = "#indi#";
	
	@FXML private HBox paneLoading;
	
	@FXML private Label bttMenuItem1;
	@FXML private Label bttMenuItem2;
	@FXML private Label bttMenuItem3;
	@FXML private Label bttMenuItem4;
	@FXML private Label bttNewConnection;
	@FXML private Label bttOpenConnection;
	
	@FXML private ImageView imgBttMenuItem1;
	@FXML private ImageView imgBttMenuItem2;
	@FXML private ImageView imgBttMenuItem3;
	@FXML private ImageView imgBttMenuItem4;
	
	@FXML private MenuItem menuItemClose;
	
	@FXML private Label lblStartHeader;
	@FXML private Label lblStartContent;
	@FXML private Label lblAlertMessage;
	@FXML private AnchorPane paneAlert;
	@FXML private ImageView bttCloseAlertMessage;
	
	@FXML private RadioMenuItem rbLangEn;
	@FXML private RadioMenuItem rbLangDe;
	
	@FXML private ListView<Connection> listRecConn;

	private ResourceBundle resources;
	private DataHandler dataHandler;
	private Stage startViewStage;
	
	@FXML
	public void onMenuItemCloseClick(){
		Platform.exit();
		System.exit(0);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;
	}	
	
	public void implementListeners(Stage stage, DataHandler dataHandler){
		this.startViewStage = stage;
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent arg0) {
				Platform.exit();
				System.exit(0);
			}
		});
		
		this.dataHandler = dataHandler;
		
		if(Main.paramLocalhost){
			Connection temp = new Connection("Editor Runtime", "localhost", Main.paramPort, 0, -1, Main.paramSsl);
			connect(temp);
		}else{			
			lblStartHeader.setText(resources.getString("welcome_header"));
			lblStartContent.setText(resources.getString("welcome_content"));
			
			listRecConn.setCellFactory(new Callback<ListView<Connection>, ListCell<Connection>>() {
				
				@Override
				public ListCell<Connection> call(ListView<Connection> param) {
					return new RecentConnectionListCell();
				}
			});

			refreshRecentConnections();
			
			if(Main.paramConnection != null){
				Connection connection = dataHandler.getConnection(Main.paramConnection);
				if(connection != null){
					connect(connection);
				}
				
			}else if(Main.paramAddress != null){
				Connection temp = new Connection("Unnamed", Main.paramAddress, Main.paramPort, 0, -1, Main.paramSsl);
				connect(temp);
			}else if(ConfigManager.getDefaultConnection() != -1){
				Connection connection = dataHandler.getConnection(ConfigManager.getDefaultConnection());
				if(connection != null){
					connect(connection);
				}else{
					ConfigManager.setDefaultConnection(-1);
				}
			}
		}
	}
	
	@FXML
	private void onBttCloseAlertMessageClick(){
		paneAlert.setVisible(false);
	}
	
	@FXML 
	private void onBttMenuItem1MouseEnter(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem1);
		scaleTransition.setFromX(1);
		scaleTransition.setToX(1.25);
		scaleTransition.setFromY(1);
		scaleTransition.setToY(1.25);
		scaleTransition.play();
		bttMenuItem1.setFont(Font.font(null, FontWeight.BOLD, 14));
	}
	
	@FXML 
	private void onBttMenuItem1MouseLeave(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem1);
		scaleTransition.setFromX(1.25);
		scaleTransition.setToX(1);
		scaleTransition.setFromY(1.25);
		scaleTransition.setToY(1);
		scaleTransition.play();
		bttMenuItem1.setFont(Font.font(null, FontWeight.NORMAL, 14));
	}
	
	@FXML 
	private void onBttMenuItem1Click(){
		lblStartHeader.setText(resources.getString("menu_item_1_caption"));
		lblStartContent.setText(resources.getString("menu_item_1_content"));
	}
	
	@FXML 
	private void onBttForumClick(){
		String url = "http://forum.indi-systems.com/";

        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
				desktop.browse(new URI(url));
			} catch (IOException | URISyntaxException e) {
				LogManager.getLogger().error(e.getLocalizedMessage(), e);
			}
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("xdg-open " + url);
            } catch (IOException e) {
				LogManager.getLogger().error(e.getLocalizedMessage(), e);
            }
        }
	}
	
	@FXML 
	private void onBttMenuItem2MouseEnter(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem2);
		scaleTransition.setFromX(1);
		scaleTransition.setToX(1.25);
		scaleTransition.setFromY(1);
		scaleTransition.setToY(1.25);
		scaleTransition.play();
		bttMenuItem2.setFont(Font.font(null, FontWeight.BOLD, 14));
	}
	
	@FXML 
	private void onBttMenuItem2MouseLeave(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem2);
		scaleTransition.setFromX(1.25);
		scaleTransition.setToX(1);
		scaleTransition.setFromY(1.25);
		scaleTransition.setToY(1);
		scaleTransition.play();
		bttMenuItem2.setFont(Font.font(null, FontWeight.NORMAL, 14));
	}

	@FXML 
	private void onBttMenuItem2Click(){
		lblStartHeader.setText(resources.getString("menu_item_2_caption"));
		lblStartContent.setText(resources.getString("menu_item_2_content"));
	}
	
	@FXML 
	private void onBttMenuItem3MouseEnter(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem3);
		scaleTransition.setFromX(1);
		scaleTransition.setToX(1.25);
		scaleTransition.setFromY(1);
		scaleTransition.setToY(1.25);
		scaleTransition.play();
		bttMenuItem3.setFont(Font.font(null, FontWeight.BOLD, 14));
	}
	
	@FXML 
	private void onBttMenuItem3MouseLeave(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem3);
		scaleTransition.setFromX(1.25);
		scaleTransition.setToX(1);
		scaleTransition.setFromY(1.25);
		scaleTransition.setToY(1);
		scaleTransition.play();
		bttMenuItem3.setFont(Font.font(null, FontWeight.NORMAL, 14));
	}
	
	@FXML 
	private void onBttMenuItem3Click(){
		lblStartHeader.setText(resources.getString("menu_item_3_caption"));
		lblStartContent.setText(resources.getString("menu_item_3_content"));
	}
	
	@FXML 
	private void onBttMenuItem4MouseEnter(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem4);
		scaleTransition.setFromX(1);
		scaleTransition.setToX(1.25);
		scaleTransition.setFromY(1);
		scaleTransition.setToY(1.25);
		scaleTransition.play();
		bttMenuItem4.setFont(Font.font(null, FontWeight.BOLD, 14));
	}
	
	@FXML 
	private void onBttMenuItem4MouseLeave(){
		ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), imgBttMenuItem4);
		scaleTransition.setFromX(1.25);
		scaleTransition.setToX(1);
		scaleTransition.setFromY(1.25);
		scaleTransition.setToY(1);
		scaleTransition.play();
		bttMenuItem4.setFont(Font.font(null, FontWeight.NORMAL, 14));
	}
	
	@FXML 
	private void onBttMenuItem4Click(){
		lblStartHeader.setText(resources.getString("menu_item_4_caption"));
		lblStartContent.setText(resources.getString("menu_item_4_content"));
	}
	
	@FXML 
	private void onBttNewConnectionMouseEnter(){
		bttNewConnection.setFont(Font.font(null, FontWeight.BOLD, 14));
	}
	
	@FXML 
	private void onBttNewConnectionMouseLeave(){
		bttNewConnection.setFont(Font.font(null, FontWeight.NORMAL, 14));
	}
	
	@FXML
	private void onMenuItemSettingsClick(){
		openSettingsDialog();
	}
	
	private void openSettingsDialog(){
		Stage settingsStage = new Stage();
		settingsStage.initModality(Modality.APPLICATION_MODAL);
		settingsStage.setScene(SceneManager.getScene("SettingsView.fxml", ConfigManager.getLang()));
		settingsStage.getIcons().add(Main.ICON);
		settingsStage.setTitle(resources.getString("settings"));
		settingsStage.setResizable(false);
		
		SettingsController controller = (SettingsController) SceneManager.getLastController();
		controller.initialize();
		
		settingsStage.show();	
	}
	
	private void openNewConnectionDialog(Connection connection){
		Stage newConnStage = new Stage();
		newConnStage.initModality(Modality.APPLICATION_MODAL);
		newConnStage.setScene(SceneManager.getScene("NewConnectionView.fxml", ConfigManager.getLang()));
		newConnStage.getIcons().add(Main.ICON);
		if(connection == null){
			newConnStage.setTitle(resources.getString("new_connection"));
		}else{
			newConnStage.setTitle(resources.getString("edit_connection"));			
		}
		newConnStage.setResizable(false);
		
		NewConnectionController controller = (NewConnectionController) SceneManager.getLastController();
		controller.initializeStage(connection, dataHandler, this);
		
		newConnStage.show();		
	}
	
	@FXML 
	private void onBttNewConnectionClick(){
		openNewConnectionDialog(null);
	}
	
	@FXML
	private void onBttOpenConnectionClick(){
		Stage openConnStage = new Stage();
		openConnStage.initModality(Modality.APPLICATION_MODAL);
		openConnStage.setScene(SceneManager.getScene("OpenConnectionView.fxml", ConfigManager.getLang()));
		openConnStage.getIcons().add(Main.ICON);
		openConnStage.setTitle(resources.getString("open_connection"));
		openConnStage.setResizable(false);
		
		OpenConnectionController controller = (OpenConnectionController) SceneManager.getLastController();
		controller.initializeStage(dataHandler, this);
		
		openConnStage.show();
	}
	
	@FXML 
	private void onBttOpenConnectionMouseEnter(){
		bttOpenConnection.setFont(Font.font(null, FontWeight.BOLD, 14));
	}
	
	@FXML 
	private void onBttOpenConnectionMouseLeave(){
		bttOpenConnection.setFont(Font.font(null, FontWeight.NORMAL, 14));
	}
	
	@FXML
	private void onRbLangDeClick(){
		ConfigManager.setLocale(new Locale("de", "DE"));
	}
	
	@FXML
	private void onRbLangEnClick(){
		ConfigManager.setLocale(new Locale("en", "US"));		
	}
	
	@FXML
	private void onBttInfoClick(){
		Stage infoStage = new Stage();
		infoStage.initModality(Modality.APPLICATION_MODAL);
		infoStage.getIcons().add(Main.ICON);
		infoStage.setScene(SceneManager.getScene("InfoView.fxml", ConfigManager.getLang()));
		infoStage.setTitle("Info");
		infoStage.setResizable(false);
		
		infoStage.show();
	}
	
	/**
     * Trys to establish a connection. Is either called when a new connection is added, or when a recent connection is selected.
     * @param name Name of connection
     * @param host Host of connection
     * @param port Port of connection
     * @param project Project of connection
     * @param needAuthentication If set to true, user will be prompted with a login
     */
	public void requestNewConnect(String name, String host, int port, boolean isDefault, boolean isSsl) {
		long recentConnection = dataHandler.isConnection(name, host, port);
		
		if(recentConnection == -1){
			Connection connection = dataHandler.addConnection(name, host, port, isSsl);
			if(isDefault)
				ConfigManager.setDefaultConnection(connection.getId());
			connect(connection);
				
			refreshRecentConnections();
		}else{
			Connection connection = dataHandler.getConnection(recentConnection);
			connect(connection);
		}
	}
	
	public void connect(Connection connection){
		paneLoading.setVisible(true);
		dataHandler.updateLastConnected(connection);
		refreshRecentConnections();
		
		loadModelStage(connection);
	}
	
	public void refreshRecentConnections(){
		ObservableList<Connection> connections = dataHandler.getRecentConnections();
		if(connections != null){
			listRecConn.setItems(null);
			listRecConn.setItems(connections);
			if(connections.size() > 0){
				Connection tmp = connections.get(0);
				connections.remove(0);
				connections.add(0, tmp);
			}
		}
	}
	
	private Stage modelstage = null;
	private ModelController controller;
	public void loadModelStage(Connection connection){
		System.gc();
		if(modelstage == null){
			modelstage = new Stage();
		}
		
		modelstage.getIcons().add(Main.ICON);
		modelstage.setScene(SceneManager.getScene("ModelView.fxml", ConfigManager.getLang()));
		modelstage.setTitle(connection.getName());
		modelstage.setMaximized(true);
		
		if(controller != null){
			controller = null;
		}
		controller = (ModelController) SceneManager.getLastController();
		
		controller.setStageAndImplementListeners(connection,
												modelstage,
												startViewStage,
												dataHandler);
		
		paneLoading.setVisible(false);
		close();
		modelstage.show();
	}
	
	private void close(){
		startViewStage.hide();
	}
	
	private class RecentConnectionListCell extends ListCell<Connection>{
		private long id;
		private HBox hBox = new HBox();
		private Label name = new Label();
		private Pane pane = new Pane();
		private ImageView imgBttEdit = new ImageView();
		private ImageView imgBttDelete = new ImageView();
		
		public RecentConnectionListCell(){
			configureName();
			configurePane();
			configureButtons();
			configureHBox();
		}
		
		private void configureHBox(){
			
			this.setOnMouseEntered(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					imgBttEdit.setVisible(true);
					imgBttDelete.setVisible(true);
					name.setFont(Font.font("System", FontWeight.BOLD, 12));
				}
			});
			
			this.setOnMouseExited(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					imgBttEdit.setVisible(false);
					imgBttDelete.setVisible(false);
					if(ConfigManager.getDefaultConnection() != id){
						name.setFont(Font.font("System", FontWeight.NORMAL, 12));
					}
				}
			});
			
			hBox.getChildren().addAll(name, pane, imgBttEdit, imgBttDelete);
			hBox.setSpacing(5);
		}
		
		private void configureButtons(){
			imgBttEdit.setFitWidth(16);
			imgBttEdit.setFitHeight(16);
			imgBttEdit.setImage(new Image(RecentConnectionListCell.class.getResource("/resources/images/pencil.png").toExternalForm()));
			imgBttEdit.setPickOnBounds(true);
			imgBttEdit.setVisible(false);
			
			imgBttDelete.setFitWidth(16);
			imgBttDelete.setFitHeight(16);
			imgBttDelete.setImage(new Image(RecentConnectionListCell.class.getResource("/resources/images/delete.png").toExternalForm()));
			imgBttDelete.setPickOnBounds(true);
			imgBttDelete.setVisible(false);
		}
		
		private void configurePane(){
			HBox.setHgrow(pane, Priority.ALWAYS);
		}
		
		private void configureName(){
			name.setWrapText(true);
		}
		
		@Override
		protected void updateItem(Connection connection, boolean empty) {
			super.updateItem(connection, empty);
			if(empty || connection == null){
				clearContent();
			}else{
				this.id = connection.getId();
				addContent(connection);
			}
		}
		
		private void clearContent(){
			setText(null);
			setGraphic(null);
			setBackground(null);
			this.setCursor(Cursor.DEFAULT);
		}
		
		private void addContent(Connection connection){
			this.setCursor(Cursor.HAND);
			setText(null);
			name.setText(connection.getName());
			if(ConfigManager.getDefaultConnection() == connection.getId()){
				name.setFont(Font.font("System", FontWeight.BOLD, 12));
			}else{
				name.setFont(Font.font("System", FontWeight.NORMAL, 12));
			}
			
			name.setOnMouseClicked(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					connect(listRecConn.getSelectionModel().getSelectedItem());
				}
				
			});
			
			pane.setOnMouseClicked(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					connect(listRecConn.getSelectionModel().getSelectedItem());
				}
				
			});
			
			imgBttEdit.setOnMouseClicked(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					openNewConnectionDialog(connection);
				}
			});
			
			imgBttDelete.setOnMouseClicked(new EventHandler<Event>() {

				@Override
				public void handle(Event event) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle(resources.getString("delete_connection"));
					alert.setHeaderText(String.format(resources.getString("delete_connection_message"), connection.getName()));
					
					ButtonType buttonTypeYes = new ButtonType(resources.getString("yes"));
					ButtonType buttonTypeNo = new ButtonType(resources.getString("no"));
					
					alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
					
					Optional<ButtonType> result = alert.showAndWait();
					if(result.get() == buttonTypeYes){
						dataHandler.deleteConnection(connection.getId());
						if(ConfigManager.getDefaultConnection() == id){
							ConfigManager.setDefaultConnection(-1);
						}
						refreshRecentConnections();
					}
				}
			});
			setGraphic(hBox);
		}
	}
}
