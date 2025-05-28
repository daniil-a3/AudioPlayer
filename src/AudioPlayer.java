import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class AudioPlayer extends JFrame {
	boolean isMuted=false;
	int volume=100;
	
	File currentFile;
	Clip clip;
	AudioInputStream audioInputStream;
	Long currentFrame=null;
	Long trackLength=null;
	boolean stopped=true;
	boolean paused=false;
	boolean invalidFile=false;
	
	JLabel currentTrack=new JLabel("No file selected");
	JLabel timeElapsed=new JLabel("0");
	JButton backButton, playButton, stopButton, nextButton,
	muteButton, openButton;
	
	public static void main(String[] args) {
		AudioPlayer frame=new AudioPlayer();
		frame.setVisible(true);
	}
	
	public AudioPlayer() {
		setTitle("Audio Player");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		JPanel contentPane=new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		currentTrack.setBounds(10, 10, 620, 20);
		timeElapsed.setBounds(10, 40, 620, 20);
		
		backButton=new JButton("| <");
		backButton.setBounds(10, 70, 50, 50);
		backButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				backButton_mouseClicked(e);
			}
		});
		
		playButton=new JButton(">");
		playButton.setBounds(80, 70, 50, 50);
		playButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				playButton_mouseClicked(e);
			}
		});
		
		stopButton=new JButton("[]");
		stopButton.setBounds(150, 70, 50, 50);
		stopButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				stopButton_mouseClicked(e);
			}
		});
		
		nextButton=new JButton("> |");
		nextButton.setBounds(220, 70, 50, 50);
		nextButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				nextButton_mouseClicked(e);
			}
		});
		
		muteButton=new JButton("> ]");
		muteButton.setBounds(290, 70, 50, 50);
		muteButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				muteButton_mouseClicked(e);
			}
		});
		
		openButton=new JButton("^");
		openButton.setBounds(360, 70, 50, 50);
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
		
		Timer timer=new Timer(20, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (!paused) currentFrame=clip.getMicrosecondPosition();
					trackLength=clip.getMicrosecondLength();
				} catch (Exception e1) {
					;
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
	}
	
	private void backButton_mouseClicked(MouseEvent e) {
		Toolkit.getDefaultToolkit().beep();
	}
	
	private void playButton_mouseClicked(MouseEvent e) {
		try {
			if (invalidFile) throw new UnsupportedAudioFileException();
			if (stopped) {
				clip.start();
				stopped=false;
				playButton.setText("| |");
				clip.addLineListener(e1 -> {
					if (e1.getType()==LineEvent.Type.STOP) {
						stopPlay();
						stopPlay();
					}
				});
			} else {
				if (paused) {
					//clip.close();
					//loadFile();
					clip.setMicrosecondPosition(currentFrame);
					paused=false;
					clip.start();
					playButton.setText("| |");
				} else {
					paused=true;
					playButton.setText(">");
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
		} catch (Exception e1) {
			Toolkit.getDefaultToolkit().beep();
			System.err.println(e1);
		}
	}
	
	private void nextButton_mouseClicked(MouseEvent e) {
		Toolkit.getDefaultToolkit().beep();
	}
	
	private void muteButton_mouseClicked(MouseEvent e) {
		if (volume>0) {
			volume=0;
			muteButton.setText(">/]");
		} else {
			volume=100;
			muteButton.setText("> ]");
		}
	}
	
	private void openButton_mouseClicked(MouseEvent e) 
	throws UnsupportedAudioFileException, IOException {
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter() {

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
		fileChooser.addChoosableFileFilter(new FileFilter() {

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
			currentFile=selectedFile;
			try {
				stopPlay();
			} catch (NullPointerException e1) {
				loadFile();
			}
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
			invalidFile=false;
		} catch (NullPointerException e1) {
			System.out.println("No file chosen");
		} catch (UnsupportedAudioFileException e1) {
			invalidFile=true;
			showErrorMessage(String.format
			("File extension \".%s\" is currently not supported.",
			getFileExt(currentFile.toString())),
			"Unsupported File Type");
		} catch (Exception e1) {
			invalidFile=true;
			Toolkit.getDefaultToolkit().beep();
			System.err.println(e1);
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
	
	private static void showErrorMessage(String message, String title) {
		JOptionPane.showMessageDialog(null, message, title,
			JOptionPane.ERROR_MESSAGE);
	}
	
	private void stopPlay() {
		clip.stop();
		playButton.setText(">");
		clip.close();
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
