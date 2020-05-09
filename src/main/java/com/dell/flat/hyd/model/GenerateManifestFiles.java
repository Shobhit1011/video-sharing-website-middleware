package com.dell.flat.hyd.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GenerateManifestFiles {
	public void generateManifestFile(String filename) throws IOException, InterruptedException{
		String[] values = filename.split("[.]");
		String file = values[0];
		String extension = values[1];
		String batchFilePath = "E:\\video_coversion.bat";
		String line;
		System.out.print("Extension "+extension+" filename "+filename);
		if(!extension.equals("mp4")) {
			Process p1 = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "ffmpeg -i E:\\tmpFiles\\"+filename+" E:\\tmpFiles\\"+file+".mp4"});
			BufferedReader in1 = new BufferedReader(
                    new InputStreamReader(p1.getErrorStream()));
            while ((line = in1.readLine()) != null) {
                System.out.println(line);
            }
			p1.waitFor();
		}
		try {
            Process p = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", batchFilePath, file, "mp4"}); 
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
