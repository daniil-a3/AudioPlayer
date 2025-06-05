import java.awt.Color;
import java.awt.Graphics;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.swing.JComponent;

public class Visualizer extends JComponent {
	private Color color;
	private int[] audioData;
	private static Clip clip;
	
	public Visualizer() {
		color=Color.blue;
	}
	
	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, AudioPlayer.panelWidth, AudioPlayer.panelHeight);
		try {
			clip=AudioPlayer.clip;
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
			System.out.println(audioData);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public static byte[] clipToByteArray() /*throws IOException*/ {
		AudioInputStream audioInputStream=clip.getAudioInputStream();
		AudioFormat audioFormat=clip.getFormat();
		int frameSize=audioFormat.getFrameSize();
		int frameLength=clip.getFrameLength();
		int bufferSize=frameLength * frameSize;
		byte[] byteArray=new byte[bufferSize];
		
		audioInputStream.read(byteArray);
		return byteArray;
	}
}