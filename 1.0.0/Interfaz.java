/*/////////////////////////////////////////////////////////////////////////////////////
CalAr calcula sonidos parciales a partir de una nota elegida y muestra sus frecuencias
y el corrimiento de afinación del sonido parcial con respecto a la altura temperada
gráficamente sobre el teclado, en todos aquellos casos en que el error de afinación
sea superior al 1%.
/////////////////////////////////////////////////////////////////////////////////////*/
import javax.swing.JComponent;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JSlider;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Soundbank;
import javax.sound.midi.MidiUnavailableException;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.Math;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.net.URL;

/*/////////////////////////////////////////////////////////////////////////////////////
La clase interfaz construye la ventana del programa y contiene todos los métodos
necesarios para su funcionamiento.
/////////////////////////////////////////////////////////////////////////////////////*/
class Interfaz implements ActionListener {

	//Variables globales
	Color negro = new Color(45, 45, 45);
	Color blanco = new Color(255, 255, 255);
	Color rojo = new Color(190, 40, 40);
	Color azul = new Color(90, 140, 190);
	JTextField fundamental = new JTextField("");
	JTextField listaAr[] = new JTextField[32];
	JLabel n[] = new JLabel[32];
	JButton bNotas[][] = new JButton[9][12];
	JLabel octava[] = new JLabel[9];
	JLabel armonicos[] = new JLabel[32];
	JButton borrar = new JButton();
	JButton verMemorias = new JButton();
	JSlider slider;
	int posicionMemoria = 0;
	int posicionMemoriaVer = 0;
	int contadorEjecutar = 0;
	int historial[][] = new int[12][3];
	boolean hayInstrumentos = true;
	boolean mostrandoMemoria = false;
	String idioma[] = new String[6];
	Font classInfoFont;
	Font dataObtenidaFont;
	JLayeredPane lp;
	Synthesizer sint;
	MidiChannel canal;
	Double doCero = 16.351598;
	Double frecuenciaNota = 0.0;
	DecimalFormat z = new DecimalFormat("#,###,###.00");
	
	/*//////////
	Constructor
	/////////*/
	Interfaz(){
		
		//Construcción del sintetizador
		try {
			sint = MidiSystem.getSynthesizer();
			sint.open();
         	//Confirmación de la existencia de un banco de sonidos.
         	if (sint.getDefaultSoundbank().getInstruments() == null){
         		hayInstrumentos = false;
         	} else {         		
				canal = sint.getChannels()[0];
         	}
      	} catch (MidiUnavailableException e) {}

		getIdioma(System.getProperty("user.language"));
		
		construirVentana(System.getProperty("os.name"));
      	
	}
	
	
	/*///////////////////////////////////////////////////////
	Construcción y métodos de gestión de la interfaz gráfica.
	///////////////////////////////////////////////////////*/
	
	//Construcción de la ventana, agregado del panel principal.
	void construirVentana(String os) {
		
		JFrame v = new JFrame("CalAr");
		v.setDefaultCloseOperation(3);
		
		v.setResizable(false);
		v.setLocationRelativeTo(null);
		
		//Definiendo formatos según sistema operativo
		if (os.startsWith("Windows")){
			classInfoFont = new Font("sansserif", Font.PLAIN, 12);
			dataObtenidaFont = new Font("monospace", Font.BOLD, 14);
		} else {
			classInfoFont = new Font("sansserif", Font.PLAIN, 12);
			dataObtenidaFont = new Font("monospace", Font.PLAIN, 14);
		}
        
        //Icono para windows y linux
        if (os.startsWith("Windows") || os.startsWith("Linux")){
	    	v.setSize(935, 500);
	    	URL iconoUrl = getClass().getResource("Icono.png");
			if (iconoUrl != null){
				ImageIcon icono = new ImageIcon(iconoUrl);
				v.setIconImage(icono.getImage());
			}
		} else {
			v.setSize(935, 550);
		}
		
		/*Inicialización de botones y etiquetas del teclado para poder ejecutar
		los métodos que los configuran.*/	
		for (int o = 0; o < bNotas.length; o++){
			octava[o] = new JLabel();
			for (int i = 0; i < bNotas[o].length; i++){
				bNotas[o][i] = new JButton();
			}
		}
		
		for (int i = 0; i < armonicos.length; i++){
			armonicos[i] = new JLabel();
			armonicos[i].setOpaque(true);
		}
						
		v.setLocationRelativeTo(null);
		v.add(construirPanel());
		v.setVisible(true);

	}
	
	//Construcción del panel principal.
	JPanel construirPanel(){
	
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));		
		p.add(Box.createRigidArea(new Dimension (0, 10)));
		p.add(construirpTeclado());
		p.add(Box.createRigidArea(new Dimension (0, 10)));
		p.add(construirpResultados());
		p.add(Box.createRigidArea(new Dimension (0, 10)));
		
		return p;

	}
	
	//Construcción del panel que aloja el teclado.
	JPanel construirpTeclado(){
	
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(10, 0)));
		
		lp = new JLayeredPane();
		lp.setOpaque(true);
        lp.setBorder(BorderFactory.createTitledBorder(idioma[0]));
        
		Font numeroOctava = new Font("sansserif", Font.PLAIN, 11);
		for (int o = 0; o < octava.length; o++){
			octava[o].setText(String.valueOf(o));
			octava[o].setLocation((98 * o) + 18, 20);
			octava[o].setOpaque(true);
			octava[o].setSize(20, 20);
			octava[o].setVisible(true);
			octava[o].setFont(numeroOctava);
			lp.add(octava[o], new Integer(1));
		}
		
		construirTeclado();
		construirArmonicos();
		desactivarArmonicos();
        
		p.add(lp);
		p.add(Box.createRigidArea(new Dimension(10, 0)));
	
		return p;
		
	}
	
	//Construcción del panel que mostrará los resultados.
	JPanel construirpResultados(){
	
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(10, 0)));	
		p.add(panelR());
		p.add(Box.createRigidArea(new Dimension(10, 0)));
	
		return p;
		
	}
	
	//Construcción de los paneles que integran el panel segundaNotas.
	JPanel panelR(){
	
		JPanel p = new JPanel();
		Dimension d = new Dimension(0, 5);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(idioma[1]));
		p.add(Box.createRigidArea(d));
		p.add(panelRx());
		p.add(Box.createRigidArea(d));
		p.add(panelCI());
		p.add(Box.createRigidArea(d));
			
		return p;
	
	}
	
	JPanel panelRx(){
	
		JPanel p = new JPanel();
		Dimension d = new Dimension(5, 0);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(d));
		p.add(panelRxA());
		p.add(Box.createRigidArea(new Dimension(40, 0)));
		p.add(panelRxB());
		p.add(Box.createRigidArea(d));
			
		return p;
	
	}
	
	JPanel panelRxA(){
	
		JPanel p = new JPanel();
		Dimension d = new Dimension(0, 10);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setMaximumSize(new Dimension(300, 200));
		
		p.add(Box.createRigidArea(new Dimension(0, 20)));
		p.add(panelF());
		p.add(Box.createRigidArea(d));
		p.add(panelS());
		p.add(Box.createRigidArea(d));
		p.add(panelB());
		p.add(Box.createRigidArea(d));
		
		return p;
	
	}
	
	JPanel panelRxB(){
	
		JPanel p = new JPanel();
		Dimension d = new Dimension(0, 5);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		p.add(Box.createRigidArea(d));
		p.add(panelGrillaAr());
		p.add(Box.createRigidArea(d));
			
		return p;
	
	}
	
	JPanel panelF(){
	
		JPanel p = new JPanel();
		p.setOpaque(true);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setMaximumSize(new Dimension(270, 25));
		
		JLabel fundamentallabel = new JLabel(idioma[2], JLabel.CENTER);
		fundamentallabel.setVisible(true);
		
		fundamental.setVisible(true);
		fundamental.setEditable(false);
		fundamental.setHorizontalAlignment(JTextField.CENTER);
		fundamental.setBackground(blanco);
		fundamental.setFont(dataObtenidaFont);
		
		Dimension d = new Dimension(5, 0);
		
		p.add(fundamentallabel);
		p.add(Box.createRigidArea(d));
		p.add(fundamental);
	
		return p;
	
	}
	
	JPanel panelS(){
	
		JPanel p = new JPanel();
		p.setOpaque(true);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setBorder(BorderFactory.createTitledBorder(idioma[3]));
		p.setPreferredSize(new Dimension(200, 80));
		
		slider = new JSlider(JSlider.HORIZONTAL, 4, 32, 8);
		slider.setVisible(true);
		slider.setMajorTickSpacing(4);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
	
		p.add(slider);
	
		return p;
	
	}
	
	JPanel panelB(){
	
		JPanel p = new JPanel();
		p.setOpaque(true);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		verMemorias.setText(idioma[4]);
		verMemorias.setVisible(true);
		verMemorias.addActionListener(this);

		borrar.setText(idioma[5]);
		borrar.setVisible(true);
		borrar.addActionListener(this);

		Dimension d = new Dimension(10, 0);
		
		p.add(Box.createRigidArea(d));
		p.add(verMemorias);
		p.add(Box.createRigidArea(d));
		p.add(borrar);
	
		return p;
	
	}
	
	JPanel panelGrillaAr(){
	
		JPanel p = new JPanel();
		p.setOpaque(true);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		JPanel a[] = panelAr();		
		JPanel g[] = new JPanel[4];
		Dimension d = new Dimension(0,1);
		
		for (int i = 0; i < g.length; i++){
			
			g[i] = new JPanel();
			g[i].setOpaque(true);
			g[i].setLayout(new BoxLayout(g[i], BoxLayout.Y_AXIS));
			
			g[i].add(a[0 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			g[i].add(a[1 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			g[i].add(a[2 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			g[i].add(a[3 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			g[i].add(a[4 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			g[i].add(a[5 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			g[i].add(a[6 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			g[i].add(a[7 + (8*i)]);
			g[i].add(Box.createRigidArea(d));
			
		}
		
		Dimension dd = new Dimension(4, 0);
		p.add(g[0]);
		p.add(Box.createRigidArea(dd));
		p.add(g[1]);
		p.add(Box.createRigidArea(dd));
		p.add(g[2]);
		p.add(Box.createRigidArea(dd));
		p.add(g[3]);
		p.add(Box.createRigidArea(dd));
		
		return p;
		
	}
	
	JPanel panelCI(){
	
		JPanel p = new JPanel();
		p.setOpaque(true);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		
		JLabel classInfo = new JLabel("<html><div align='center'>http://sourceforge.net/projects/calar<br>Rodrigo Valla</div><html>", JLabel.CENTER);
	
		p.add(classInfo);
	
		return p;
	
	}
	
	JPanel[] panelAr(){
	
		JPanel p[] = new JPanel[32];
		for (int i = 0; i < p.length; i++){
			p[i] = new JPanel();
			p[i].setOpaque(true);
			p[i].setLayout(new BoxLayout(p[i], BoxLayout.X_AXIS));
			
			Dimension d = new Dimension(120, 30);
			
			if (i < 8) {
				p[i].setMaximumSize(new Dimension(110, 30));
			} else {
				p[i].setMaximumSize(d);
			}
			
			n[i] = new JLabel();
			if (i == 8){
				n[i].setText("  " + (i + 1));
			} else {
				n[i].setText(String.valueOf(i + 1));
			}	
			
			p[i].add(n[i]);
			p[i].add(Box.createRigidArea(new Dimension(2, 0)));
			
			listaAr[i] = new JTextField();
			listaAr[i].setVisible(true);
			listaAr[i].setEditable(false);
			listaAr[i].setHorizontalAlignment(JTextField.RIGHT);
			listaAr[i].setBackground(blanco);
			listaAr[i].setFont(classInfoFont);
			
			p[i].add(listaAr[i]);
			
		}
		
		return p;
	
	}
	
	void borrarListaAr(){
	
		for (int i = 0; i < listaAr.length; i++){
			listaAr[i].setText("");
		}
	
	}
	
	void getIdioma(String s){
	
		if (s.equals("es")){
			idioma[0] = "Teclado";
			idioma[1] = "Análisis";
			idioma[2] = "Fundamental:";
			idioma[3] = "Cantidad de armónicos";
			idioma[4] = "Historial";
			idioma[5] = "Borrar";
		} else {
			idioma[0] = "Keyboard";
			idioma[1] = "Analysis";
			idioma[2] = "Fundamental:";
			idioma[3] = "Harmonic amount";
			idioma[4] = "History";
			idioma[5] = "Erase";
		}	
		
	}
	
	/*/////////////////////////////////////////////////
	Métodos para la construcción y gestión del teclado.
	/////////////////////////////////////////////////*/
	void construirTeclado(){
	
		for (int o = 0; o < bNotas.length; o ++){
			for (int i = 0; i < bNotas[o].length; i++){
			
       			bNotas[o][i].setOpaque(true);
   				bNotas[o][i].setBorder(null);
				bNotas[o][i].setBorderPainted(false);
				bNotas[o][i].addActionListener(this);
			
				//Diferenciación del tamaño de las teclas.
				if (esTeclaNegra(i) == true){			
					bNotas[o][i].setSize(6, 35);
				} else {
					bNotas[o][i].setSize(12, 60);
				}
			
	        	pintarTeclado();
				posicionarTeclado();
	        	bNotas[o][i].setVisible(true);
	        	
        	}	
		}
		
		for (int o = 0; o < bNotas.length; o++){
        	for (int i = 0; i < bNotas[o].length; i++){
        
        		if (esTeclaNegra(i) == true){
        			lp.add(bNotas[o][i], new Integer(2));
        		} else {
        			lp.add(bNotas[o][i], new Integer(1));
       		 	}
		
			}        
        }
		
	}
	
	//Método para pintar el teclado a blanco y negro.
	void pintarTeclado(){
	
		for (int o = 0; o < bNotas.length; o ++){
			for (int i = 0; i < bNotas[o].length; i++){
			
				if (esTeclaNegra(i) == true){			
					bNotas[o][i].setBackground(negro);
				} else {
					bNotas[o][i].setBackground(blanco);
				}
			
			}
		}
	
	}
	
	/* Método que posiciona las teclas (pnicial corresponde al margen con respecto al
	borde del panel, ajuste permite corregir el error que se produce cuando aparecen
	dos teclas blancas consecutivas, ajusteNegras permite centrar las teclas negras con
	respecto a las blancas y ajusteOctava ubica las octavas en forma sucesiva.*/
	void posicionarTeclado(){
	
		int pinicial = 16;
		int ajuste = 0;
		int ajusteNegras = 3;
		int ajusteOctava = 98;
		
		for (int o = 0; o < bNotas.length; o++){
			
			ajuste = 0;
			
			for (int i = 0; i < bNotas[o].length; i++){
			
				if (i >= 5){
					ajuste = 7;
				}
			
				if (esTeclaNegra(i) == true){			
					bNotas[o][i].setLocation(pinicial + (7 * i) + ajuste + (ajusteOctava * o) + ajusteNegras, 40);
				} else {
					bNotas[o][i].setLocation(pinicial + (7 * i) + ajuste + (ajusteOctava * o), 40);
				}
			
			}
		}
	
	}

	//Método para decidir si el botón corresponde a una tecla blanca o negra.
	boolean esTeclaNegra(int i){
	
		boolean esTN = false;
		if (i == 1 || i == 3 || i == 6 || i == 8 || i == 10){
			esTN = true;
		}
		return esTN;
	
	}
	
	//Métodos para activar y desactivar el teclado.
	void activarTeclado(){
		for (int o = 0; o < bNotas.length; o++){
			for (int i=0; i < bNotas[o].length; i++){
				bNotas[o][i].setEnabled(true);
			}
		}
	}

	void desactivarTeclado(){
		for (int o = 0; o < bNotas.length; o++){
			for (int i=0; i < bNotas[o].length; i++){
				bNotas[o][i].setEnabled(false);
			}
		}
	}
	
	/*//////////////////////////////////////////////////////////////////////
	Métodos para la construcción y gestión de los indicadores de armónicos.
	//////////////////////////////////////////////////////////////////////*/
	Color colores[] = new Color[16];
	
	//Método para configurar los indicadores.
	void construirArmonicos(){
	
		for (int i = 0; i < armonicos.length; i++){
			
			//Se establecen distintos tamaños porque algunos se superpondrán.
			if (i < 8) {
				armonicos[i].setSize(6, 40);
				lp.add(armonicos[i], new Integer(4));
				armonicos[i].setLocation(20, 95);
			} else if (i > 7 && i < 16){
				if (i%2 == 0){
					armonicos[i].setSize(6, 60);
					lp.add(armonicos[i], new Integer(3));
					armonicos[i].setLocation(20, 90);
				} else {
					armonicos[i].setSize(6, 40);
					lp.add(armonicos[i], new Integer(4));
					armonicos[i].setLocation(20, 95);
				}	
			} else {
				if (i%2 == 0){
					armonicos[i].setSize(6, 60);
					lp.add(armonicos[i], new Integer(3));
					armonicos[i].setLocation(20, 90);
				} else {
					if ((i - 1)%4 == 0){
						armonicos[i].setSize(6, 80);
						lp.add(armonicos[i], new Integer(2));
						armonicos[i].setLocation(20, 85);
					} else {
						armonicos[i].setSize(6, 40);
						lp.add(armonicos[i], new Integer(4));
						armonicos[i].setLocation(20, 95);
					}
				}
			}
		
		}
		
		pintarArmonicos();
	
	}
	
	//Método para asignar distintos colores a distintas notas.
	void pintarArmonicos(){
	
		prepararColores();
		
		int c = 0;
		for (int i = 0; i < armonicos.length; i++) {
			if ((i + 1)%2 == 1){
				armonicos[i].setBackground(colores[c]);
				c = c + 1;
			} else {
				armonicos[i].setBackground(armonicos[i/2].getBackground());
			}
		}
	
	}
	
	//Método para hacer visibles o no visibles los indicadores.
	void activarArmonicos(int l){
		for (int i = 0; i < l; i++){
			armonicos[i].setVisible(true);
		}
	}
	void desactivarArmonicos(){
		for (int i = 0; i < armonicos.length; i++){
			armonicos[i].setVisible(false);
		}
	}
	
	//Método que instancia los distintos colores.
	void prepararColores(){
		colores[0] = new Color(200, 20, 25);
		colores[3] = new Color(160, 100, 10);
		colores[2] = new Color(220, 220, 0);
		colores[1] = new Color(0, 110, 50);
		colores[4] = new Color(0, 120, 160);
		colores[5] = new Color(0, 50, 110);
		colores[9] = new Color(100, 5, 100);
		colores[7] = new Color(160, 0, 90);
		colores[8] = new Color(240, 100, 30);
		colores[6] = new Color(140, 200, 60);
		colores[10] = new Color(0, 160, 240);
		colores[11] = new Color(250, 170, 90);
		colores[12] = new Color(120, 70, 30);
		colores[13] = new Color(80, 70, 60);
		colores[14] = new Color(160, 210, 150);
		colores[15] = new Color(120, 200, 200);
	}
	
	/*Método que recibe un arreglo con las frecuencias de los armónicos y posiciona
	los indicadores en el teclado.*/
	void posicionarArmonicos(double f[]){
	
		desactivarArmonicos();
		
		double n[] = logT(f);
		
		for (int i = 0; i < n.length; i++){
			armonicos[i].setLocation(coordenadaX(n[i]), armonicos[i].getY());
		}
		
		activarArmonicos(n.length);
			
	}
	
	/*Método que recibe el corrimiento hacia la derecha para un armónico y devuelve
	la coordenada horizontal que le corresponde.*/
	int coordenadaX(double x){
	
		int coordenada = 0;
		double c = 0.0;
		int pinicial = 19;
		int nota = (int) Math.round(x%12);
		if (nota == 12){
			nota = nota - 12;
		} 
		
		//8.17 es una constante relacionada con el ancho de la octava.
		c = (x*8.17);
		
		int correccion = 0;
		if (nota >= 3 && nota < 5){
			correccion = -1;
		} else if (nota >= 5 && nota <= 8){
			correccion = 6;
		} else if (nota > 7){
			correccion = 5;
		}
		
		coordenada = ((int) Math.round(c)) + pinicial - nota + correccion;
		
		return coordenada;
		
	}
	
	/* Método que devuelve un arreglo que indica el corrimiento hacia la derecha para
	cada armónico con respecto a la primera nota del teclado, filtrando aquellas que
	quedan fuera de su rango.*/
	double[] logT (double f[]){
		
		int c = f.length;
		int d = 1;
		while (f[f.length - d] > 8000){
			c = c - 1;
			d = d + 1;
		}
		
		double t = Math.pow(2, 1.0/12.0);
		double n[] = new double[c];
		
		for(int i = 0; i < n.length; i++){
			n[i] = (Math.log(f[i]/doCero)/Math.log(t));
		}
		
		return n;
		
	}
	
	/*//////////////////////////////////////////
	Métodos para el funcionamiento del programa
	//////////////////////////////////////////*/
	public void actionPerformed (ActionEvent ae) {
	
		for (int o = 0; o < bNotas.length; o++){
			for (int i=0; i < bNotas[o].length; i++ ){
			
				if(ae.getSource() == bNotas[o][i]){
			
					frecuenciaNota = doCero * Math.pow(Math.pow(2, 1.0/12.0), i) * Math.pow(2, o);
					
					pintarTeclado();
					if (esTeclaNegra(i) == true){			
						bNotas[o][i].setBackground(azul);
					} else {
						bNotas[o][i].setBackground(rojo);
					}
				
					fundamental.setText(z.format(frecuenciaNota) + " Hz");
					ejecutar(frecuenciaNota, slider.getValue());
					
					if (hayInstrumentos == true){
							playN((12 * o) + 12 + i);
					}
					
					if(mostrandoMemoria == false){
						historial[posicionMemoria][0] = o;
						historial[posicionMemoria][1] = i;
						historial[posicionMemoria][2] = slider.getValue();
						posicionMemoriaVer = contadorEjecutar;
						gestionarPosicionMemoria();
       				}
				
				}
						
			}
		}
		
	
		if(ae.getSource() == borrar) {
			
			fundamental.setText("");
			pintarTeclado();
			borrarListaAr();
			desactivarArmonicos();
			if (mostrandoMemoria == true){
				mostrandoMemoria = false;
				activarTeclado();
			}
			
		}
		
		if(ae.getSource() == verMemorias){
			
			pintarTeclado();

			if (0 < contadorEjecutar && contadorEjecutar < 12) {
				
				if(0 < posicionMemoriaVer){
					mostrarMemoria(posicionMemoriaVer);
					posicionMemoriaVer = posicionMemoriaVer - 1;
				} else {
					mostrarMemoria(posicionMemoriaVer);
					posicionMemoriaVer = contadorEjecutar - 1;
				}

			} else if (11 < contadorEjecutar){

				if (posicionMemoriaVer < 12){
					posicionMemoriaVer = posicionMemoriaVer + 12;
				}
			
				mostrarMemoria(posicionMemoriaVer - 12);
				posicionMemoriaVer = posicionMemoriaVer - 1;

			} else {
			
			}
			
		}
	
	}
	
	
	
	/*///////////////////////////////////
	Métodos para al producción de sonido.
	///////////////////////////////////*/
	
	//Método para hacer sonar las teclas.
	void playN(int n){
	
		canal.noteOn(n, 100);
       	try {
       		Thread.sleep(10);
           	} catch (InterruptedException e) {
          
           	} finally {
           	canal.noteOff(n);
           	}
        
    }
    
    /*////////////////////////////////
	Gestión del historial de análisis
	////////////////////////////////*/
	void mostrarMemoria(int x){
			
		if (mostrandoMemoria == false){
			mostrandoMemoria = true;
			desactivarTeclado();
		}
		bNotas[historial[x][0]][historial[x][1]].setEnabled(true);
		slider.setValue(historial[x][2]);
		bNotas[historial[x][0]][historial[x][1]].doClick();
		bNotas[historial[x][0]][historial[x][1]].setEnabled(false);

	}

	void gestionarPosicionMemoria(){
		
		if (contadorEjecutar == 24){
			contadorEjecutar = contadorEjecutar - 11;
		} else {
			contadorEjecutar = contadorEjecutar + 1;
		}
		
		if (posicionMemoria == 11){
				posicionMemoria = 0;
			} else {
				posicionMemoria = posicionMemoria + 1;
		}
			
	}
	
	/*///////////////////
	Gestión del análisis
	///////////////////*/
	void ejecutar(double f, int c){
	
		double a[] = frecuenciaArmonicos(f, c);
		listaArmonicos(a);
		posicionarArmonicos(a);
		
	}
	
	//Método que recibe la lista de armónicos y los muestra en los cuadros de texto.
	void listaArmonicos(double f[]) {
		
		borrarListaAr();
		for (int i = 0; i < f.length; i++){
			listaAr[i].setText(z.format(f[i]) + "Hz");
		}
		
	
	}
	
	/*Método que recibe fundamental y cantidad de armónicos pedidos y devuelve un
	arreglo con las frecuencias correspondientes, filtrando aquellas que quedan
	fuera del rango audible.*/
	double[] frecuenciaArmonicos(double f, int c){
		
		while (f*c > 20000){
			c = c-1;
		}
		
		double a[] = new double[c];
		
		for (int i = 0; i < a.length; i++){
			a[i]= f*(i+1);
		}
	
		return a;
		
	}

}