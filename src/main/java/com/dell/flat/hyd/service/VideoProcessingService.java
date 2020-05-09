package com.dell.flat.hyd.service;

import java.io.IOException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.dell.flat.hyd.model.GenerateManifestFiles;
import com.dell.flat.hyd.model.Video;
import com.dell.flat.hyd.model.VideoDao;

@Service("videoProcessing")
public class VideoProcessingService {
	@Async("threadPoolTaskExecutor")
	public void videoProcessing(GenerateManifestFiles generateManifestFiles, Video video, VideoDao videoDao) throws IOException, InterruptedException {
		generateManifestFiles.generateManifestFile(video.getName_in_folder());
		video.setStatus("Done");
		videoDao.updateVideoById(video);
	}
}
