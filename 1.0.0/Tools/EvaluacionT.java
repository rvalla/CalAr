/*//////////////////////////////////////////////////////////////////////////////////////
EvaluacionT es un programa que permite evaluar el desempeño de CalAr desde la terminal.
Muestra para cada parcial: la frecuencia de la nota temperada más cercana, su frecuencia,
la diferencia entre ambas en Hz y el porcentaje que representa el error.
//////////////////////////////////////////////////////////////////////////////////////*/

import java.io.*;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.Arrays;

class EvaluacionT {

	static DecimalFormat z = new DecimalFormat("#,##0,000.00");
	static DecimalFormat p = new DecimalFormat("##0.00");
	static double f = 0.0;
	//Arreglo que establece las alturas temperadas usadas para realizar la comparación.
	static int ar[] = new int[]{0, 12, 19, 24, 28, 31, 34, 36, 38, 40, 42, 43, 45, 46, 47, 48,
						49, 50, 51, 52, 53, 54, 54, 55, 56, 57, 57, 58, 58, 59, 59, 60};
	
	public static void main (String[] args) throws Exception {
	
		EvaluacionT();	
	
	}
	
	public static void EvaluacionT() throws Exception {
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);

		//Se pide al usuario la frecuencia para una fundamental
		System.out.println("Ingrese la frecuencia para la fundamental (en Hz):");
		String f1fr;
		f1fr = br.readLine();
		
		//Se captura el error en caso de ingreso inválido
		try	{f = (Double.parseDouble(f1fr));
		} catch (NumberFormatException e){
				System.out.println();
				System.out.println("'" + f1fr + "' NO PUEDE PROCESARSE");
				System.out.println("Introducir valor en Hz");
				System.out.println();
				EvaluacionT();
		}
		
		if (f>8000) {
			System.out.println();	
			System.out.println("Valor fuera de rango");
			System.out.println("Rango: 25Hz <= fundamental <= 8000Hz");
			System.out.println();
			EvaluacionT();
			} else if (f<25){
				System.out.println();	
				System.out.println("Valor fuera de rango");
				System.out.println("Rango: 25Hz <= fundamental <= 8000Hz");
				System.out.println();
				EvaluacionT();
			} else {
				System.out.println(ar.length);
				calcular(f);
		}

		exit();

	}
		
	public static void calcular(double f){
	
		double frT[] = new double[32];
		double frN[] = new double[32];
	
		for (int i = 0; i < frT.length; i++){
			frT[i] = f * Math.pow(Math.pow(2, 1.0/12.0), ar[i]);
			frN[i] = f * (i + 1);
		}
		
		System.out.println();
		System.out.println("	FrT		FrA		Dif		%");
		
		for (int i = 0; i < frT.length; i++){
			System.out.print((i + 1) + "	");
			System.out.print(z.format(frT[i]) + "	");
			System.out.print(z.format(frN[i]) + "	");
			
			double dif = frN[i] - frT[i];
			
			System.out.print(p.format(dif) + "		");
			
			double por = (dif * 100)/frT[i];
			
			System.out.println(p.format(por));

		} 
	
	}
	
	public static void exit () throws Exception{
		
		InputStreamReader isr2 = new InputStreamReader(System.in);
		BufferedReader br2 = new BufferedReader(isr2);

		System.out.println();
		System.out.println("Para correr el programa de nuevo tipear 'n'");
		String ex;
		ex = br2.readLine();
		if (ex.equals("n")) {
			System.out.println(); 
			System.out.println(); 			
			EvaluacionT();
		} else {
			System.exit(0);
		}
	
	}

}