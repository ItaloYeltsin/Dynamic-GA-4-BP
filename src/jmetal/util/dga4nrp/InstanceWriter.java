package jmetal.util.dga4nrp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class InstanceWriter extends InstanceManager{

	public InstanceWriter(String path) {
		super(path);
	}
	
	public void write(Instance instance) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("id;votes;priority;severity;precedence");
			bw.newLine();
			for (Bug bug : instance.getBugs()) {
				bw.write(bug.id+";"+bug.votes+";"+bug.priority+";"+bug.serity+";");
				
				ArrayList<Integer> prec = (ArrayList<Integer>)instance.getPrecedences().get(bug);
				
				if(prec != null) {
					for (Integer integer : prec) {
						bw.write(integer+",");
					}
				}
					
				bw.newLine();
			}
			
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
