import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

public class AudioPlayer extends JFrame {
	public static void main(String[] args) {
		AudioPlayer frame=new AudioPlayer();
		frame.setVisible(true);
	}
	
	public void AudioPlayerGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 272, 264);
		JPanel contentPane=new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton backButton=new JButton("| <");
		JButton playButton=new JButton(">");
		JButton stopButton=new JButton("[]");
		JButton nextButton=new JButton("> |");
		JButton muteButton=new JButton(">/]");
		JButton openButton=new JButton("^");
		
		getContentPane().add(backButton);
		getContentPane().add(playButton);
		getContentPane().add(stopButton);
		getContentPane().add(nextButton);
		getContentPane().add(muteButton);
		getContentPane().add(openButton);
		
		pack();
	}
}