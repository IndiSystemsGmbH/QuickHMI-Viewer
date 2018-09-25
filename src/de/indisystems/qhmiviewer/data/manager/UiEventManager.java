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

package de.indisystems.qhmiviewer.data.manager;

import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class UiEventManager {
	
	private HashMap<Node, EventHandler<KeyEvent>> keyPressedEvents = new HashMap<>();
	private HashMap<Node, EventHandler<KeyEvent>> keyReleasedEvents = new HashMap<>();
	private HashMap<Node, EventHandler<MouseEvent>> mouseClickedEvents = new HashMap<>();
	private HashMap<Node, EventHandler<MouseEvent>> mouseReleasedEvents = new HashMap<>();
	private HashMap<Node, EventHandler<MouseEvent>> mouseDraggedEvents = new HashMap<>();
	private HashMap<Node, EventHandler<MouseEvent>> mousePressedEvents = new HashMap<>();
	private HashMap<MenuItem, EventHandler<ActionEvent>> actionEvents = new HashMap<>();
	private HashMap<Node, EventHandler<ScrollEvent>> scrolledEvents = new HashMap<>();
	private HashMap<Slider, ChangeListener<Number>> sliderValueChangeListeners = new HashMap<>();
	
	public void removeAllEvents(){
		for(Node node : keyPressedEvents.keySet()){
			node.removeEventHandler(KeyEvent.KEY_PRESSED, keyPressedEvents.get(node));
		}

		for(Node node : keyReleasedEvents.keySet()){
			node.removeEventHandler(KeyEvent.KEY_RELEASED, keyReleasedEvents.get(node));
		}

		for(Node node : mouseClickedEvents.keySet()){
			node.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickedEvents.get(node));
		}

		for(Node node : mouseReleasedEvents.keySet()){
			node.removeEventHandler(MouseEvent.MOUSE_RELEASED, mouseReleasedEvents.get(node));
		}

		for(Node node : mouseDraggedEvents.keySet()){
			node.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseDraggedEvents.get(node));
		}
		
		for(Node node : mousePressedEvents.keySet()){
			node.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressedEvents.get(node));
		}
		
		for(MenuItem menuItem : actionEvents.keySet()){
			menuItem.setOnAction(null);
		}
		
		for(Node node : scrolledEvents.keySet()){
			node.removeEventHandler(ScrollEvent.SCROLL, scrolledEvents.get(node));
		}
		
		for(Slider slider : sliderValueChangeListeners.keySet()){
			slider.valueProperty().removeListener(sliderValueChangeListeners.get(slider));
		}
		
		keyPressedEvents.clear();
		keyReleasedEvents.clear();
		mouseClickedEvents.clear();
		mouseReleasedEvents.clear();
		mouseDraggedEvents.clear();
		mousePressedEvents.clear();
		actionEvents.clear();
		scrolledEvents.clear();
		sliderValueChangeListeners.clear();
	}
	
	public void addKeyEvent(Node node, EventHandler<KeyEvent> eventHandler, EventType<KeyEvent> eventType){
		if(eventType == KeyEvent.KEY_PRESSED){
			keyPressedEvents.put(node, eventHandler);
		}else if(eventType == KeyEvent.KEY_RELEASED){
			keyReleasedEvents.put(node, eventHandler);
		}
		node.addEventHandler(eventType, eventHandler);
	}
	
	public void addMouseEvent(Node node, EventHandler<MouseEvent> eventHandler, EventType<MouseEvent> eventType){
		if(eventType == MouseEvent.MOUSE_CLICKED){
			mouseClickedEvents.put(node, eventHandler);
		}else if(eventType == MouseEvent.MOUSE_RELEASED){
			mouseReleasedEvents.put(node, eventHandler);
		}else if(eventType == MouseEvent.MOUSE_DRAGGED){
			mouseDraggedEvents.put(node, eventHandler);
		}else if(eventType == MouseEvent.MOUSE_PRESSED){
			mousePressedEvents.put(node, eventHandler);
		}
		
		node.addEventHandler(eventType, eventHandler);
	}
	
	public void addScrollEvent(Node node, EventHandler<ScrollEvent> eventHandler, EventType<ScrollEvent> eventType){
		if(eventType == ScrollEvent.SCROLL){
			scrolledEvents.put(node, eventHandler);
		}
		
		node.addEventHandler(eventType, eventHandler);
	}
	
	public void addActionEvent(MenuItem node, EventHandler<ActionEvent> eventHandler){
		actionEvents.put(node, eventHandler);
		
		node.setOnAction(eventHandler);
	}

	public void addSliderValueChangeListener(Slider slider, ChangeListener<Number> changeListener) {
		sliderValueChangeListeners.put(slider, changeListener);
		slider.valueProperty().addListener(changeListener);
	}
}
