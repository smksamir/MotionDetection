package com.MotionDetect;


import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class MotionDetection {
	
	public static void main(String args[]) {
		//load library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//System.load("/usr/local/Cellar/opencv/3.3.1_1/share/OpenCV/java/libopencv_java331.dylib");
		
		Mat frame = new Mat();
		Mat firstFrame = new Mat();
		Mat gray = new Mat();
		Mat frameDelta = new Mat();
		Mat thresh = new Mat();
		List<MatOfPoint> cnts = new ArrayList<MatOfPoint>();
		
		VideoCapture camera = new VideoCapture();
		camera.open(0); //open camera
		
		//set the video size to 512x288
		camera.set(3, 512);
		camera.set(4, 288);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		camera.read(frame);
		//convert to grayscale and set the first frame
		Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);
		
		while(camera.read(frame)) {
			//convert to grayscale
			Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);
			Imgproc.GaussianBlur(gray, gray, new Size(21, 21), 0);
			
			//compute difference between first frame and current frame
			Core.absdiff(firstFrame, gray, frameDelta);
			Imgproc.threshold(frameDelta, thresh, 25, 255, Imgproc.THRESH_BINARY);
			
			Imgproc.dilate(thresh, thresh, new Mat(), new Point(-1, -1), 2);
			Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
			
			for(int i=0; i < cnts.size(); i++) {
				if(Imgproc.contourArea(cnts.get(i)) < 500) {
					continue;
				}
				
				System.out.println("Motion detected!!!");
				System.exit(0);
			}
		}		
	}
}