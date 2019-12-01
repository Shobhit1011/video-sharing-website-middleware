package com.dell.flat.hyd.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GenerateManifestFiles {
	public void generateManifestFile(String filename){
		String[] values = filename.split("[.]");
		String file = values[0];
		String extension = values[1];
		String batchFilePath = "E:\\video_coversion.bat";
		String line;
		
		try {
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", batchFilePath, file, extension}); 
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getErrorStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
