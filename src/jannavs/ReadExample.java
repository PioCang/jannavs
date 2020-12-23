package jannavs;
import java.io.*;

public class ReadExample
{
	//public static void main(String[] args)
	{
		try
		{
			// Open the wav file specified as the first argument
			WavFile wavFile = WavFile.openWavFile(new File("C:\\Users\\user\\Desktop\\DIRTY.wav"));

			// Display information about the wav file
			wavFile.display();

			// Get the number of audio channels in the wav file
			int numChannels = wavFile.getNumChannels();
                        int offset = 2205000;

                        int numFrames = (int) wavFile.numFrames;
			// Create a buffer of 100 frames
			int[][] buffer = new int[numChannels][numFrames * numChannels];

                        wavFile.readFrames(buffer, numFrames);

                        for (int i = offset; i < offset + 10*0.005*44100; i+= 0.005 * 44100)
                        {
                             System.out.printf("%d -> [%d, %d]\n", i, buffer[0][i], buffer[1][i]);
                        }

			// Close the wavFile
			wavFile.close();

		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
}
