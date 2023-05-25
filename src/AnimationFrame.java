import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class AnimationFrame extends JFrame {

    private JPanel mainPanel;

    public AnimationFrame() {
        createUIComponents();
    }

    private void createUIComponents() {
        // set mainPanel (JPanel) to be a new AnimationPanel, which is allowed
        // since AnimationPanel is a subclass of JPanel; since AnimationPanel is designed
        // entirely in code, we must CHECK the "Custom Create" box for mainPanel in the Swing UI designer
        mainPanel = new AnimationPanel();
        setContentPane(mainPanel);

        setTitle("Animation App");
        setSize(550, 500);
        setLocation(450, 100);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

}
