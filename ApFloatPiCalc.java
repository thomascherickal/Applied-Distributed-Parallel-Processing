import java.io.Serializable;
import java.io.PrintWriter;
import java.io.IOException;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.samples.*;




import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;



public class Pi
{

static int n1, n2;
long time;


public static void main(String args[])
        throws IOException, ApfloatRuntimeException
    {
	  g = new GUI("picalculator", n1);

        g.addText("Calculating pi to " + args[0] + " radix-" + args[1] + " digits");

        long time = System.currentTimeMillis();

	  n1 = Integer.parseInt(args[2]);
	n2 = Integer.parseInt(args[3]);
	
	  ChudnovskyPiCalculator cpc = new ChudnovskyPiCalculator(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        Apfloat pi = cpc.execute();
        time = System.currentTimeMillis() - time;


        g.addText("Total elapsed time " + time / 1000.0 + " seconds");
	
	   g.addText(pi + "");	
    }
	
class GUI extends JFrame
{
    JTextArea text;
    GUI(String title,int id)
    {
        super(title + " " + id);
        setSize(300, 200);
        text = new JTextArea(8, 8);
        setLayout(new BorderLayout());
        add(new JScrollPane(text));
        setVisible(true);
	  addWindowListener(
			new WindowAdapter() {
		public void windowClosing(WindowEvent we)
		{
			System.exit(0);
		}
	}
	);
        
    }
    public void addText(String msg)
    {
        text.setText(text.getText() + "\n" + msg);
    }
    public void setText(String msg)
    {
        text.setText(msg);
    }
    
}
