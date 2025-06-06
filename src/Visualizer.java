import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JComponent;

public class Visualizer extends JComponent {
	private int[] audioData;
	private static byte[] byteArray;
	private static Clip clip;
	
	public Visualizer()
	throws UnsupportedAudioFileException, IOException {
		clipToByteArray();
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, AudioPlayer.panelWidth, AudioPlayer.panelHeight);
		try {
			/*int frameSize=clip.getFormat().getFrameSize();
			System.out.println(frameSize);
			int frameLength=clip.getFrameLength();
			System.out.println(frameLength);*/
			
			int sampleSizeInBits=clip.getFormat().getSampleSizeInBits();
			boolean isBigEndian=clip.getFormat().isBigEndian();
			int numSamples=byteArray.length/(sampleSizeInBits/8);
			audioData=new int[numSamples];
			for (int i=0; i<numSamples; i++) {
				int sample=0;
				for (int j=0; j<sampleSizeInBits/8; j++) {
					int byteIndex=i*(sampleSizeInBits/8)+(isBigEndian?(sampleSizeInBits/8-1-j):j);
					sample|=(byteArray[byteIndex]&0xFF)<<(j*8);
				}
				audioData[i]=sample;
			}
			
			g.setColor(Color.green);
			//g.drawLine(0, 0, AudioPlayer.panelWidth, AudioPlayer.panelHeight);
			for (int i=0; i<AudioPlayer.panelWidth; i++) {
				g.drawLine(i, (audioData[i]/65536)*AudioPlayer.panelHeight, i, (audioData[i]/65536)*AudioPlayer.panelHeight);
			}
			System.out.println(audioData);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public static void clipToByteArray()
	throws UnsupportedAudioFileException, IOException {
		try {
			clip=AudioPlayer.clip;
			AudioInputStream audioInputStream
				=AudioSystem.getAudioInputStream(AudioPlayer.currentFile);
			AudioFormat audioFormat=clip.getFormat();
			int frameSize=audioFormat.getFrameSize();
			int frameLength=clip.getFrameLength();
			int bufferSize=frameLength*frameSize;
			byteArray=new byte[bufferSize];
			
			audioInputStream.read(byteArray);
			System.out.println(byteArray);
		} catch (NullPointerException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}