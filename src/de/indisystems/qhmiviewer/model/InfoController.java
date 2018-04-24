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
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;

import de.indisystems.qhmiviewer.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Window;

public class InfoController implements Initializable {
	@FXML private Button bttOk;
	@FXML private Label lblVersion;
	
	@FXML private Hyperlink linkInternet;
	@FXML private Hyperlink linkSupport;
	@FXML private Hyperlink linkGit;
	
	private URI internetUri;
	private URI supportUri;
	private URI gitUri;
	
	@FXML
	private void onBttOkClick(){
		close();
	}
	
	@FXML
	private void onLinkInternetClick(){
		try {
			Desktop.getDesktop().browse(internetUri);
		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
	
	@FXML
	private void onLinkSupportClick(){
		try {
			Desktop.getDesktop().browse(supportUri);
		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
	
	@FXML
	private void onGitClick(){
		try {
			Desktop.getDesktop().browse(gitUri);
		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
	
	private void close(){
		Window stage = bttOk.getScene().getWindow();
		stage.hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lblVersion.setText(Main.currentVersion.toString());
		
		linkInternet.setText(resources.getString("link_internet"));
		linkSupport.setText(resources.getString("link_support"));
		linkGit.setText(resources.getString("link_git"));
		
		try {
			internetUri = new URI(resources.getString("link_internet"));
			supportUri = new URI(resources.getString("link_support"));
			gitUri = new URI(resources.getString("link_git"));
		} catch (URISyntaxException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}
}
