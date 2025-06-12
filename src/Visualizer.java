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
	private int useLines=1;
	private int bitDepth;
	private int bitSign;
	
	public Visualizer()
	throws UnsupportedAudioFileException, IOException {
		/*clipToByteArray();
		byteToIntArray();*/
	}
	
	public void paintComponent(Graphics g) {
		renderedWidth=AudioPlayer.panelWidth-20;
		renderedHeight=AudioPlayer.panelHeight-130;
		int bitAmplitude=(int) Math.pow(2, bitDepth);
		try {
			renderedFrame=(int) clip.getFormat().getSampleRate()
					/(1000/AudioPlayer.updateFrequencyMs);
		} catch (Exception e) {
			System.err.println("Exception "+e+" occured calculating renderedFrame");
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
						if (renderedWidth<renderedHeight) {
							renderedSize=renderedWidth;
							renderedOffset=(renderedHeight-renderedWidth)/2;
							isOffsetX=0;
						} else {
							renderedSize=renderedHeight;
							renderedOffset=(renderedWidth-renderedHeight)/2;
							isOffsetX=1;
						}
						if (useLines==1) {
							try {
								g.setColor(Color.getHSBColor(1.0f/3, 1.0f, 
										1/(((float) (Math.abs(
											(((int) (((float) audioData[i+2]/bitAmplitude)*renderedSize)
										+(renderedSize/2*bitSign))%renderedSize)-
										(((int) (((float) audioData[i]/bitAmplitude)*renderedSize)
										+(renderedSize/2*bitSign))%renderedSize)
												))+
											(float) (Math.abs(
												(((int) (((float) audioData[i+3]/bitAmplitude)*renderedSize)
											+(renderedSize/2*bitSign))%renderedSize)-
											(((int) (((float) audioData[i+1]/bitAmplitude)*renderedSize)
											+(renderedSize/2*bitSign))%renderedSize)
													)))/4)));
							} catch (ArithmeticException e) {
								g.setColor(Color.getHSBColor(1.0f/3, 0.0f, 1.0f));
							}
						}
						g.drawLine((((int) (((float) audioData[i]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*(1-isOffsetX),
								(((int) (((float) audioData[i+useLines*2]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1+useLines*2]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*(1-isOffsetX));
						if (useLines==1) {
							g.drawLine((((int) (((float) audioData[i]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*(1-isOffsetX),
								(((int) (((float) audioData[i]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*isOffsetX,
								renderedSize-
								(((int) (((float) audioData[i+1]/bitAmplitude)*renderedSize)
								+(renderedSize/2*bitSign))%renderedSize)+renderedOffset*(1-isOffsetX));
						}
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println(e);
				} catch (Exception e) {
					g.setColor(Color.green);
					g.drawLine(renderedWidth/2, renderedHeight/2, renderedWidth/2, renderedHeight/2);
					System.err.println("Exception "+e+" occured while drawing 2D oscilloscope");
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
								System.err.println(e);
								g.setColor(Color.red);
							}
						}
						for (int i=position.intValue()*channels+c-1; i<(position.intValue()+renderedWidth+c-1)*channels; i+=channels) {
							//System.out.println((i-position.intValue())/channels);
							if (useLines==1) {
								if (channels==1) {
									g.setColor(Color.getHSBColor(1.0f/3, 1.0f,
											1/((float) Math.abs(
												(renderedHeight-
												(((int) (((float) audioData[i]/bitAmplitude)*renderedHeight)
												+(renderedHeight/2*bitSign))%renderedHeight))
													-(renderedHeight-
												(((int) (((float) audioData[i+channels]/bitAmplitude)*renderedHeight)
												+(renderedHeight/2*bitSign))%renderedHeight))
											)/8+1)));
								} else {
									g.setColor(Color.getHSBColor((float) (c-1)/channels/(c-1), 1.0f,
										1/((float) Math.abs(
											(renderedHeight-
											(((int) (((float) audioData[i]/bitAmplitude)*renderedHeight)
											+(renderedHeight/2*bitSign))%renderedHeight))
												-(renderedHeight-
											(((int) (((float) audioData[i+channels]/bitAmplitude)*renderedHeight)
											+(renderedHeight/2*bitSign))%renderedHeight))
										)/8+1)));
								}
							}
							g.drawLine(i/channels-position.intValue(), renderedHeight-
									(((int) (((float) audioData[i]/bitAmplitude)*renderedHeight)
									+(renderedHeight/2*bitSign))%renderedHeight),
								i/channels-position.intValue()+useLines, renderedHeight-
									(((int) (((float) audioData[i+useLines*channels]/bitAmplitude)*renderedHeight)
									+(renderedHeight/2*bitSign))%renderedHeight));
						}
					}
					//System.out.println(audioData);
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.println(e);
				} catch (Exception e) {
					g.setColor(Color.green);
					g.drawLine(0, renderedHeight/2, renderedWidth, renderedHeight/2);
					System.err.println("Exception "+e+" occured while drawing horizontal oscilloscope");
				}
			}
		} else if (AudioPlayer.visStyle=="Bars") {
			try {
				//for (int i=0; i<renderedWidth; i++) {
					/*double frequency=Math.pow(2, (double) i/renderedFrame*clip.getFormat().getSampleRate()/2000);
					double distance=(clip.getFormat().getSampleRate()/frequency);
					System.out.println(distance);
					if (distance<=2) {
						break;
					}*/
				//}
				position=clip.getLongFramePosition();
				g.setColor(Color.red);
				int[] bars=normalize(computeFFT(audioData, position*channels, (position+renderedFrame)*channels), 0, renderedHeight);
				for (int i=0; i<bars.length; i++) {
					//System.out.print(bars[i]);
					//System.out.print(", ");
					g.drawLine(i, (int) (renderedHeight-((float) Math.log10(bars[i]+1)*renderedHeight/Math.log10(renderedHeight))), i, renderedHeight);
				}
				System.out.print("\n");
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println(e);
			} catch (Exception e) {
				g.setColor(Color.green);
				g.drawLine(0, renderedHeight/2, renderedWidth, renderedHeight/2);
				System.err.println("Exception "+e+" occured while drawing Fourier bars");
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
		} catch (OutOfMemoryError e) { //occured to me only once, CNR since so I can't test if this error checker has bugs
			AudioPlayer.showErrorMessage("Ran out of memory trying to load audio file",
					"File Size Error");
			byteArray=new byte[0];
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void byteToIntArray() {
		try {
			bitDepth=clip.getFormat().getSampleSizeInBits();
			if (clip.getFormat().getEncoding().toString()=="PCM_SIGNED") {
				bitSign=1;
			} else {
				bitSign=0;
			}
			boolean isBigEndian=clip.getFormat().isBigEndian();
			int numSamples=byteArray.length/(bitDepth/8);
			audioData=new int[numSamples];
			for (int i=0; i<numSamples; i++) {
				int sample=0;
				for (int j=0; j<bitDepth/8; j++) {
					int byteIndex=i*(bitDepth/8)+(isBigEndian?(bitDepth/8-1-j):j);
					sample|=(byteArray[byteIndex]&0xFF)<<(j*8);
				}
				audioData[i]=sample;
			}
			channels=clip.getFormat().getChannels();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	public void toggleLines() {
		if (useLines==1) {
			useLines=0;
		} else {
			useLines=1;
		}
	}
	
	public static int[] computeFFT(int[] inputArray, long start, long end) {
		if (start<0 || end>inputArray.length || start>=end) {
			throw new IllegalArgumentException("Invalid range specified.");
		}
		
		int n=(int) (end-start);
		double[] real=new double[n];
		double[] imag=new double[n];
		
		for (int i=0; i<n; i++) {
			real[i]=inputArray[(int) (start+i)];
			imag[i]=0.0;
		}
		
		fft(real, imag);
		
		int[] magnitudeArray=new int[n];
		for (int i=0; i<n; i++) {
			magnitudeArray[i]=(int) Math.round(Math.sqrt(real[i]*real[i]+imag[i]*imag[i]));
		}
		
		return magnitudeArray;
	}
	
	public static void fft(double[] real, double[] imag) {
		int n=real.length;
		if (n==1) return;
		
		double[] realEven=new double[n/2];
		double[] imagEven=new double[n/2];
		double[] realOdd=new double[n/2];
		double[] imagOdd=new double[n/2];
		
		for (int i=0; i<n/2; i++) {
			realEven[i]=real[i*2];
			imagEven[i]=imag[i*2];
			realOdd[i]=real[i*2+1];
			imagOdd[i]=imag[i*2+1];
		}
		
		fft(realEven, imagEven);
		fft(realOdd, imagOdd);
		
		for (int k=0; k<n/2; k++) {
			double angle=-2*Math.PI*k/n;
			double cos=Math.cos(angle);
			double sin=Math.sin(angle);
			
			double tempReal=cos*realOdd[k]-sin*imagOdd[k];
			double tempImag=sin*realOdd[k]+cos*imagOdd[k];
			
			real[k]=realEven[k]+tempReal;
			imag[k]=imagEven[k]+tempImag;
			real[k+n/2]=realEven[k]-tempReal;
			imag[k+n/2]=imagEven[k]-tempImag;
		}
	}
	
	public static int[] normalize(int[] magnitudeArray, int minOutput, int maxOutput) {
		int maxValue=Integer.MIN_VALUE;
		
		for (int value:magnitudeArray) {
			if (value>maxValue) {
				maxValue=value;
			}
		}
		
		int[] normalizedArray=new int[magnitudeArray.length];
		for (int i=0; i<magnitudeArray.length; i++) {
			normalizedArray[i]=(int) ((double) magnitudeArray[i]/maxValue*(maxOutput-minOutput)+minOutput);
		}
		
		return normalizedArray;
	}

}