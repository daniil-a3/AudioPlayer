import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class AudioPlayer extends JFrame {
	public static void main(String[] args) {
		AudioPlayer frame=new AudioPlayer();
		frame.setVisible(true);
	}
	
	public AudioPlayer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 640, 480);
		JPanel contentPane=new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton backButton=new JButton("| <");
		backButton.setBounds(10, 10, 50, 50);
		
		JButton playButton=new JButton(">");
		playButton.setBounds(80, 10, 50, 50);
		
		JButton stopButton=new JButton("[]");
		stopButton.setBounds(150, 10, 50, 50);
		
		JButton nextButton=new JButton("> |");
		nextButton.setBounds(220, 10, 50, 50);
		
		JButton muteButton=new JButton(">/]");
		muteButton.setBounds(290, 10, 50, 50);
		
		JButton openButton=new JButton("^");
		openButton.setBounds(360, 10, 50, 50);
		
		contentPane.add(backButton);
		contentPane.add(playButton);
		contentPane.add(stopButton);
		contentPane.add(nextButton);
		contentPane.add(muteButton);
		contentPane.add(openButton);
	}
}
