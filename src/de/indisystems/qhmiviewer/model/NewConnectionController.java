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

package de.indisystems.qhmiviewer.model;

import java.net.URL;
import java.util.ResourceBundle;

import de.indisystems.qhmiviewer.data.DataHandler;
import de.indisystems.qhmiviewer.data.holder.Connection;
import de.indisystems.qhmiviewer.data.manager.ConfigManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Window;

public class NewConnectionController implements Initializable {
	
	@FXML private Button bttCancel;
	@FXML private Button bttOk;
	
	@FXML private TextField editName;
	@FXML private TextField editHost;
	@FXML private TextField editPort;
	@FXML private CheckBox chkDefault;
	@FXML private CheckBox chkSsl;
	
	private Connection connection;
	private DataHandler dataHandler;
	private StartViewController callback;
	
	public void initializeStage(Connection connection, DataHandler dataHandler, StartViewController callback){
		this.connection = connection;
		this.dataHandler = dataHandler;
		this.callback = callback;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		editHost.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if(editName.getText().equals(oldValue)){
					editName.setText(newValue);
				}
			}
		});
		
		editPort.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
	                editPort.setText(newValue.replaceAll("[^\\d]", ""));
	            }
			}
		});
		
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				//editHost.requestFocus();
				
				if(connection != null){
					editName.setText(connection.getName());
					editHost.setText(connection.getHost());
					editPort.setText(connection.getPort() + "");
					chkSsl.setSelected(connection.isSsl());
					
					if(ConfigManager.getDefaultConnection() == connection.getId())
						chkDefault.setSelected(true);
				}
			}
		});
	}	
	
	@FXML
	private void onBttCancelClick(){
		close();
	}
	
	@FXML
	private void onBttOkClick(){
		//long id = dataHandler.isConnection(editName.getText(), editHost.getText(), Integer.parseInt(editPort.getText()));
		
		if(!editName.getText().trim().equals("") &&
			!editHost.getText().trim().equals("") &&
			!editPort.getText().trim().equals("")){

        	if(connection == null){
        		callback.requestNewConnect(editName.getText().toString(), 
        											editHost.getText().toString(), 
        											Integer.parseInt(editPort.getText()),
        											chkDefault.isSelected(),
        											chkSsl.isSelected());

            	close();
        	}else{
        		if(ConfigManager.getDefaultConnection() == connection.getId() && !chkDefault.isSelected()){
        			ConfigManager.setDefaultConnection(-1);
        		}else if(chkDefault.isSelected()){
        			ConfigManager.setDefaultConnection(connection.getId());
        		}
        		
        		dataHandler.editConnection(editName.getText().toString(), editHost.getText().toString(), Integer.parseInt(editPort.getText()), connection.getId(), chkSsl.isSelected());
        		callback.refreshRecentConnections();
        		//callback.connect(dataHandler.getConnection(connection.getId()));
        		
        		
            	close();
        	}
    	}else{
    		//TODO Message to fill all fields
    	}
	}
	private void close(){
		Window stage = bttCancel.getScene().getWindow();
		stage.hide();
	}
}
