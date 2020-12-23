
package jannavs;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 *
 * @author user
 */
public class JANNAVS
{
     public static double[][] weights;
     public static double [] outputOfNode;
     public static final int numberOfNodes = 10;
     public static double tol;
     private final JButton trainButton = new JButton("TRAIN");
     private final JButton testButton = new JButton("TEST");
     private final JFrame masterFrame = new JFrame("JANNAVS");
     private final JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
     private final JScrollPane scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
             JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
     public static final JTextArea logConsole = new JTextArea(50, 50);


     /**
      * @param args the command line arguments
      */
     public static void main(String[] args)
     {
          JANNAVS theProgram = new JANNAVS();
          System.out.println(System.getProperty("user.dir"));
          theProgram.executeANN();

     }




     public void executeANN()
     {
          masterFrame.setSize(700, 400);

          masterFrame.setLayout(new GridLayout(2, 1));

          buttonPanel.add(trainButton);
          buttonPanel.add(testButton);
          masterFrame.add(buttonPanel);

          scrollPane.setViewportView(logConsole);
          logConsole.setFont(new java.awt.Font("Times New Roman", Font.PLAIN, 18));
          logConsole.setEditable(false);
          masterFrame.add(scrollPane);

          File weightsFile = new File(System.getProperty("user.dir")+ "\\weights.txt");
          if(!weightsFile.exists())
          {
               testButton.setEnabled(false);
          }

          trainButton.addActionListener(new ActionListener()
          {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                    boolean trainBoolean = Trainer.train();
                    boolean weightBoolean = new File(System.getProperty("user.dir")+ "\\weights.txt").exists();
                    testButton.setEnabled(trainBoolean || weightBoolean);
                    System.gc();
               }
          });

          testButton.addActionListener(new ActionListener()
          {
               @Override
               public void actionPerformed(ActionEvent e)
               {
                    Tester.test();
                    System.gc();
               }
          });

          masterFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
          masterFrame.setVisible(true);
     }


     public static void feedForward(double xLeft, double xRight)
     {
          outputOfNode[0] = xLeft / Math.pow(2, 15);
          outputOfNode[1] = xRight/ Math.pow(2, 15);

          outputOfNode[2] = activation(feed_Sum(2, 0, 1));
          outputOfNode[3] = activation(feed_Sum(3, 0, 1));
          outputOfNode[4] = activation(feed_Sum(4, 0, 1));

          outputOfNode[5] = activation(feed_Sum(5, 2, 4));
          outputOfNode[6] = activation(feed_Sum(6, 3));
          outputOfNode[7] = activation(feed_Sum(7, 2, 4));

          outputOfNode[8] = activation(feed_Sum(8, 0, 5, 6));
          outputOfNode[9] = activation(feed_Sum(9, 1, 6, 7));
     }


     public static double feed_Sum(int itself, int first)
     {
          double sum = weights[itself][itself];
          sum += weights[first][itself] * outputOfNode[first];
          return sum;
     }

     public static double feed_Sum(int itself, int first, int second)
     {
          double sum = feed_Sum(itself, first);
          sum += weights[second][itself] * outputOfNode[second];
          return sum;
     }

     public static double feed_Sum(int itself, int first, int second, int third)
     {
          double sum = feed_Sum(itself, first, second);
          sum += weights[third][itself] * outputOfNode[third];
          return sum;
     }

     public static double activation(double arg)
     {
          return Math.tanh(arg);
     }
}
