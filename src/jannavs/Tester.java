package jannavs;

import java.io.File;
import java.io.IOException;


public class Tester
{
     private static int[][] feedSamples, referenceSamples;
     private static final String sourceFilename = System.getProperty("user.dir") + "\\FEED.wav";
     private static final String referenceFilename = System.getProperty("user.dir") + "\\REFER.wav";
     public static void test()
     {
          if (!new File(sourceFilename).exists() || !new File(referenceFilename).exists())
          {
               JANNAVS.logConsole.append("FEED.wav or REFER.wav is missing\n");
               return;
          }

          JANNAVS.weights = new double[JANNAVS.numberOfNodes][JANNAVS.numberOfNodes];
          WeightsIO.retrieveWeights();
          JANNAVS.outputOfNode = new double[JANNAVS.numberOfNodes];
          int numberOfTestInputs = Tester.getInputSamples();
          double[][] outputSamples = new double[2][numberOfTestInputs];


          JANNAVS.outputOfNode = new double[JANNAVS.numberOfNodes];
          for (int i = 0 ; i < numberOfTestInputs; i++)
          {
               JANNAVS.feedForward(feedSamples[0][i], feedSamples[1][i]);

               outputSamples[0][i] = (i < 45) ? 0 : JANNAVS.outputOfNode[8];
               outputSamples[1][i] = (i < 45) ? 0 : JANNAVS.outputOfNode[9];
          }

          JANNAVS.tol = 9 * Math.pow(10, -1);
          int j = 0;
          int numberOfConverged = numberOfTestInputs;

          while(numberOfConverged >= 0.5 * numberOfTestInputs)
          {
               numberOfConverged = 0;

               for (int i = 0 ; i < numberOfTestInputs; i++)
               {
                    double oLeft = referenceSamples[0][i] / Math.pow(2, 15);
                    double oRight = referenceSamples[1][i] / Math.pow(2, 15);

                    double left_diff = Math.abs(oLeft - outputSamples[0][i]);
                    double right_diff = Math.abs(oRight - outputSamples[1][i]);

                    if (left_diff <= JANNAVS.tol && right_diff <= JANNAVS.tol)
                    {
                         numberOfConverged++;
                    }
               }

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

          JANNAVS.logConsole.append("---TESTING END---\n");

          encodeOutputWave(outputSamples, System.getProperty("user.dir") + "\\OUTPUT.wav");
          JANNAVS.logConsole.append("one");
          cleanUp();

     }

     public static void cleanUp()
     {
          JANNAVS.weights = null;
          JANNAVS.outputOfNode = null;
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
               feedSamples = new int[sourceWave.numChannels][(int) sourceWave.numFrames];
               referenceSamples = new int[sourceWave.numChannels][(int) sourceWave.numFrames];

               sourceWave.readFrames(feedSamples, (int) sourceWave.numFrames);
               referenceWave.readFrames(referenceSamples, (int) sourceWave.numFrames);

               // Close the wavFile
               sourceWave.close();
               referenceWave.close();
          }
          catch (IOException | WavFileException e)
          {
                  System.err.println(e);
          }

          return  numberOfTestInputs;
     }


     public static void encodeOutputWave(double[][] outputSamples, String outputWave)
     {
          try
          {
                  int sampleRate = 44100;

                  // Create a wav file with the name specified as the first argument
                  WavFile wavFile = WavFile.newWavFile(new File(outputWave), 2, outputSamples[0].length, 16, sampleRate);
                  wavFile.writeFrames(outputSamples, outputSamples[0].length);


                  // Close the wavFile
                  wavFile.close();

          }
          catch (IOException | WavFileException e)
          {
                  System.err.println(e);
          }
     }
}
