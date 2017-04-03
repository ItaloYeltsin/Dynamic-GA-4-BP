package jmetal.util.dga4nrp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InstanceReader extends InstanceManager{

	
	public InstanceReader(String path) {
		super(path);
	}
	
	public Instance load() {
		try {
			Instance instance = new Instance(
					new ArrayList<Bug>(), new HashMap<Bug, ArrayList<Integer>>());
			
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.readLine();
			while (br.ready()) {
				String line = br.readLine();
				String [] bugParams = line.split(";");
				Bug b = new Bug();
				b.setId(Integer.parseInt(bugParams[0]));
				b.setVotes(Integer.parseInt(bugParams[1]));
				b.setPriority(Double.parseDouble(bugParams[2]));
				b.setSerity(Double.parseDouble(bugParams[3]));
				instance.getBugs().add(b);
				
				
				if (bugParams.length > 4) {
					String [] precedences = bugParams[4].split(",");
					
					ArrayList<Integer> prec  = new ArrayList<Integer>();
					instance.getPrecedences().put(b, prec);
					for (int i = 0; precedences != null && i < precedences.length; i++) {
						prec.add(Integer.parseInt(precedences[i]));
					}
				}
				
				
			}
			
			return instance;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
