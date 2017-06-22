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

package de.indisystems.qhmiviewer.data.holder;

public class Connection {
	private String name;
	private String host;
	private int port;
	private long lastConnectedMillis;
	private long id;
	private boolean ssl;
	
	public Connection(String name, String host, int port, long lastConnected, long id, boolean ssl){
		this.name = name;
		this.host = host;
		this.port = port;
		this.lastConnectedMillis = lastConnected;
		this.id = id;
		this.ssl = ssl;
	}
	
	public int getPort(){
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}

	public long getLastConnected() {
		return lastConnectedMillis;
	}

	public void setLastConnected(long lastConnected) {
		this.lastConnectedMillis = lastConnected;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	public String getUrl(){
		String url = "http";
		
		if(ssl)
			url += "s";
		
		url += "://" + host + ":" + port;
		
		return url;
	}
}
