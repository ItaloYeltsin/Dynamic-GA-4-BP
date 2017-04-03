package jmetal.util.dga4nrp;

import java.io.File;

public abstract class InstanceManager {
	protected String path = null;
	File file;
	public InstanceManager(String path) {
		this.path = path;
		file = new File(path);
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	
}
