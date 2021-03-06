import java.io.*;
import java.util.Arrays;

//Program to list natural harmonics of string's instruments
class HarmonicCounter {
	
	static int notes[];
	
	public static void main(String[] args) throws Exception{
		
		notes = new int[127];
		
		System.out.println();
		System.out.println("////////////////////////////////////////////////////");
		System.out.println("		NATURAL HARMONICS COUNTER");
		System.out.println("////////////////////////////////////////////////////");		
		System.out.println();
		System.out.println("Type 'number of harmonics (1-16)' space 'Midi note of");
		System.out.println("string 1' space 'Midi note of string 2'...");
		System.out.println();
		
		HarmonicCounter();
		
	}
	
	public static void HarmonicCounter() throws Exception{
	
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		for (int i = 0; i < notes.length; i++){
			notes[i] = 0;
		}

		String parameters;
		parameters = br.readLine();
		String values [] = parameters.split(" ");
		System.out.println();
		
		int p[] = new int[values.length];
		
		try	{
			for (int i = 0; i < p.length; i++){
				p[i] = Integer.parseInt(values[i]);
			}
			if (p[0] > 16){
				System.out.println();
				System.out.println("'" + parameters + "' IS NOT VALID");
				System.out.println("Retry:");
				System.out.println();
			} else {
				evalHarmonicList(getTotalHarmonics(p));
				printData(notes);
			}
		} catch (NumberFormatException e){
				System.out.println();
				System.out.println("'" + parameters + "' IS NOT VALID");
				System.out.println("Retry:");
				System.out.println();
				HarmonicCounter();
		}		
		
		exit();
	
	}
	
	static void printData(int[] n){
	
		for (int i = 0; i < n.length; i++){
			if (n[i] > 0){
				System.out.print("(" + i + ", " + n[i] + "); "); 
			}
		}
	
		System.out.println();
	
	}
	
	static void evalHarmonicList(int[][] m){

		for (int i = 0; i < m.length; i++){
			for (int o = 0; o < m[i].length; o++){
				if (m[i][o] < 128){
					notes[m[i][o]] = notes[m[i][o]] + 1;
				}
			}
		}
	
	}
	
	
	static int[][] getTotalHarmonics (int[] p){
	
		int harmonicMatrix[][] = new int[p.length - 1][p[0]];
		
		for (int i = 0; i < harmonicMatrix.length; i++){
			harmonicMatrix[i] = getStringHarmonics(p[i + 1], p[0]);
			System.out.println(getText(harmonicMatrix[i]));
		}
	
		System.out.println();
		return harmonicMatrix;
	
	}
	
	static int[] getStringHarmonics (int f, int c){
	
		int harmonicList[] = new int[c];
		int ar[] = new int[]{0, 12, 19, 24, 28, 31, 34, 36, 38, 40, 42, 43, 45, 46, 47, 48};
		
		for (int i = 0; i < harmonicList.length; i++){
			harmonicList[i] = f + ar[i];
		}

		return harmonicList;
		
	}
	
	//Method to get strings from arrays of integers
	static String getText(int[] x){
		
		String conjuntoAlturas = new String();

		for (int i=0; i < x.length ; i++){	
			conjuntoAlturas += String.valueOf(x[i]);
			if (i < x.length - 1){
				conjuntoAlturas += " ";
			}
		}
		
		return conjuntoAlturas;
	
	}
	
	//Method for exit the program...
	public static void exit () throws Exception{
		
		InputStreamReader isr2 = new InputStreamReader(System.in);
		BufferedReader br2 = new BufferedReader(isr2);

		System.out.println();
		System.out.println("To run program again: 'n'");
		String ex;
		ex = br2.readLine();
		if (ex.equals("n")) {
			System.out.println(); 
			System.out.println(); 			
			HarmonicCounter();
		} else {
			System.exit(0);
		}
	
	}

}