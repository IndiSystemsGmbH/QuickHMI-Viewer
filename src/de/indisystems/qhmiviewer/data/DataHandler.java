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

package de.indisystems.qhmiviewer.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import de.indisystems.qhmiviewer.Main;
import de.indisystems.qhmiviewer.data.OSValidator.eOS;
import de.indisystems.qhmiviewer.data.holder.Connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataHandler {

	public static String programDataDir;

	private ArrayList<Connection> connections = new ArrayList<Connection>();

	private File fileConnections;

	public DataHandler() {
		try {
			programDataDir = getAppDataDir();
			initConnections();
		} catch (Exception e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}

	public static String getAppDataDir() {
		if(programDataDir == null) {
			if (OSValidator.getOS() == eOS.Windows) {			
				programDataDir = System.getenv("SystemDrive") + "\\ProgramData\\Indi.Systems\\QuickHMI Viewer v" + Main.currentVersion.getMajor();
			} else if (OSValidator.getOS() == eOS.Unix_LINUX) {
				programDataDir = "/usr/share/Indi.Systems/QuickHMI Viewer v" + Main.currentVersion.getMajor();
			} else if (OSValidator.getOS() == eOS.MacOS) {
				programDataDir = System.getProperty("user.home") + "/Library/Application Support/Indi.Systems/QuickHMI Viewer v" + Main.currentVersion.getMajor();
			}
			
			File file = new File(programDataDir);
			if(!file.exists()) {
				file.mkdirs();
				
				if(file.exists()) {
					if(OSValidator.getOS() == eOS.Unix_LINUX) {
						String bashCommand = "sudo chmod -R 777 " + file.getAbsolutePath();
		
						try {
							Runtime.getRuntime().exec(bashCommand);
						} catch (IOException e) {
							LogManager.getLogger().error("Can't change mode of directory '" + file.getAbsolutePath() + "'.", e);
						}
					}
				} else {
					LogManager.getLogger().warn("Couldn't create programdata directory: '" + file.getAbsolutePath() + "'.");
				}
			}
			
			return programDataDir;
		} else {
			return programDataDir;
		}
	}

	private void initConnections() {
		try {
			if (OSValidator.getOS() == eOS.Windows) {
				fileConnections = new File(programDataDir + "\\config\\recent_connections.txt");
			} else {
				fileConnections = new File(programDataDir + "/config/recent_connections.txt");
			}

			if (!fileConnections.exists()) {
				fileConnections.getParentFile().mkdirs();
				fileConnections.createNewFile();
			}
			
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(fileConnections));
			boolean fileCorrupted = false;
			boolean updateFile = false;

			try {
				while ((line = reader.readLine()) != null) {
					String[] conArray = line.split(";");
					if(conArray.length == 5){
						//SSL Attribut fehlt
						//Default: True
						
						int port = 0;
						try {
							port = Integer.parseInt(conArray[3]);
						} catch (Exception ex) {
							port = 6062;
						}

						connections.add(new Connection(conArray[1], conArray[2], port, Long.parseLong(conArray[4]),
								Long.parseLong(conArray[0]), true));
						
						updateFile = true;
					}else{
						int port = 0;
						try {
							port = Integer.parseInt(conArray[3]);
						} catch (Exception ex) {
							port = 6062;
						}

						connections.add(new Connection(conArray[1], conArray[2], port, Long.parseLong(conArray[5]),
								Long.parseLong(conArray[0]), Boolean.parseBoolean(conArray[4])));
					}
				}
			} catch (Exception ex) {
				fileCorrupted = true;
			} finally {
				reader.close();
			}

			if (fileCorrupted)
				fileConnections.delete();
			else if(updateFile){
				fileConnections.delete();
				fileConnections.createNewFile();
				
				ArrayList<Connection> tmpConnections = new ArrayList<>(connections);
				connections.clear();
				
				for(Connection connection : tmpConnections){
					addConnection(connection);
				}
			}

			Collections.sort(connections, new Comparator<Connection>() {
				@Override
				public int compare(Connection o1, Connection o2) {
					return (int) (o1.getLastConnected() - o2.getLastConnected());
				}
			});

		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		}
	}

	/*
	 * RECENT_CONNECTIONS METHODS
	 */
	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public ObservableList<Connection> getRecentConnections() {
		List<Connection> tmpList = connections;
		Collections.sort(tmpList, new ConnectionComparator());
		return FXCollections.observableArrayList(tmpList);
	}

	private class ConnectionComparator implements Comparator<Connection> {

		@Override
		public int compare(Connection arg0, Connection arg1) {
			if (arg1.getLastConnected() - arg0.getLastConnected() < 0)
				return -1;
			else if (arg1.getLastConnected() - arg0.getLastConnected() > 0)
				return 1;
			else
				return 0;
		}

	}

	public Connection getConnection(long id) {
		for (Connection conn : connections) {
			if (conn.getId() == id) {
				return conn;
			}
		}
		return null;
	}

	public Connection getConnection(String name) {
		for (Connection conn : connections) {
			if (conn.getName().equals(name)) {
				return conn;
			}
		}
		return null;
	}

	public void updateLastConnected(Connection connection) {
		connection.setLastConnected(System.currentTimeMillis());

		String lineSeparator = System.getProperty("line.separator");
		StringBuilder tempBuilder = new StringBuilder();

		BufferedReader reader = null;
		FileWriter writer = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(fileConnections));

			while ((line = reader.readLine()) != null) {
				String[] conArray = line.split(";");
				if (Long.parseLong(conArray[0]) == connection.getId()) {
					conArray[5] = "" + System.currentTimeMillis();

					for (int i = 0; i < conArray.length; i++) {
						if (i != conArray.length - 1)
							tempBuilder.append(conArray[i] + ";");
						else
							tempBuilder.append(conArray[i]);
					}
					tempBuilder.append(lineSeparator);
				} else {
					tempBuilder.append(line + lineSeparator);
				}
			}

			writer = new FileWriter(fileConnections);
			writer.write(tempBuilder.toString());
		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		} finally {
			try {
				writer.flush();
				writer.close();
				reader.close();
			} catch (IOException e) {
				LogManager.getLogger().error(e.getLocalizedMessage(), e);
			}			
		}
	}

	public void addConnection(Connection connection) {
		addConnection(connection.getName(), connection.getHost(), connection.getPort(), connection.isSsl());
	}

	private long getNewConnectionId() {
		long newId = 0;

		for (Connection conn : connections) {
			if (conn.getId() > newId) {
				newId = conn.getId();
			}
		}

		return newId + 1;
	}

	public Connection addConnection(String name, String host, int port, boolean isSsl) {
		long connId = isConnection(name, host, port);
		if (connId == -1) {
			String lineSeparator = System.getProperty("line.separator");

			Connection newConnection = new Connection(name, host, port, System.currentTimeMillis(),
					getNewConnectionId(), isSsl);

			if (!Main.paramLocalhost) {
				connections.add(newConnection);

				FileWriter writer = null;
				try {
					writer = new FileWriter(fileConnections, true);
					writer.append(newConnection.getId() + ";" + newConnection.getName() + ";" + newConnection.getHost()
							+ ";" + newConnection.getPort() + ";" + newConnection.isSsl() + ";" + System.currentTimeMillis() + lineSeparator);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						writer.flush();
						writer.close();
					} catch (IOException e) {
						LogManager.getLogger().error(e.getLocalizedMessage(), e);
					}
				}
			}

			return newConnection;
		} else {
			Connection connection = getConnection(connId);
			updateLastConnected(connection);
			return connection;
		}
	}

	public void editConnection(String name, String host, int port, long id, boolean isSsl) {
		Connection connection = getConnection(id);
		connection.setName(name);
		connection.setHost(host);
		connection.setPort(port);
		connection.setSsl(isSsl);

		String lineSeparator = System.getProperty("line.separator");
		StringBuilder tempBuilder = new StringBuilder();

		BufferedReader reader = null;
		FileWriter writer = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(fileConnections));

			while ((line = reader.readLine()) != null) {
				String[] conArray = line.split(";");
				if (Long.parseLong(conArray[0]) == connection.getId()) {
					conArray[1] = name;
					conArray[2] = host;
					conArray[3] = port + "";
					conArray[4] = isSsl + "";

					for (int i = 0; i < conArray.length; i++) {
						if (i != conArray.length - 1)
							tempBuilder.append(conArray[i] + ";");
						else
							tempBuilder.append(conArray[i]);
					}
					tempBuilder.append(lineSeparator);
				} else {
					tempBuilder.append(line + lineSeparator);
				}
			}

			writer = new FileWriter(fileConnections);
			writer.write(tempBuilder.toString());
		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		} finally {
			try {
				writer.flush();
				writer.close();
				reader.close();
			} catch (IOException e) {
				LogManager.getLogger().error(e.getLocalizedMessage(), e);
			}
		}
	}

	public void deleteConnection(long id) {
		for (Connection conn : connections) {
			if (conn.getId() == id) {
				connections.remove(conn);
				break;
			}
		}

		String lineSeparator = System.getProperty("line.separator");
		StringBuilder tempBuilder = new StringBuilder();

		BufferedReader reader = null;
		FileWriter writer = null;
		try {
			String line;
			reader = new BufferedReader(new FileReader(fileConnections));

			while ((line = reader.readLine()) != null) {
				String[] conArray = line.split(";");
				if (!(Long.parseLong(conArray[0]) == id)) {
					tempBuilder.append(line + lineSeparator);
				}
			}

			writer = new FileWriter(fileConnections);
			writer.write(tempBuilder.toString());
		} catch (IOException e) {
			LogManager.getLogger().error(e.getLocalizedMessage(), e);
		} finally {
			try {
				writer.flush();
				writer.close();
				reader.close();
			} catch (IOException e) {
				LogManager.getLogger().error(e.getLocalizedMessage(), e);
			}
		}
	}

	/**
	 * Checks if given connection is already in the database.
	 * 
	 * @param db
	 * @param name
	 * @param host
	 * @param port
	 * @param project
	 * @return ID of given connection, or -1 if the connection is not in the
	 *         database yet.
	 */
	public long isConnection(String name, String host, int port) {
		for (Connection conn : connections) {
			if (conn.getName().equals(name) && conn.getHost().equals(host) && conn.getPort() == port) {
				return conn.getId();
			}
		}
		return -1;
	}
}