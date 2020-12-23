package jannavs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WeightsIO
{
     private static final Logger aLogger = Logger.getLogger(JANNAVS.class.getPackage().getName());
     private static final String weightsFile = System.getProperty("user.dir") + "\\weights.txt";


     public static void populateWeights()
     {
          JANNAVS.weights[2][2] = randomized();
          JANNAVS.weights[3][3] = randomized();
          JANNAVS.weights[4][4] = randomized();
          JANNAVS.weights[5][5] = randomized();
          JANNAVS.weights[6][6] = randomized();
          JANNAVS.weights[7][7] = randomized();
          JANNAVS.weights[8][8] = randomized();
          JANNAVS.weights[9][9] = randomized();


          JANNAVS.weights[0][2] = randomized();
          JANNAVS.weights[0][3] = randomized();
          JANNAVS.weights[0][4] = randomized();
          JANNAVS.weights[0][8] = randomized();

          JANNAVS.weights[1][2] = randomized();
          JANNAVS.weights[1][3] = randomized();
          JANNAVS.weights[1][4] = randomized();
          JANNAVS.weights[1][9] = randomized();

          JANNAVS.weights[2][5] = randomized();
          JANNAVS.weights[2][7] = randomized();

          JANNAVS.weights[4][5] = randomized();
          JANNAVS.weights[4][7] = randomized();

          JANNAVS.weights[3][6] = randomized();

          JANNAVS.weights[5][8] = randomized();

          JANNAVS.weights[6][8] = randomized();
          JANNAVS.weights[6][9] = randomized();

          JANNAVS.weights[7][9] = randomized();
     }


     public static double randomized()
     {
          return (Math.random() - 0.5)/128;
     }


     public static void displayWeights()
     {
          for (int i = 0; i < JANNAVS.numberOfNodes; i++)
          {
               for (int j = 0; j < JANNAVS.numberOfNodes; j++)
               {
                    JANNAVS.logConsole.append(String.format("[%d, %d] -> %.10f\t", i, j, JANNAVS.weights[i][j]));
               }
               JANNAVS.logConsole.append(System.getProperty("line.separator"));
          }
     }





     public static void encodeWeights()
     {
          try
          (
               FileOutputStream fos = new FileOutputStream(weightsFile);
               ObjectOutputStream oos = new ObjectOutputStream(fos);
          )
          {
               oos.writeDouble(JANNAVS.tol);
               for (int i = 0; i < JANNAVS.numberOfNodes; i++)
               {
                    for (int j = 0; j < JANNAVS.numberOfNodes; j++)
                    {
                         oos.writeDouble(JANNAVS.weights[i][j]);
                    }
               }
               oos.close();
          }
          catch(IOException ex)
          {
               aLogger.log(Level.SEVERE, "Cannot perform output.", ex);
          }
     }


     public static void retrieveWeights()
     {
          try
          (
               FileInputStream fis = new FileInputStream(weightsFile);
               ObjectInputStream ois = new ObjectInputStream(fis);
          )
          {
               JANNAVS.tol = ois.readDouble();
               for (int i = 0; i < JANNAVS.numberOfNodes; i++)
               {
                    for (int j = 0; j < JANNAVS.numberOfNodes; j++)
                    {
                         JANNAVS.weights[i][j] = ois.readDouble();
                    }
               }

               ois.close();
          }
          catch(IOException ex)
          {
               aLogger.log(Level.SEVERE, "Cannot perform input.", ex);
          }
     }
}
