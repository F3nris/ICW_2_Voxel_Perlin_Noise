package de.htw.mtm.icw2.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ShaderReader {
	public static String readShaderFromFile(String path) {
		String shadercode = "";

		InputStream in;
		try {
			in = new FileInputStream(path);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 
	        
	        String line;
	        while ((line = reader.readLine()) != null) {
	            shadercode += line + "\n";
	        }
	        
	        reader.close();
	        in.close();
		} catch (FileNotFoundException e) {
			System.err.println("The requested file for the shader could not be opened. Please double check the path: "+ path);
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("A critical error occured when trying to perform an IO operation related to the loading of shaders.");
			e.printStackTrace();
		}
        
		return shadercode;
	}
}
