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
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;

public class OpenConnectionController implements Initializable {
	@FXML private Button bttOk;
	@FXML private ComboBox<Connection> cbConnections;
	private StartViewController callback;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	public void initializeStage(DataHandler dataHandler, StartViewController callback){		
		ObservableList<Connection> connections = dataHandler.getRecentConnections();
		if(connections != null){
			cbConnections.setItems(connections);
		}
		
		cbConnections.getSelectionModel().select(0);
		
		this.callback = callback;
	}
	
	@FXML
	private void onBttOkClick(){
		if(cbConnections.getSelectionModel().getSelectedItem() != null){
			callback.connect(cbConnections.getSelectionModel().getSelectedItem());
			close();
		}
	}
	
	@FXML
	private void onBttCancelClick(){
		close();
	}
	
	private void close(){
		Window stage = bttOk.getScene().getWindow();
		stage.hide();
	}
}
