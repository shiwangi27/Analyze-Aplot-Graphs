/***********************************************************************
 * Version History
 * 
 * Version_No		Date			Author  	Reason for Modification
 * 1.0			15-Nov-2015     	Pavan		Initial Version
 * 1.1			26-Nov-2015		Sandeep		Changing the way in which the methods are retireved.
 * 1.2			26-Nov-2015		Pavan		Removed one extra hardcoded filepaths and removed space in starting of each line in output
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class Find_Input_Specific_Methods 
{
  public static void main(String[] args) throws Exception
  {
  	String file_path = "//home//pavan//aprof-outputs";
	File base_folder = new File(file_path);
	File[] input_folders = base_folder.listFiles();
	String line;
	String method;
	String curr_folder;
	LinkedHashMap<String, HashSet<String>> input_methods_mapping = new LinkedHashMap<>();
	HashSet<String> temp = new HashSet<>();
	// opening each folder in base folder
	for (File folder : input_folders) 
	{
		// opening each file in current folder 
		File[] aprof_output = folder.listFiles();
		curr_folder = folder.toString();
		
		//We are removing the path before adding it to the LinkedHashMap, so that the output looks
		//good.
		
		curr_folder = curr_folder.replace(file_path, "");
		temp= new HashSet<>();
		input_methods_mapping.put(curr_folder, temp);
		for (File aprof_file : aprof_output) 
		{
			// adding all the methods in current file to a arraylist
			BufferedReader br = new BufferedReader(new FileReader(aprof_file));
			while ((line = br.readLine()) != null) 
			{
				if(line.charAt(0)=='r')
				{
					//Only take methods that are in our code base, eliminating the ones obtained
					//from third party.
					if(line.contains("linuxdcpp/linuxdcpp") && !line.contains("<"))
					{
						method = line.split("\"")[1];
						
						//Splitting the argumnets and taking only the method names.
						method = method.split("\\(")[0];
						method = method.replace("dcpp::","");
						//method = method.substring(1, method.length()-1);
						if(!method.isEmpty()){
							input_methods_mapping.get(curr_folder).add(method);
							//System.out.println(method);
						}
					}
					
				}
			}
			br.close();
		}
	}
	
	// finding specific methods for a given input
	PrintWriter writer = new PrintWriter("input_specific_methods.txt");
	HashSet<String> methods = new HashSet<>();
	boolean isUniqueMethod = true;
	// looping over each folder
	for (String input1 : input_methods_mapping.keySet()) 
	{
		// looping over each method in current folder say folder1
		for (String method1 : input_methods_mapping.get(input1)) 
		{
			isUniqueMethod = true;	
			//looping over each of other folder(other than current one) say folder2
			for (String input2 : input_methods_mapping.keySet()) 
			{
				if(input2.equals(input1))
					continue;
				// taking all the methods in folder2 in a hasset
				methods = input_methods_mapping.get(input2);
				// if method1 of folder1 is also called in folder2, 
				//it means it is not specific to input1
				if(methods.contains(method1))
				{ 
					isUniqueMethod = false;
					break;
				}
			}
			
			if(isUniqueMethod)
			{
				writer.println(input1 + " " + method1);
			}
		}
	}
	writer.close();
	System.out.println("done");
  }
}

