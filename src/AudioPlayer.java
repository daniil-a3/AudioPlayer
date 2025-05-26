import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.Toolkit;
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
	
	JLabel currentTrack=new JLabel("No file selected");
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
		
		backButton=new JButton("| <");
		backButton.setBounds(10, 40, 50, 50);
		backButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				backButton_mouseClicked(e);
			}
		});
		
		playButton=new JButton(">");
		playButton.setBounds(80, 40, 50, 50);
		playButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				playButton_mouseClicked(e);
			}
		});
		
		stopButton=new JButton("[]");
		stopButton.setBounds(150, 40, 50, 50);
		stopButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				stopButton_mouseClicked(e);
			}
		});
		
		nextButton=new JButton("> |");
		nextButton.setBounds(220, 40, 50, 50);
		nextButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				nextButton_mouseClicked(e);
			}
		});
		
		muteButton=new JButton(">/]");
		muteButton.setBounds(290, 40, 50, 50);
		muteButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				muteButton_mouseClicked(e);
			}
		});
		
		openButton=new JButton("^");
		openButton.setBounds(360, 40, 50, 50);
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
		
		contentPane.add(currentTrack);
		contentPane.add(backButton);
		contentPane.add(playButton);
		contentPane.add(stopButton);
		contentPane.add(nextButton);
		contentPane.add(muteButton);
		contentPane.add(openButton);
	}
	
	private void backButton_mouseClicked(MouseEvent e) {
		Toolkit.getDefaultToolkit().beep();
	}
	
	private void playButton_mouseClicked(MouseEvent e) {
		try {
			clip.start();
			playButton.setText("| |");
			clip.addLineListener(e1 -> {
				if (e1.getType() == LineEvent.Type.STOP) {
					stopPlay(); //why does replaying it only work the second time!?
				}
			});
		} catch (Exception e1) {
			Toolkit.getDefaultToolkit().beep();
			System.err.println(e1);
		}
	}
	
	private void stopButton_mouseClicked(MouseEvent e) {
		try {
			stopPlay();
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
			muteButton.setText("> ]");
		} else {
			volume=100;
			muteButton.setText(">/]");
		}
	}
	
	private void openButton_mouseClicked(MouseEvent e) 
	throws UnsupportedAudioFileException, IOException {
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter() {

				public String getDescription() {
					return "All audio files (*.wav, *.mp3, *.aac)";
				}

				public boolean accept(File f) {
					if (f.isDirectory()) {
						return true;
					} else {
						String filename=f.getName().toLowerCase();
						return filename.endsWith(".wav")
						|| filename.endsWith(".mp3") 
						|| filename.endsWith(".aac") ;
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
		int result=fileChooser.showOpenDialog(fileChooser);
		if (result==JFileChooser.APPROVE_OPTION) {
			File selectedFile=fileChooser.getSelectedFile();
			System.out.println("Selected file: "+selectedFile.getAbsolutePath());
			currentTrack.setText(selectedFile.getName());
			currentFile=selectedFile;
		}
		
		loadFile();
	}
	
	private void loadFile() {
		try {
			System.out.println(currentFile.toString());
			audioInputStream=AudioSystem.getAudioInputStream
					(new File(currentFile.toString())
							.getAbsoluteFile());
			clip=AudioSystem.getClip();
			clip.open(audioInputStream);
		} catch (NullPointerException e1) {
			System.out.println("No file chosen");
		} catch (UnsupportedAudioFileException e1) {
			showErrorMessage(String.format
			("File extension \"%s\" is currently not supported.",
			getFileExt(currentFile.toString())),
			"Unsupported File Type");
		} catch (Exception e1) {
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
}
