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
import java.util.Locale;
import java.util.ResourceBundle;

import de.indisystems.qhmiviewer.data.manager.ConfigManager;
import javafx.scene.control.ComboBox;
import javafx.stage.Window;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class SettingsController implements Initializable {	
	@FXML private ComboBox<String> cmbLanguage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> languages = FXCollections.observableArrayList("English", "Deutsch");
		
		cmbLanguage.setItems(languages);
		cmbLanguage.setEditable(false);
	}
	
	public void initialize(){
		Locale locale = ConfigManager.getLang();
		if(locale != null && locale.getLanguage().equals("de")){
			cmbLanguage.getSelectionModel().select(1);
		}else{
			cmbLanguage.getSelectionModel().select(0);			
		}
	}
	
	@FXML 
	private void onBttSaveClick(){
		if(cmbLanguage.getSelectionModel().getSelectedIndex() == 0){
			ConfigManager.setLocale(new Locale("en"));
		}else if(cmbLanguage.getSelectionModel().getSelectedIndex() == 1){
			ConfigManager.setLocale(new Locale("de"));
		}		

		close();
	}
	
	@FXML 
	private void onBttCancelClick(){
		close();
	}
	
	private void close(){
		Window stage = cmbLanguage.getScene().getWindow();
		stage.hide();
	}
}
