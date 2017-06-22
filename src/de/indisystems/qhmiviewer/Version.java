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

package de.indisystems.qhmiviewer;

public class Version{
	public static final int NEWER = 1;
	public static final int EQUAL = 0;
	public static final int OLDER = -1;

	public static final int BONOBO = 4;
	public static final int CHEETAH = 5;
	
	private Integer major;
	private Integer minor;
	private Integer patch;

	public Version(int[] version){
		this.major = version[0];
		this.minor = version[1];
		this.patch = version[2];
	}
	
	public Version(int major, int minor, int patch){
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	public Version(String version){
		String[] arrVersion = version.split("\\.");
		if(arrVersion.length == 1){
			this.major = Integer.parseInt(arrVersion[0]);
		}else if(arrVersion.length == 2){
			this.major = Integer.parseInt(arrVersion[0]);
			this.minor = Integer.parseInt(arrVersion[1]);				
		}else if(arrVersion.length == 3){
			this.major = Integer.parseInt(arrVersion[0]);
			this.minor = Integer.parseInt(arrVersion[1]);
			this.patch = Integer.parseInt(arrVersion[2]);				
		}
	}

	public Integer getMajor() {
		return major;
	}

	public Integer getMinor() {
		return minor;
	}

	public Integer getPatch() {
		return patch;
	}
	
	public int compare(Version version){
		if(version.getMajor() > this.major){
			return NEWER;
		}else if(version.getMajor() <  this.major){
			return OLDER;
		}else{
			if(version.getMinor() > this.minor){
				return NEWER;
			}else if(version.getMinor() < this.minor){
				return OLDER;
			}else{
				if(version.getPatch() > this.patch){
					return NEWER;
				}else if(version.getPatch() < this.patch){
					return OLDER;
				}else{
					return EQUAL;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return this.major+"."+this.minor+"."+this.patch;
	}
}