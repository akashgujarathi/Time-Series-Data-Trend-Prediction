package timeSeriesPredection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.StringTokenizer;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class DataPredectionAlgorithm {
	static int TotalFileInput = 118; // Total Input Data
	static int ValueToPredect = 29; // Total value to predict 
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {

		BufferedReader x;
		x = new BufferedReader(new FileReader("product_distribution_training_set.txt"));
		File f = new File("output_1.txt");  //Contains output of individual item
		File f1 = new File("output_2.txt"); //Contains output of total items
		File f3 = new File("output.txt");	// OutputFile 
		f.delete();
		f1.delete();
		f3.delete();

		int [] fileInput = new int[TotalFileInput]; //InputArray Will hold the input values
		int fileInputCnt=0;

		double[][] data = new double[TotalFileInput-1][2]; // 2 D array will be given as a input to the Linear regression (X,Y) 
		int i=0;

		String Line = x.readLine();

		int dataCntX=0;

		try {
			while(Line != null)
			{
				/*
				 * Tokenizing the input file 
				 */
				fileInputCnt = 0;
				StringTokenizer St = new StringTokenizer(Line);
				int productID = Integer.parseInt(St.nextToken());

				while(St.hasMoreTokens()) {
					fileInput[fileInputCnt++] = Integer.parseInt(St.nextToken());
				}

				dataCntX=0;

				for(i=0;i<(fileInputCnt-1);i++)
				{
					data[dataCntX++][0]=fileInput[i];		// Inserting the value on Data[x][0] representing the x axis 
				}

				dataCntX=0;

				for(i=0;i<(fileInputCnt-1);i++)
				{
					data[dataCntX++][1]=fileInput[i+1];	   //Inserting the value on Data[x][0] representing the y axis
				}
				AutoReg(data,productID);					// Function will predict the 29 days value per item
				Line = x.readLine();
			}
		}catch (Exception e) {
			System.out.println(e);
		}

		forcasteTotal();									// Function for the total forecast 
	}
	/*
	 * Function AutoReg (InputArray(X,Y), Product ID)
	 * Function takes in input data and calculates the slope, standardError and the intercept
	 * Formula for prediction output = intercept + (input data * slope) + standardError
	 * A loop from 0..29 is used to predict the next 29 values of of the individual item  
	 * Output is then appended to the output_1.txt
	 * */
	public static void AutoReg(double[][] data,int productID) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter("output_1.txt", true));
		writer.append(Integer.toString(productID));
		SimpleRegression test = new SimpleRegression();
		test.addData(data);
		double Intercept = test.getIntercept();
		double Slope = test.getSlope();
		double StandardError = test.getInterceptStdErr();
		int Counter=0;
		for(Counter=(TotalFileInput-(ValueToPredect+1));Counter<(TotalFileInput-1);Counter++)
		{
			double AR = Intercept+(data[Counter][1]*Slope)+StandardError;
			writer.append("\t");
			writer.append(Integer.toString((int)Math.round(AR)));
		}
		writer.append(System.lineSeparator());
		writer.close();
	}
	/*
	 * Function forcasteTotal ()
	 * Function first calculates the sum of all the days and stores in array(data[118]) of 118 size.
	 * Separate out data in 2D matrix data_1[117][2] representing -> data_1(X,Y) 
	 * Function takes the array and calculates the slope, standardError and the intercept
	 * Formula for prediction output = intercept + (input data * slope) + standardError
	 * A loop from 0..29 is used to predict the next 29 values of of the individual item  
	 * Output is then appended to the output_2.txt
	 * Finally the output of output_1.txt and output_2.txt is appended to output.txt
	 * */
	public static void forcasteTotal() throws IOException{

		BufferedReader x = new BufferedReader(new FileReader("product_distribution_training_set.txt"));
		int[] fileInput = new int[TotalFileInput];
		int[] data = new int[TotalFileInput];
		double[][] data_1 = new double[TotalFileInput][2]; 
		int fileInputCnt=0;
		String Line = x.readLine();

		int dataCntX=0;
		for (int i = 0; i < ValueToPredect; i++) {
			data[i] = 0;
		}
		try 
		{
			while(Line != null)
			{
				fileInputCnt = 0;
				StringTokenizer St = new StringTokenizer(Line);
				Integer.parseInt(St.nextToken());

				while(St.hasMoreTokens()) {
					fileInput[fileInputCnt++] = Integer.parseInt(St.nextToken());
				}
				dataCntX=0;
				for(int i=0;i<(fileInputCnt);i++)
				{
					data[dataCntX++] += fileInput[i];
				}
				Line = x.readLine();
			}
		}catch (Exception e) {
			System.out.println(e);
		}
		x.close();
		int cnt=0;
		while(cnt< (data.length-1))
		{
			data_1[cnt][0] = data[cnt];
			cnt++;
		}
		cnt = 0;
		while(cnt < (data.length-1))
		{
			data_1[cnt][1] = data[cnt+1];
			cnt++;
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter("output_2.txt", true));
		writer.append(Integer.toString(0));

		SimpleRegression test = new SimpleRegression();
		test.addData(data_1);
		double Intercept = test.getIntercept();
		double Slope = test.getSlope();
		double StandardError = test.getInterceptStdErr();
		int Counter=0;
		for(Counter=(TotalFileInput-(ValueToPredect+1));Counter<(TotalFileInput-1);Counter++)
		{
			double AR = Intercept+(data_1[Counter][1]*Slope)+StandardError;
			writer.append("\t");
			writer.append(Integer.toString((int)Math.round(AR)));
		}
		writer.append(System.lineSeparator());
		writer.close();

		FileInputStream s = new FileInputStream("output_1.txt");
		FileInputStream d = new FileInputStream("output_2.txt");
		SequenceInputStream sis = new SequenceInputStream(d,s);
		FileOutputStream o = new FileOutputStream("output.txt");
		int c1;
		while((c1=sis.read())!=-1)
		{
			o.write(c1);	
		}
		o.close();
		sis.close();
		d.close();
		s.close();
		File f = new File("output_1.txt");
		File f1 = new File("output_2.txt");
		f.delete();
		f1.delete();	
	}

}
