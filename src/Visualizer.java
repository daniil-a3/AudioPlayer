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
	private int renderedWidth, renderedHeight, renderedFrame;
	private Long position;
	private int channels;
	private int LINES_USED=1; // TODO MAKE TOGGLABLE
	
	public Visualizer()
	throws UnsupportedAudioFileException, IOException {
		/*clipToByteArray();
		byteToIntArray();*/
	}
	
	public void paintComponent(Graphics g) {
		renderedWidth=AudioPlayer.panelWidth-20;
		renderedHeight=AudioPlayer.panelHeight-130;
		try {
			renderedFrame=(int) clip.getFormat().getSampleRate()
					/(1000/AudioPlayer.updateFrequencyMs);
		} catch (Exception e) {
			System.err.println(e);
		}
		
		g.setColor(Color.black);
		g.fillRect(0, 0, renderedWidth, renderedHeight);
		if (AudioPlayer.visStyle=="Horizontal"
			|| AudioPlayer.visStyle=="Oscilloscope") {
			if (AudioPlayer.visStyle=="Oscilloscope" && channels==2) {
				g.setColor(Color.green);
				try {
					position=clip.getLongFramePosition();
					//System.out.println(position);
					int renderedSize, renderedOffset;
					int isOffsetX;
					for (int i=position.intValue()*2; i<(position.intValue()+renderedFrame)*2; i+=2) {
						//System.out.println((i-position.intValue())/channels);
						// TODO with lines looks too bright and spiky...
						if (renderedWidth<renderedHeight) {
							renderedSize=renderedWidth;
							renderedOffset=(renderedHeight-renderedWidth)/2;
							isOffsetX=0;
						} else {
							renderedSize=renderedHeight;
							renderedOffset=(renderedWidth-renderedHeight)/2;
							isOffsetX=1;
						}
						if (LINES_USED==1) {
							try {
								g.setColor(Color.getHSBColor(1.0f/3, 1.0f, 
										1/(((float) (Math.abs(
											(((int) (((float) audioData[i+2]/65536)*renderedSize)
										+(renderedSize/2))%renderedSize)-
										(((int) (((float) audioData[i]/65536)*renderedSize)
										+(renderedSize/2))%renderedSize)
												))+
											(float) (Math.abs(
												(((int) (((float) audioData[i+3]/65536)*renderedSize)
											+(renderedSize/2))%renderedSize)-
											(((int) (((float) audioData[i+1]/65536)*renderedSize)
											+(renderedSize/2))%renderedSize)
													)))/4)));
							} catch (ArithmeticException e) {
								g.setColor(Color.getHSBColor(1.0f/3, 0.0f, 1.0f));
							}
						}
						g.drawLine((((int) (((float) audioData[i]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*(1-isOffsetX),
								(((int) (((float) audioData[i+LINES_USED*2]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1+LINES_USED*2]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*(1-isOffsetX));
						if (LINES_USED==1) {
							g.drawLine((((int) (((float) audioData[i]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*(1-isOffsetX),
								(((int) (((float) audioData[i]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1]/65536)*renderedSize)
								+(renderedSize/2))%renderedSize)+renderedOffset*(1-isOffsetX));
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println(e);
				} catch (Exception e) {
					g.setColor(Color.green);
					g.drawLine(renderedWidth/2, renderedHeight/2, renderedWidth/2, renderedHeight/2);
					System.err.println(e);
				}
			} else {
				try {
					position=clip.getLongFramePosition();
					//System.out.println(position);
					
					//g.drawLine(0, 0, renderedWidth, renderedHeight); // display size test
					for (int c=1; c<channels+1; c++) {
						if (channels==1) {
							g.setColor(Color.green);
						} else {
							try {
								g.setColor(Color.getHSBColor((float) (c-1)/channels/(c-1), 1.0f, 1.0f));
							} catch (ArithmeticException e) {
								g.setColor(Color.red);
							}
						}
						for (int i=position.intValue()*channels+c-1; i<(position.intValue()+renderedWidth+c-1)*channels; i+=channels) {
							//System.out.println((i-position.intValue())/channels);
							g.drawLine(i/channels-position.intValue(), renderedHeight-
									(((int) (((float) audioData[i]/65536)*renderedHeight)
									+(renderedHeight/2))%renderedHeight),
								i/channels-position.intValue()+LINES_USED, renderedHeight-
									(((int) (((float) audioData[i+LINES_USED*channels]/65536)*renderedHeight)
									+(renderedHeight/2))%renderedHeight));
						}
					}
					//System.out.println(audioData);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println(e);
				} catch (Exception e) {
					g.setColor(Color.green);
					g.drawLine(0, renderedHeight/2, renderedWidth, renderedHeight/2);
					System.err.println(e);
				}
			}
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
			System.out.println(bufferSize);
			//System.out.println(byteArray);
			/*for (int i=0; i<bufferSize; i++) {
				System.out.println(byteArray[i]);
			}*/
		} catch (NullPointerException e) {
			System.err.println(e);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void byteToIntArray() {
		try {
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
			channels=clip.getFormat().getChannels();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}