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

package de.indisystems.qhmiviewer.data;

public class OSValidator {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public enum eOS {
          not_Supported, Windows, MacOS, Unix_LINUX, Solaris
    }

    public static eOS getOS() {
          if (OS.indexOf("win") >= 0)
                 return eOS.Windows;
          else if (OS.indexOf("mac") >= 0)
                 return eOS.MacOS;
          else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0)
                 return eOS.Unix_LINUX;
          else if (OS.indexOf("sunos") >= 0)
                 return eOS.Solaris;
          else
                 return eOS.not_Supported;
    }
}
