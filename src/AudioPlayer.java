import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class AudioPlayer extends JFrame {
	boolean isMuted=false;
	File currentFile=null;
	JLabel currentTrack=new JLabel("No file selected");
	JButton backButton, playButton, stopButton, nextButton,
	muteButton, openButton;
	int volume=100;
	
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
				openButton_mouseClicked(e);
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
		Toolkit.getDefaultToolkit().beep();
	}
	
	private void stopButton_mouseClicked(MouseEvent e) {
		Toolkit.getDefaultToolkit().beep();
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
	
	private void openButton_mouseClicked(MouseEvent e) {
		JFileChooser fileChooser=new JFileChooser();
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter() {

			   public String getDescription() {
			       return "All audio files (*.wav, *.mp3)";
			   }

			   public boolean accept(File f) {
			       if (f.isDirectory()) {
			           return true;
			       } else {
			           String filename = f.getName().toLowerCase();
			           return filename.endsWith(".wav") || filename.endsWith(".mp3") ;
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
	}
}
