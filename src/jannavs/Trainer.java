package jannavs;

import java.io.File;
import java.io.IOException;

public class Trainer
{
     private static int[][] trainingSamples, desiredOutputs;
     private static double[] errorOfNode;
     private static double[][] deltas;
     private static final String sourceFilename = System.getProperty("user.dir") + "\\CLEAN.wav";
     private static final String referenceFilename = System.getProperty("user.dir") + "\\DIRTY.wav";
     static double lr = 1;


     public static boolean train()
     {
          if (!new File(sourceFilename).exists() || !new File(referenceFilename).exists())
          {
               JANNAVS.logConsole.append("CLEAN.wav or DIRTY.wav is missing\n");
               return false;
          }

          errorOfNode = new double[JANNAVS.numberOfNodes];
          deltas = new double[JANNAVS.numberOfNodes][JANNAVS.numberOfNodes];
          JANNAVS.outputOfNode = new double[JANNAVS.numberOfNodes];
          JANNAVS.weights = new double[JANNAVS.numberOfNodes][JANNAVS.numberOfNodes];
          WeightsIO.populateWeights();


          WeightsIO.displayWeights();
          int numberOfTestInputs = Trainer.getInputSamples();
          JANNAVS.tol = 3 * Math.pow(10, -1);
          double[][] simulatedOutputSamples = new double[2][numberOfTestInputs];

          //showInputSamples();
          int j = 0;
          int numberOfConverged = numberOfTestInputs;




          while(numberOfConverged >= 0.9 * numberOfTestInputs)
          {
               numberOfConverged = 0;

               for (int i = 0 ; i < numberOfTestInputs; i++)
               {
                    JANNAVS.feedForward(trainingSamples[0][i], trainingSamples[1][i]);
                    numberOfConverged += backPropagate(desiredOutputs[0][i], desiredOutputs[1][i]);
                    computeDeltas();
                    updateWeights();

                    simulatedOutputSamples[0][i] = (i < 45) ? 0 : JANNAVS.outputOfNode[8];
                    simulatedOutputSamples[1][i] = (i < 45) ? 0 : JANNAVS.outputOfNode[9];
               }

               System.gc();


               j++;
               JANNAVS.logConsole.append(String.format("%d\t%.10f\t%.4f\n", j,
                  JANNAVS.tol, ((float) numberOfConverged/numberOfTestInputs)));


               if (JANNAVS.tol >=  2 * Math.pow(10, -1))
               {
                    JANNAVS.tol -= Math.pow(10, -1);
               }
               else if (JANNAVS.tol >= 2  * Math.pow(10, -2))
               {
                    JANNAVS.tol -= Math.pow(10, -2);
               }
               else if (JANNAVS.tol >= 2  * Math.pow(10, -3))
               {
                    JANNAVS.tol -= Math.pow(10, -3);
               }
               else
               {
                    break;
               }

          }

          JANNAVS.logConsole.append("---TRAINING END---\n");

          if (new File(sourceFilename).exists())
          {
               WeightsIO.displayWeights();
               WeightsIO.encodeWeights();
          }
          cleanUp();
          return true;
     }


     public static void cleanUp()
     {
          trainingSamples =  null;
          desiredOutputs = null;
          errorOfNode = null;
          deltas = null;
          Tester.cleanUp();
     }


     public static int backPropagate(int left, int right)
     {
          double oLeft = left / Math.pow(2, 15);
          double oRight = right / Math.pow(2, 15);

          double left_diff = Math.abs(oLeft - JANNAVS.outputOfNode[8]);
          double right_diff = Math.abs(oRight - JANNAVS.outputOfNode[9]);

          errorOfNode[8] = d_activation(JANNAVS.outputOfNode[8]) * (oLeft - JANNAVS.outputOfNode[8]);
          errorOfNode[9] = d_activation(JANNAVS.outputOfNode[9]) * (oRight - JANNAVS.outputOfNode[9]);

          errorOfNode[5] = d_activation(JANNAVS.outputOfNode[5]) * back_Sum(5, 8);
          errorOfNode[6] = d_activation(JANNAVS.outputOfNode[6]) * back_Sum(6, 8, 9);
          errorOfNode[7] = d_activation(JANNAVS.outputOfNode[7]) * back_Sum(7, 9);

          errorOfNode[2] = d_activation(JANNAVS.outputOfNode[2]) * back_Sum(2, 5, 7);
          errorOfNode[3] = d_activation(JANNAVS.outputOfNode[3]) * back_Sum(3, 6);
          errorOfNode[4] = d_activation(JANNAVS.outputOfNode[4]) * back_Sum(4, 5, 7);

          if (left_diff <= JANNAVS.tol && right_diff <= JANNAVS.tol)
          {
               return 1;
          }
          return 0;
     }

     public static double back_Sum(int itself, int first)
     {
          return JANNAVS.weights[itself][first] * errorOfNode[first];
     }

     public static double back_Sum(int itself, int first, int second)
     {
          double sum = back_Sum(itself, first);
          sum += JANNAVS.weights[itself][second] * errorOfNode[second];
          return sum;
     }


     public static double d_activation(double arg)
     {
          return 1 - Math.pow(JANNAVS.activation(arg), 2);
     }


     public static void computeDeltas()
     {
          deltas[2][2] = errorOfNode[2] * lr;
          deltas[3][3] = errorOfNode[3] * lr;
          deltas[4][4] = errorOfNode[4] * lr;
          deltas[5][5] = errorOfNode[5] * lr;
          deltas[6][6] = errorOfNode[6] * lr;
          deltas[7][7] = errorOfNode[7] * lr;
          deltas[8][8] = errorOfNode[8] * lr;
          deltas[9][9] = errorOfNode[9] * lr;


          deltas[0][2] = errorOfNode[2] * JANNAVS.outputOfNode[0] * lr;
          deltas[0][3] = errorOfNode[3] * JANNAVS.outputOfNode[0] * lr;
          deltas[0][4] = errorOfNode[4] * JANNAVS.outputOfNode[0] * lr;
          deltas[0][8] = errorOfNode[8] * JANNAVS.outputOfNode[0] * lr;

          deltas[1][2] = errorOfNode[2] * JANNAVS.outputOfNode[1] * lr;
          deltas[1][3] = errorOfNode[3] * JANNAVS.outputOfNode[1] * lr;
          deltas[1][4] = errorOfNode[4] * JANNAVS.outputOfNode[1] * lr;
          deltas[1][9] = errorOfNode[9] * JANNAVS.outputOfNode[1] * lr;

          deltas[2][5] = errorOfNode[5] * JANNAVS.outputOfNode[2] * lr;
          deltas[2][7] = errorOfNode[7] * JANNAVS.outputOfNode[2] * lr;

          deltas[4][5] = errorOfNode[5] * JANNAVS.outputOfNode[4] * lr;
          deltas[4][7] = errorOfNode[7] * JANNAVS.outputOfNode[4] * lr;

          deltas[3][6] = errorOfNode[6] * JANNAVS.outputOfNode[3] * lr;

          deltas[5][8] = errorOfNode[8] * JANNAVS.outputOfNode[5] * lr;

          deltas[6][8] = errorOfNode[8] * JANNAVS.outputOfNode[6] * lr;
          deltas[6][9] = errorOfNode[9] * JANNAVS.outputOfNode[6] * lr;

          deltas[7][9] = errorOfNode[9] * JANNAVS.outputOfNode[7] * lr;
     }


     public static void updateWeights()
     {
          JANNAVS.weights[2][2] += deltas[2][2];
          JANNAVS.weights[3][3] += deltas[3][3];
          JANNAVS.weights[4][4] += deltas[4][4];
          JANNAVS.weights[5][5] += deltas[5][5];
          JANNAVS.weights[6][6] += deltas[6][6];
          JANNAVS.weights[7][7] += deltas[7][7];
          JANNAVS.weights[8][8] += deltas[8][8];
          JANNAVS.weights[9][9] += deltas[9][9];


          JANNAVS.weights[0][2] += deltas[0][2];
          JANNAVS.weights[0][3] += deltas[0][3];
          JANNAVS.weights[0][4] += deltas[0][4];
          JANNAVS.weights[0][8] += deltas[0][8];

          JANNAVS.weights[1][2] += deltas[1][2];
          JANNAVS.weights[1][3] += deltas[1][3];
          JANNAVS.weights[1][4] += deltas[1][4];
          JANNAVS.weights[1][9] += deltas[1][9];

          JANNAVS.weights[2][5] += deltas[2][5];
          JANNAVS.weights[2][7] += deltas[2][7];

          JANNAVS.weights[4][5] += deltas[4][5];
          JANNAVS.weights[4][7] += deltas[4][7];

          JANNAVS.weights[3][6] += deltas[3][6];

          JANNAVS.weights[5][8] += deltas[5][8];

          JANNAVS.weights[6][8] += deltas[6][8];
          JANNAVS.weights[6][9] += deltas[6][9];

          JANNAVS.weights[7][9] += deltas[7][9];
     }


     private static int getInputSamples()
     {
          int numberOfTestInputs = 0;

          try
          {
               // Open the wav file specified as the first argument
               WavFile sourceWave = WavFile.openWavFile(new File(sourceFilename));
               WavFile referenceWave = WavFile.openWavFile(new File(referenceFilename));

/*HERE*/       numberOfTestInputs = (int) sourceWave.numFrames;
               trainingSamples = new int[sourceWave.numChannels][(int) sourceWave.numFrames];
               desiredOutputs = new int[sourceWave.numChannels][(int) sourceWave.numFrames];

               sourceWave.readFrames(trainingSamples, (int) sourceWave.numFrames);
               referenceWave.readFrames(desiredOutputs, (int) sourceWave.numFrames);

               // Close the wavFile
               sourceWave.close();
               referenceWave.close();
          }
          catch (WavFileException | IOException e)
          {
                  System.err.println(e);
                  e.printStackTrace();
          }

          return  numberOfTestInputs;
     }
}
