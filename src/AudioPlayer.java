import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

public class AudioPlayer extends JFrame {
	boolean isMuted=false;
	int volume=100;
	int backupVolume=100;
	
	static File currentFile;
	static Clip clip;
	AudioInputStream audioInputStream;
	Long currentFrame=null;
	Long trackLength=null;
	boolean stopped=true;
	boolean paused=false;
	boolean muted=false;
	boolean invalidFile=false;
	boolean sliderUsed=false;
	FloatControl gainControl;
	Visualizer audioVis;
	static String visStyle="Horizontal";
	
	static int updateFrequencyMs=20;
	
	static int panelWidth=640;
	static int panelHeight=480;
	
	JLabel currentTrack=new JLabel("No file selected");
	JLabel timeElapsed=new JLabel("0");
	JButton backButton, playButton, stopButton, nextButton,
	muteButton, openButton;
	JComboBox visStyleBox;
	String visOptions[]={"Horizontal", "Oscilloscope", "Bars"};
	JSlider playPos=new JSlider(JSlider.HORIZONTAL,0,0,0);
	JSlider volSlider=new JSlider(JSlider.HORIZONTAL,0,100,0);
	
	ImageIcon backIcon=new ImageIcon("res\\control_previous.png");
	ImageIcon playIcon=new ImageIcon("res\\control_play.png");
	ImageIcon pauseIcon=new ImageIcon("res\\control_pause.png");
	ImageIcon stopIcon=new ImageIcon("res\\control_stop.png");
	ImageIcon nextIcon=new ImageIcon("res\\control_next.png");
	ImageIcon muteIcon=new ImageIcon("res\\sound.png");
	ImageIcon unmuteIcon=new ImageIcon("res\\nosound.png");
	ImageIcon openIcon=new ImageIcon("res\\control_eject.png");
	
	public static void main(String[] args)
	throws UnsupportedAudioFileException, IOException {
		AudioPlayer frame=new AudioPlayer();
		frame.setVisible(true);
	}
	
	public AudioPlayer()
	throws UnsupportedAudioFileException, IOException {
		audioVis=new Visualizer();
		
		setTitle("Audio Player");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		JPanel contentPane=new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		currentTrack.setBounds(10, 10, 620, 20);
		timeElapsed.setBounds(10, 40, 620, 20);
		playPos.setBounds(10, 60, 620, 30);
		playPos.setPaintTicks(true);
		playPos.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source=(JSlider)e.getSource();
				sliderUsed=source.getValueIsAdjusting();
				if (sliderUsed) {
					try {
						if (invalidFile) throw new UnsupportedAudioFileException();
						if (trackLength!=null) 
							currentFrame=(long) source.getValue();
						clip.setMicrosecondPosition(source.getValue());
					} catch (Exception e1) {
						Toolkit.getDefaultToolkit().beep();
					}
				}
			}
		});
		volSlider.setValue(100);
		volSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source=(JSlider)e.getSource();
				volume=source.getValue();
			}
		});
		
		backButton=new JButton(backIcon);
		backButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				backButton_mouseClicked(e);
			}
		});
		
		playButton=new JButton(playIcon);
		playButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				playButton_mouseClicked(e);
			}
		});
		
		stopButton=new JButton(stopIcon);
		stopButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				stopButton_mouseClicked(e);
			}
		});
		
		nextButton=new JButton(nextIcon);
		nextButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				nextButton_mouseClicked(e);
			}
		});
		
		muteButton=new JButton(muteIcon);
		muteButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				muteButton_mouseClicked(e);
			}
		});
		
		openButton=new JButton(openIcon);
		openButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					openButton_mouseClicked(e);
				} catch (UnsupportedAudioFileException
						| IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		visStyleBox=new JComboBox(visOptions);
		visStyleBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				visStyle=(String) visStyleBox.getSelectedItem();
				System.out.println(visStyle);
				audioVis.repaint();
		}});
		
		contentPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				panelWidth=contentPane.getWidth();
				panelHeight=contentPane.getHeight();

				currentTrack.setBounds(10, 10, panelWidth-10, 20);
				timeElapsed.setBounds(10, panelHeight-100, panelWidth-10, 20);
				playPos.setBounds(10, panelHeight-80, panelWidth-20, 30);
				volSlider.setBounds(panelWidth-190, panelHeight-40, 100, 30);
				backButton.setBounds(10, panelHeight-40, 30, 30);
				playButton.setBounds(50, panelHeight-40, 30, 30);
				stopButton.setBounds(90, panelHeight-40, 30, 30);
				nextButton.setBounds(130, panelHeight-40, 30, 30);
				visStyleBox.setBounds(170, panelHeight-40, 90, 30);
				muteButton.setBounds(panelWidth-80, panelHeight-40, 30, 30);
				openButton.setBounds(panelWidth-40, panelHeight-40, 30, 30);
				audioVis.setBounds(10, 30, panelWidth-20, panelHeight-130);
			}
		});
		
		Timer timer=new Timer(updateFrequencyMs, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!paused)
						currentFrame=clip.getMicrosecondPosition();
					trackLength=clip.getMicrosecondLength();
					playPos.setMaximum(Math.toIntExact(trackLength));
					playPos.setValue(Math.toIntExact(currentFrame));
					if (trackLength<=10000000) { // 10 seconds
						playPos.setMinorTickSpacing(100000); // 0.1 s
						playPos.setMajorTickSpacing(1000000); // 1 s
					} else if (trackLength<=120000000) { // 2 mins
							playPos.setMinorTickSpacing(1000000); // 1 s
							playPos.setMajorTickSpacing(10000000); // 10 s
					} else if (trackLength<=300000000) { // 5 mins
						playPos.setMinorTickSpacing(5000000); // 5 s
						playPos.setMajorTickSpacing(30000000); // 30 s
					} else if (trackLength<=1200000000) { // 20 mins
						playPos.setMinorTickSpacing(10000000); // 10 s
						playPos.setMajorTickSpacing(60000000); // 1 min
					} else {
						playPos.setMinorTickSpacing(30000000); // 30 s
						playPos.setMajorTickSpacing(60000000); // 1 min
					}
					//System.out.println(gainControl.getValue());
					if (!muted) {
						gainControl.setValue((float)
							(((Math.log10(Math.pow((float) volume/100, 0.5)))
								*40)));
					} else {
						gainControl.setValue(-80.0f);
					}
					if (!stopped) {
						audioVis.repaint();
					}
				} catch (NullPointerException e1) {
					;
				} catch (Exception e1) {
					System.err.println(e1);
				}
				timeElapsed.setText(humanTime(currentFrame)+"/"+humanTime(trackLength));
			}
		});
		timer.start();
		
		/// DO NOT FORGET ABOUT THESE!!!!!!!!!!
		contentPane.add(currentTrack);
		contentPane.add(backButton);
		contentPane.add(playButton);
		contentPane.add(stopButton);
		contentPane.add(nextButton);
		contentPane.add(muteButton);
		contentPane.add(openButton);
		contentPane.add(timeElapsed);
		contentPane.add(playPos);
		contentPane.add(volSlider);
		contentPane.add(audioVis);
		contentPane.add(visStyleBox);
	}
	
	private void backButton_mouseClicked(MouseEvent e) {
		Toolkit.getDefaultToolkit().beep();
	}
	
	private void playButton_mouseClicked(MouseEvent e) {
		try {
			if (invalidFile) throw new UnsupportedAudioFileException();
			if (stopped) {
				if (currentFrame.compareTo(trackLength)==0
				&& trackLength!=null) {
					currentFrame=0L;
					clip.setMicrosecondPosition(currentFrame);
				}
				clip.start();
				stopped=false;
				playButton.setIcon(pauseIcon);
				clip.addLineListener(e1 -> {
					if (e1.getType()==LineEvent.Type.STOP) {
						stopPlay();
					}
				});
			} else {
				if (paused) {
					//resumes
					clip.setMicrosecondPosition(currentFrame);
					paused=false;
					if (currentFrame==trackLength && trackLength!=null) {
						currentFrame=0L;
						clip.setMicrosecondPosition(currentFrame);
					}
					clip.start();
					playButton.setIcon(pauseIcon);
					clip.addLineListener(e1 -> {
						if (e1.getType()==LineEvent.Type.STOP) {
							stopPlay();
						}
					});
				} else {
					//pauses
					paused=true;
					playButton.setIcon(playIcon);
					currentFrame=clip.getMicrosecondPosition();
					clip.stop();
				}
			}
			//System.out.println(String.format("paused: %b\n stopped: %b", paused, stopped));
		} catch (Exception e1) {
			Toolkit.getDefaultToolkit().beep();
			System.err.println(e1);
		}
	}
	
	private void stopButton_mouseClicked(MouseEvent e) {
		try {
			stopPlay();
			stopped=true;
			paused=false;
			loadFile();
		} catch (Exception e1) {
			Toolkit.getDefaultToolkit().beep();
			System.err.println(e1);
		}
	}
	
	private void nextButton_mouseClicked(MouseEvent e) {
		Toolkit.getDefaultToolkit().beep();
	}
	
	private void muteButton_mouseClicked(MouseEvent e) {
		if (!muted) {
			muted=true;
			muteButton.setIcon(unmuteIcon);
		} else {
			muted=false;
			muteButton.setIcon(muteIcon);
		}
	}
	
	private void openButton_mouseClicked(MouseEvent e) 
	throws UnsupportedAudioFileException, IOException {
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(new FileFilter() {

				public String getDescription() {
					return "All audio files (*.wav, *.mp3, *.aac, *.m4a)";
				}

				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					} else {
						String filename=f.getName().toLowerCase();
						return filename.endsWith(".wav")
						|| filename.endsWith(".mp3")
						|| filename.endsWith(".aac")
						|| filename.endsWith(".m4a");
					}
				}
			});
		fileChooser.setFileFilter(new FileFilter() {

				public String getDescription() {
					return "WAVE File (*.wav)";
				}

				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					} else {
						String filename=f.getName().toLowerCase();
						return filename.endsWith(".wav");
					}
				}
			});
		fileChooser.addChoosableFileFilter(new FileFilter() {

			public String getDescription() {
				return "MP3 File (*.mp3)";
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename=f.getName().toLowerCase();
					return filename.endsWith(".mp3");
				}
			}
		});
		fileChooser.addChoosableFileFilter(new FileFilter() {

			public String getDescription() {
				return "AAC File (*.aac)";
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename=f.getName().toLowerCase();
					return filename.endsWith(".aac");
				}
			}
		});
		fileChooser.addChoosableFileFilter(new FileFilter() {

			public String getDescription() {
				return "M4A File (*.m4a)";
			}

			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename=f.getName().toLowerCase();
					return filename.endsWith(".m4a");
				}
			}
		});
		int result=fileChooser.showOpenDialog(fileChooser);
		if (result==JFileChooser.APPROVE_OPTION) {
			File selectedFile=fileChooser.getSelectedFile();
			System.out.println("Selected file: "+selectedFile.getAbsolutePath());
			currentTrack.setText(selectedFile.getName());
			setTitle("Audio Player - "+selectedFile.getName());
			currentFile=selectedFile;
			try {
				if (!paused) stopPlay();
			} catch (Exception e1) {
				System.err.println(e1);
			}
			currentFrame=0L;
			loadFile();
		}
	}
	
	private void loadFile() {
		try {
			System.out.println(currentFile.toString());
			audioInputStream=AudioSystem.getAudioInputStream
					(new File(currentFile.toString())
							.getAbsoluteFile());
			clip=AudioSystem.getClip();
			clip.open(audioInputStream);
			audioVis.clipToByteArray();
			audioVis.byteToIntArray();
			audioVis.repaint();
			invalidFile=false;
		} catch (NullPointerException e1) {
			System.out.println("No file chosen");
		} catch (UnsupportedAudioFileException e1) {
			invalidFile=true;
			showErrorMessage(String.format
			("File extension \".%s\" is currently not supported.",
			getFileExt(currentFile.toString())),
			"Unsupported File Type");
		} catch (LineUnavailableException e1) {
			invalidFile=true;
			showErrorMessage(e1.toString(),
			"Unsupported Format");
		} catch (Exception e1) {
			invalidFile=true;
			Toolkit.getDefaultToolkit().beep();
			System.err.println(e1);
		}
		
		try {
			gainControl=(FloatControl) clip.getControl
					(FloatControl.Type.MASTER_GAIN);
		} catch (Exception e) {
			System.err.println(e);
		}
	}
	
	private String getFileExt(String filename) {
		if (filename==null) {
			return null;
		}
		int dotIndex=filename.lastIndexOf(".");
		if (dotIndex>=0) {
			return filename.substring(dotIndex+1);
		}
		return "";
	}
	
	public static void showErrorMessage(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title,
			JOptionPane.ERROR_MESSAGE);
	}
	
	private void stopPlay() {
		clip.stop();
		playButton.setIcon(playIcon);
		clip.close();
		if (paused==false) {
			stopped=true;
		}
		loadFile();
	}
	
	private String humanTime(Long time) {
		if (time==null) {
			return "---:--.--";
		} else {
			return String.format("%03d:%02d.%02d",
					((int) (((double) time)/60000000)), //mins
					((int) (((double) time)/1000000)%60), //secs
					((int) (((double) time)/10000)%100)); //millis
		}
	}
}
