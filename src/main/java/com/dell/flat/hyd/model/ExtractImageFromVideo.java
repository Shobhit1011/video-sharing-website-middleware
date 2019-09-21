package com.dell.flat.hyd.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExtractImageFromVideo {
	public void extractImage(String videoName) {
		final Logger LOG = Logger.getLogger(ExtractImageFromVideo.class.getName());
		try {
			String line;
			String videoNameWithoutExtension = videoName.replaceFirst("[.][^.]+$", "");
			String videoFile = "E:\\tmpFiles\\"+videoName;
			String imageFile = "E:\\imageFiles\\"+videoNameWithoutExtension+".png";
			
			// ffmpeg -i New_rules.mp4 -ss 00:00:01.000 -vframes 1 output.png
            
			String cmd = "ffmpeg -i " + videoFile +" -ss 00:00:01.000 -vframes 1 " +imageFile;
			System.out.println(cmd);
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(p.getErrorStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            p.waitFor();
            System.out.println("Video converted successfully!");
            in.close();
		}
		catch(Exception ee) {
			LOG.log(Level.SEVERE, null, ee);
		}
	}
}
