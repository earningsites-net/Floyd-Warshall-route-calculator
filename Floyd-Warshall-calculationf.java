/*    Per l'implementazione del progettino ho deciso di utilizzare come cuore dell'algoritmo la
	procedura Floyd-Warshall. Ritengo conveniente utilizzare un algoritmo che calcoli
	i cammini minimi tra tutte le coppie piuttosto che una procedura che calcoli i cammini
	minimi da una sorgente singola verso il resto dei nodi. Sebbene infatti il tipo di procedura
	da me	prescelto richieda un tempo di esecuzione asintoticamente maggiore rispetto ad esempio
	all'algoritmo Dijkstra, deve essere chiamata una sola volta per tutte le interrogazioni
	che l'utente effettuerà, e non una volta per ogni diversa sorgente.
	Inoltre la procedura Floyd-Warshall è a mio avviso la preferibile	nel suo genere per la sua
	struttura compatta e relativamente semplice.
	
	Ho implementato tre diversi grafi mediante la rappresentazione in memoria di tre differenti
	matrici di adiacenza.
	Ho quindi utilizzato le tre matrici W1, W2 e W3 con le relative matrici dei predecessori P1, P2
	e P3. In particolare:
	W1: rappresenta la matrice di adiacenza relativa ai COSTI dei collegamenti tra le città
	W2: rappresenta la matrice di adiacenza relativa ai TEMPI dei collegamenti tra le città
	W3: rappresenta la matrice di adiacenza per individuare il percorso più breve.
	Va notato che i primi due grafi sono dei grafi orientati in quanto il collegamento tra due
	diverse città c[i] -> c[j] può essere differente per costo o per tempo di percorrenza dal
	collegamento c[j] ->c[i].
	D'altra parte nella matrice W3 tutti gli archi hanno peso pari a 1, e poichè se la città c[i]
	è raggiungibile dalla città c[j] si suppone che la città c[j] sia raggiungibile da c[i], 
	il terzo grafo è un grafo non orientato. 
	Un'interrogazione sulla matrice W3 restituisce quindi	semplicemente il minimo numero di archi 
	che collega la città c[i] e la città c[j] in entrambi i versi, il che non significa che siano
	stati necessariamente impostati i collegamenti per tempo o per costo di c[i] -> c[j] e c[j] -> c[i], 
	e quindi che esista un cammino minimo per tempo o per costo in entrambi i versi.
	Se comunque questa impostazione non fosse richiesta dall'esercitazione basterebbe rimuovere una linea
	nel corpo dell'evento Imposta (che indicherò di seguito).

	DUE RIGHE SULL'UTILIZZO DEL PROGRAMMA....
	Fase 1:
	Innanzi tutto sarà necessario inserire il numero di città, numero che deve essere ovviamente
	positivo; successivamente sarà possibile inserire tutti i nomi delle città. Non si può inserire
	due volte la stessa città.

	Fase 2:
	Completata questa operazione si potrà impostare gli archi: i dati andranno inseriti nei due
	appositi Textfields. Più precisamente:
	- Nel primo TextField andranno inseriti, in quest'ordine, il nome della città di PARTENZA e quello
	  della città di ARRIVO per i quali si vuole creare un collegamento, SEPARATI DA UNO SPAZIO
	- Nel secondo TextField andranno inseriti, in quest'ordine, il costo ed il tempo di percorrenza
	  del collegamento inserito nel TextField sovrastante, sempre SEPARATI DA UNO SPAZIO
	Non sono ammessi cappi in quanto una città non si può definire raggiungibile da se stessa ad un
	tempo o costo x.
	Terminata la fase di impostazione degli archi si clicca sul pulsante Termina.

	Fase 3:
	Adesso è possibile effettuare interrogazioni sul grafo.
	Anche se non richiesto dall'esercitazione ho implementato tre Button che restituiscono rispettivamente
	nell'outputfield al centro della finestra:
	- La matrice di adiacenza per visualizzare il percorso più breve tra due nodi e la relativa matrice
	  dei predecessori
	- La matrice di adiacenza relativa ai COSTI dei collegamenti tra le città e la relativa matrice dei
	  predecessori
	- La matrice di adiacenza relativa ai TEMPI dei collegamenti tra le città e la relativa matrice dei
	  predecessori
	Per visualizzare i tre percorsi (più breve, più veloce e meno costoso) è sufficiente completare i campi
	inserendo la città di partenza e quella di arrivo e cliccare sul pulsante Calcola Percorso.

*/

import java.awt.*;
import java.awt.event.*;

public class ProgettoGraf extends Frame implements ActionListener{

// Imposto i valori che rimarranno invariati in tutto il programma:
// WIDTH e HEIGHT per quanto riguarda le dimensioni della finestra, e
// i valori INF(inito) e NIL pari agli ultimi due numeri utilizzabili
// da una variabile Integer.

	public static final int INF = 2147483647;	
	public static final int NIL = 2147483646;
	public static final int WIDTH = 600;
	public static final int HEIGHT = 240;
	
// Imposto le variabili grafiche...
	
	private TextField	numCitta;
	private TextField Citta;
	private TextField arco1;
	private TextField arco2;
	private TextArea outputArea;
	private TextField cittaPartenza;
	private TextField cittaArrivo;
	Button Ok;
	Button Inserisci;
	Button Imposta;
	Button Termina;
	Button Breve;
	Button Veloce;
	Button Costosa;
	Button Calcola;
	Button Esci;
	Label nomeCittaLabel;

// ...e quelle relative all'algoritmo vero e proprio inizializzandole ad un valore di default.

	int n;
	String nome1;
	String nome2;
	int nodo1 = -1;
	int nodo2 = -1;
	int c;
	int t;
	int cont = 0;
	int scelta1;
	int scelta2;
	int[][] W1 = new int[n][n];
	int[][] W2 = new int[n][n];
	int[][] P1 = new int[n][n];
	int[][] P2 = new int[n][n];
	int[][] W3 = new int[n][n];
	int[][] P3 = new int[n][n];
	String[] nome = new String[n];	
	int flag;

	public static void main(String[]args){

		ProgettoGraf Progetto = new ProgettoGraf();
		Progetto.setVisible(true);
	}

	public ProgettoGraf(){
		
// Imposto innanzi tutto il Frame principale...

		setTitle("Progetto Complementi");
		setLayout(new BorderLayout());
		setSize(WIDTH, HEIGHT);
		addWindowListener(new WindowDestroyer());

// ...successivamente imposto il pannello che conterrà i comandi per impostare il grafo,
// abilitando inizialmente solo quelli che permettono di inserire il numero di città...

		Panel impostaPanel = new Panel();
		impostaPanel.setLayout(new GridLayout(0,1));
		impostaPanel.setBackground(Color.grey);
		Label numCittaLabel = new Label("Inserisci numero di città");
		impostaPanel.add(numCittaLabel);
		numCitta = new TextField(10);
		impostaPanel.add(numCitta);
		Ok = new Button("Ok");
		Ok.addActionListener(this);
		impostaPanel.add(Ok);
		nomeCittaLabel = new Label("Inserisci nome città 1");
		impostaPanel.add(nomeCittaLabel);
		Citta = new TextField(10);
		impostaPanel.add(Citta);
		Citta.setEnabled(false);
		Inserisci = new Button("Inserisci");
		Inserisci.addActionListener(this);
		impostaPanel.add(Inserisci);
		Inserisci.setEnabled(false);
		Label arcoLabel = new Label("Imposta arco");
		impostaPanel.add(arcoLabel);
		arco1 = new TextField(10);
		arco1.setText("Partenza Arrivo");
		impostaPanel.add(arco1);
		arco1.setEnabled(false);
		arco2 = new TextField(10);
		arco2.setText("Costo Tempo");
		impostaPanel.add(arco2);
		arco2.setEnabled(false);
		Imposta = new Button("Imposta");
		Imposta.addActionListener(this);
		impostaPanel.add(Imposta);
		Imposta.setEnabled(false);
		Termina = new Button("Termina");
		impostaPanel.add(Termina);
		Termina.addActionListener(this);
		Termina.setEnabled(false);

// ...e lo posiziono nella parte sinistra del frame...

		add(impostaPanel, "West");

// ...poi il pannello che conterrà l'area dove verrà visualizzato l'output, ponendolo
// al centro della finestra...

		Panel output1 = new Panel();
		output1.setBackground(Color.blue);
		outputArea = new TextArea(12, 30);
		outputArea.setBackground(Color.gray);
		output1.add(outputArea);
		add(output1, "Center");

// ...e infine il pannello contenente i comandi per effettuare interrogazioni sul grafo, 
// che occuperà la parte destra del frame.

		Panel buttonQuery = new Panel();
		buttonQuery.setLayout(new GridLayout(0,1));
		buttonQuery.setBackground(Color.blue);

		Breve = new Button("Matrice più breve");
		buttonQuery.add(Breve);
		Breve.setEnabled(false);
		Breve.addActionListener(this);

		Veloce = new Button("Matrice più veloce");
		buttonQuery.add(Veloce);
		Veloce.setEnabled(false);
		Veloce.addActionListener(this);

		Costosa = new Button("Matrice meno costosa");
		buttonQuery.add(Costosa);
		Costosa.setEnabled(false);
		Costosa.addActionListener(this);

		Label cittaP = new Label("Città di partenza");
		cittaPartenza = new TextField();
		cittaPartenza.setEnabled(false);
		buttonQuery.add(cittaP);
		buttonQuery.add(cittaPartenza);
		Label cittaA = new Label("Città di arrivo");
		cittaArrivo = new TextField();
		cittaArrivo.setEnabled(false);
		buttonQuery.add(cittaA);
		buttonQuery.add(cittaArrivo);
		Calcola = new Button("Calcola Percorso");
		buttonQuery.add(Calcola);
		Calcola.setEnabled(false);
		Calcola.addActionListener(this);

		Esci = new Button("Esci");
		buttonQuery.add(Esci);
		Esci.addActionListener(this);

		add(buttonQuery, "East");

	}

	public void actionPerformed(ActionEvent e){

// Pulsante Esci: il programma termina.	
		if (e.getActionCommand().equals("Esci"))
			System.exit(0);

// Pulsante Ok: se il campo numCitta è vuoto l'utente non ha inserito nessun numero, quindi
// l'evento termina. Altrimenti viene memorizzato in n il numero inserito e su questo viene
// subito effettuato un controllo: se minore di zero viene stampato un messaggio di errore
// e si esce dall'evento.
// Se l'utente inserisce un input conforme ai requisiti vengono inizializzati gli array multipli
// W1 W2 W3 P1 P2 P3 e l'array nome[] che associa ad ogni nodo il nome della città inserito
// dall'utente. Le tre matrici di adiacenza vengono inizializzate con un doppio ciclo for
// che imposta il valore 0 se i=j o il valore INF se i!=j per ogni i,j = 0,1....n-1;
// questo valore verrà modificato solo se l'utente imposterà un collegamento tra le due città.
// Inoltre vengono abilitati i comandi per inserire i nomi delle città, e disabilitati
// quelli appena utilizzati (una volta impostato il parametro n questo non può più essere
// modificato fino al termine del programma).

		if (e.getActionCommand().equals("Ok")){
			if (numCitta.getText().equals(""))
				return;
			n = Integer.valueOf(numCitta.getText().trim()).intValue();
			if (n<0){
				outputArea.setText("Inserire un numero positivo"+"\n");
				numCitta.setText("");
				return;}
			numCitta.setEnabled(false);
			Ok.setEnabled(false);
			Citta.setEnabled(true);
			Inserisci.setEnabled(true);
			nome = new String[n];
			W1 = new int[n][n];
			W2 = new int[n][n];
			P1 = new int[n][n];
			P2 = new int[n][n];
			W3 = new int[n][n];
			P3 = new int[n][n];
			for (int i = 0; i<n; i++){
				for (int j = 0; j<n; j++){
					if (i==j)
					{W1[i][j]=0; W2[i][j]=0; W3[i][j]=0;}
					else
					{W1[i][j]=INF; W2[i][j]=INF; W3[i][j]=INF;}
				}
			}

		}

// Pulsante Inserisci: se il campo Citta è non vuoto e se questo contiene un nome di città che non
// è stato già usato (non è possibile inserire due volte la stessa città), viene memorizzato
// il nome nell'array nome[] ed incrementata la variabile cont che tiene il conto del numero di
// città memorizzate. Inserita l'ultima città, vengono disabilitati i comandi appena utilizzati
// dall'utente e abilitati quelli necessari all'impostazione degli archi.

		if (e.getActionCommand().equals("Inserisci")){
			if(!(Citta.getText().equals(""))){
				String temp = Citta.getText().trim();
				for (int i=0; i<cont; i++){
					if (temp.equals(nome[i])){
						outputArea.setText(outputArea.getText()+"Città già inserita"+"\n");
						Citta.setText("");
						return;
					}
				}
				nome[cont] = temp;
				Citta.setText("");
				outputArea.setText(outputArea.getText()+"Città "+(cont+1)+": "+nome[cont]+"\n");
				nomeCittaLabel.setText("Inserisci nome citta "+(cont+2));
				cont++;
				if (cont==n){
					Citta.setEnabled(false);
					Inserisci.setEnabled(false);
					arco1.setEnabled(true);
					Imposta.setEnabled(true);
					arco2.setEnabled(true);
					Termina.setEnabled(true);

				}
			}
		}

// Pulsante Imposta: vengono memorizzati nelle variabili temporanee stringa e stringa2
// i testi contenuti nei due textField. Se le due stringhe non sono vuote, con un ciclo
// for vengono estratti i due nomi delle città dalla variabile stringa: il metodo .charAt()
// la controlla carattere per carattere fino a quando non trova lo spazio. La stringa viene
// quindi suddivisa nelle due sottostringhe nome1 e nome2 che contengono il nome
// del nodo dal quale deve partire l'arco e il nome del nodo verso il quale l'arco è diretto.

		if (e.getActionCommand().equals("Imposta")){
			String stringa = arco1.getText().trim();
			String stringa2 = arco2.getText().trim();
			if (!(stringa.equals("") || stringa2.equals(""))){
				for(int k=0; k<(stringa.length()-1); k++){
					if (stringa.charAt(k)==' '){
						nome1 = stringa.substring(0,k);
						nome2 = stringa.substring(k+1, stringa.length());
						k = stringa.length();
					}
				}

// Con un secondo ciclo for viene scandito l'array nome[] per trovare i nodi associati ai nomi
// inseriti dall'utente.

				for (int i=0; i<cont; i++){
					if (nome1.equals(nome[i]))
						nodo1 = i;
					if (nome2.equals(nome[i]))
						nodo2 = i;
				}

// Se almeno uno dei due nodi sono ancora impostati al loro valore di default (-1) significa che
// l'utente ha tentato di impostare un arco tra due città che non sono state precedentemente inserite,
// quindi dopo un messaggio di errore ed il ripristino delle variabili l'evento termina.
// Altrettanto accade quando (nodo1==nodo2) e cioè quando si tenta di impostare un cappio.

				if ((nodo1 == -1)||(nodo2 == -1)||(nome1.equals(""))||(nome2.equals(""))||(nodo1==nodo2)){
					outputArea.setText(outputArea.getText()+"Impossibile creare collegamento. Si"+"\n"+
								"ricorda di inserire le città di partenza e"+"\n"+
								"di arrivo separate da uno spazio."+"\n");
					arco1.setText("");
					arco2.setText("");
					nodo1 = -1;
					nodo2 = -1;
					nome1 = "";
					nome2 = "";	
					return;}

// Con un processo analogo al precedente, vengono estratti dalla stringa2 il costo e il tempo di
// percorrenza dell'arco che si desidera impostare...

				for(int k=0; k<(stringa2.length()-1); k++){
					if (stringa2.charAt(k)==' '){
						c = Integer.valueOf(stringa2.substring(0,k)).intValue();
						t = Integer.valueOf(stringa2.substring(k+1,stringa2.length())).intValue();
						k = stringa2.length();
					}
				}

// ...e se questi non sono conformi ai requisiti (numero intero che va da -1000 a 1000) il processo
// termina dopo un messaggio di errore ed il ripristino delle variabili.

				if ((c<-1000)||(t<-1000)||(c>1000)||(t>1000)){
					outputArea.setText(outputArea.getText()+"Inserire valori da -1000 a 1000"+"\n");
					arco1.setText("");
					arco2.setText("");
					nodo1 = -1;
					nodo2 = -1;
					nome1 = "";
					nome2 = "";				
					return;}

// Se tutto va a buon fine viene impostato il costo c nella matrice W1 tra i due nodi definiti dall'utente,
// il tempo t nella matrice W2 ed il valore 1 nella matrice W3. Va notato che poichè il grafo associato
// a W3 non è orientato, la matrice è simmetrica rispetto alla diagonale principale e quindi 
// W3[i][j] = W3[j][i] per ogni i, j = 0....n-1.
// Infine le variabili vengono come di consueto ripristinate al loro valore di default.
		
				W1[nodo1][nodo2] = c;
				W2[nodo1][nodo2] = t;
				W3[nodo1][nodo2] = 1;

// L'istruzione seguente rende W3 un grafo non orientato in quanto crea un arco anche nel verso opposto
// a quello indicato dall'utente. Rimuovendola, W3 diventa un grafo orientato esattamente come i due
// precedenti.

				W3[nodo2][nodo1] = 1;
				outputArea.setText(outputArea.getText()+"Arco: "+nome1+" -> "+nome2+" Costo: "+c+
							 " Tempo: "+t+"\n");
				arco1.setText("");
				arco2.setText("");
				nodo1 = -1;
				nodo2 = -1;
				nome1 = "";
				nome2 = "";
			}
		}

// Pulsante Termina: informa l'algoritmo che l'utente ha impostato tutti gli archi desiderati, quindi
// abilita i comandi necessari ad interrogare il grafo e disabilita quelli appena utilizzati.
// Viene chiamata la procedura Floyd-Warshall per ogni matrice: questa procedura verrà descritta
// meglio nel seguito.

		if(e.getActionCommand().equals("Termina")){
			arco1.setEnabled(false);
			arco2.setEnabled(false);
			Imposta.setEnabled(false);
			Termina.setEnabled(false);
			Breve.setEnabled(true);
			Veloce.setEnabled(true);
			Costosa.setEnabled(true);
			Calcola.setEnabled(true);
			cittaPartenza.setEnabled(true);
			cittaArrivo.setEnabled(true);
			P1 = FloydWarshall(W1, n, 0);
			W1 = FloydWarshall(W1, n, 1);
			P2 = FloydWarshall(W2, n, 0);
			W2 = FloydWarshall(W2, n, 1);
			P3 = FloydWarshall(W3, n, 0);
			W3 = FloydWarshall(W3, n, 1);
		}

// Pulsante Matrice meno costosa: 
// Pulsante Matrice più veloce:
// Pulsante Matrice più breve:
// stampano nel campo di output le matrici relative ai costi, ai tempi e alle distanze dei vari
// collegamenti; a questo scopo vengono utilizzati due diversi cicli for annidati.
// Successivamente vengono stampate anche la relative matrici dei predecessori.

		if(e.getActionCommand().equals("Matrice meno costosa")){
			outputArea.setText("Matrice principale:"+"\n");
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					if((W1[i][j]>=0)&& (W1[i][j]!=INF))
					outputArea.setText(outputArea.getText()+"   "+W1[i][j]+"   ");
					if(W1[i][j]<0)
					outputArea.setText(outputArea.getText()+"  "+W1[i][j]+"  ");
					if(W1[i][j]==INF)
					outputArea.setText(outputArea.getText()+" INF ");
				}outputArea.setText(outputArea.getText()+"\n");
			}
			outputArea.setText(outputArea.getText()+"Matrice dei predecessori:"+"\n");
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					if((P1[i][j]>=0)&& (P1[i][j]!=NIL))
					outputArea.setText(outputArea.getText()+"   "+P1[i][j]+"   ");
					if(P1[i][j]<0)
					outputArea.setText(outputArea.getText()+"  "+P1[i][j]+"  ");
					if(P1[i][j]==NIL)
					outputArea.setText(outputArea.getText()+" NIL ");
				}outputArea.setText(outputArea.getText()+"\n");
			}

		}

		if(e.getActionCommand().equals("Matrice più veloce")){
			outputArea.setText("Matrice principale:"+"\n");
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					if((W2[i][j]>=0)&& (W2[i][j]!=INF))
					outputArea.setText(outputArea.getText()+"   "+W2[i][j]+"   ");
					if(W2[i][j]<0)
					outputArea.setText(outputArea.getText()+"  "+W2[i][j]+"  ");
					if(W2[i][j]==INF)
					outputArea.setText(outputArea.getText()+" INF ");
				}outputArea.setText(outputArea.getText()+"\n");
			}
			outputArea.setText(outputArea.getText()+"Matrice dei predecessori:"+"\n");			
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					if((P2[i][j]>=0)&& (P2[i][j]!=NIL))
					outputArea.setText(outputArea.getText()+"   "+P2[i][j]+"   ");
					if(P2[i][j]<0)
					outputArea.setText(outputArea.getText()+"  "+P2[i][j]+"  ");
					if(P2[i][j]==NIL)
					outputArea.setText(outputArea.getText()+" NIL ");
				}outputArea.setText(outputArea.getText()+"\n");
			}

		}

		if(e.getActionCommand().equals("Matrice più breve")){
			outputArea.setText("Matrice principale:"+"\n");
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					if((W3[i][j]>=0)&& (W3[i][j]!=INF))
					outputArea.setText(outputArea.getText()+"   "+W3[i][j]+"   ");
					if(W3[i][j]==INF)
					outputArea.setText(outputArea.getText()+" INF ");
				}outputArea.setText(outputArea.getText()+"\n");
			}
			outputArea.setText(outputArea.getText()+"Matrice dei predecessori:"+"\n");
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					if((P3[i][j]>=0)&& (P3[i][j]!=NIL))
					outputArea.setText(outputArea.getText()+"   "+P3[i][j]+"   ");
					if(P3[i][j]==NIL)
					outputArea.setText(outputArea.getText()+" NIL ");
				}outputArea.setText(outputArea.getText()+"\n");
			}

		}


// Pulsante Calcola Percorso: dopo aver nuovamente impostato le variabili nodo1 e nodo2 al
// valore di default ed aver pulito la outputArea, vengono memorizzati i nomi delle città di
// partenza e di arrivo nelle due variabili temporanee stringa e stringa2.

		if(e.getActionCommand().equals("Calcola Percorso")){
			nodo1 = -1;
			nodo2 = -1;
			outputArea.setText("");
			String stringa = cittaPartenza.getText().trim();
			String stringa2 = cittaArrivo.getText().trim();

// Se queste contengono effettivamente una stringa non nulla, con un processo analogo a quello
// utilizzato per impostare gli archi, vengono ricavati i nodi associati ai nomi delle città
// inserite dall'utente.

			if (!(stringa.equals("") || stringa2.equals(""))){
				for (int i=0; i<cont; i++){
					if (stringa.equals(nome[i]))
						nodo1 = i;
					if (stringa2.equals(nome[i]))
						nodo2 = i;
				}

// Viene quindi effettuato un controllo simile a quello definito nella fase dell'impostazione
// degli archi.

				if ((nodo1==-1)||(nodo2==-1)||(nodo1==nodo2)){
					outputArea.setText("Impossibile visualizzare il percorso."+"\n"+
								"Inserire i nomi delle città nei rispettivi"+"\n"+
								"campi."+"\n");
					nodo1 = -1;
					nodo2 = -1;
					return;
				}

// Se il tutto va a buon fine, e se esiste un cammino tra i due nodi indicati dall'utente
// (W[nodo1][nodo2] != INF) allora viene chiamata la procedura ricorsiva PrintAllPairsShortestPath.
// La funzione viene chiamata tre diverse volte, per calcolare il cammino meno costoso, quello più
// veloce ed infine il percorso più breve.
// Questa procedura verrà esaminata più in avanti.

				if (W1[nodo1][nodo2]==INF)
				outputArea.setText(outputArea.getText()+"Non esiste nessun percorso da "+nome[nodo1]
							+" a "+nome[nodo2]+"\n");
				else{
				outputArea.setText(outputArea.getText()+"Percorso meno costoso: "+W1[nodo1][nodo2]+"\n");
				PrintAllPairsShortestPath(P1, nodo1, nodo2);
				outputArea.setText(outputArea.getText()+"\n");}
				if (W2[nodo1][nodo2]==INF)
				outputArea.setText(outputArea.getText()+"Non esiste nessun percorso da "+nome[nodo1]+
							" a "+nome[nodo2]+"\n");
				else{
				outputArea.setText(outputArea.getText()+"Percorso più veloce: "+W2[nodo1][nodo2]+"\n");
				PrintAllPairsShortestPath(P2, nodo1, nodo2);
				outputArea.setText(outputArea.getText()+"\n");}
				if (W3[nodo1][nodo2]==INF)
				outputArea.setText(outputArea.getText()+"Non esiste nessun percorso da "+nome[nodo1]+
							" a "+nome[nodo2]+"\n");
				else{
				outputArea.setText(outputArea.getText()+"Percorso più breve: "+W3[nodo1][nodo2]+"\n");
				PrintAllPairsShortestPath(P3, nodo1, nodo2);
				outputArea.setText(outputArea.getText()+"\n");}
			}		
		}
	}

// Imposto il listener della finestra principale.

	public class WindowDestroyer extends WindowAdapter{

		public void windowClosing(WindowEvent e){
			System.exit(0);
	}
	}

// Ho deciso di implementare una singola funzione FloydWarshall che calcoli "in linea" la matrice dei predecessori
// mentre vengono calcolate le n matrici D: questa scelta mi permette di non appesantire il codice con due
// procedure strutturalmente molto simili tra di loro.
// D'altra parte è necessario un campo flag che indichi alla procedura quale tra le due matrici D[n] e P restituire;
// quando il valore è impostato a 1 viene restituita la matrice dei pesi di cammino minimo, quando è impostato a 0
// la matrice restituita è quella dei predecessori.
// Va inoltre notato che sebbene la procedura venga chiamata per restituire una sola delle due matrici, entrambe
// vengono comunque calcolate; avrei potuto inserire un controllo sul campo flag all'interno dei cicli per
// imporre all'algoritmo di calcolare una o l'altra matrice, ma questo ne avrebbe complicato inutilmente la struttura
// per ottenere un miglioramento nei tempi di esecuzione solo di un fattore costante.
// Un'ultima considerazione deve essere fatta sulla sequenzialità delle chiamate a FloydWarshall: poichè 
// la matrice restituita dopo la procedura viene sovrascritta a quella di partenza (per gli scopi di questa
// esercitazione dopo la chiamata a FloydWarshall la matrice di adiacenza originale non serve più) è necessario chiamare
// prima la procedura per calcolare la matrice dei predecessori, e solo successivamente calcolare quella dei pesi di
// cammino minimo.


	public static int[][] FloydWarshall(int[][] W, int num, int f){
	
		int n = num;

		int[][][] D = new int[n+1][n+1][n+1];   //L'insieme delle k matrici principali in fase di calcolo
		int[][] R = new int[n][n];              //La matrice principale finale che viene restituita dalla funzione
		int[][][] D2  = new int[n+1][n+1][n+1]; //L'insieme delle k matrici dei predecessori in fase di calcolo
		int[][] P = new int[n][n];              //La matrice dei predecessori finale che viene restituita

// Inizializzazione delle matrici...
// Viene "trascritta" nella prima delle k matrici D, ossia D[0], la matrice W passata come parametro.
// La matrice D2[0] dei predecessori viene invece impostata seconda l'apposita equazione di ricorrenza.
// Per esigenze di calcolo nel corpo principale della procedura Floyd-Warshall, è più comodo utilizzare
// un insieme di matrici D[n+1][n+1][n+1] dove D[k][0][j] = 0 per ogni j = 0,1,...n e D[k][i][0] = 0
// per ogni i = 0,1,...n. In pratica il primo elemento effettivo della matrice si troverà in posizione [1][1]
// invece che in posizione [0][0], e l'ultimo in posizione [n][n] invece che in posizione [n-1][n-1];
// in questo modo si evitano complicazioni nella gestione degli indici nel calcolo della matrice di output.

		for(int c=0; c<=n; c++){
			for(int d=0; d<=n; d++){
				if (c==0 || d==0){
					D[0][c][d] = 0;
					D2[0][c][d] = 0;}
				else {D[0][c][d] = W[c-1][d-1];
					if ((c!=d) && (W[c-1][d-1]<INF))
						D2[0][c][d]=c;
						else D2[0][c][d]=NIL;}
			}
		}

// Il corpo principale della procedura Floyd-Warshall....

		for(int k=1; k<=n; k++){
			for(int i=1; i<=n; i++){
				for(int j=1; j<=n; j++){
					if (D[k-1][i][j] <= SommaInfiniti(D[k-1][i][k], D[k-1][k][j])){
						D[k][i][j] = D[k-1][i][j];
						D2[k][i][j] = D2[k-1][i][j];}
						else {D[k][i][j] = SommaInfiniti(D[k-1][i][k], D[k-1][k][j]);
							D2[k][i][j] = D2[k-1][k][j];}
				}
			}
		}

// Prima di restituire una delle due matrici, queste vengono "depurate" eliminando la prima riga e la
// prima colonna composte di soli "0", che sono state precedentemente inserite per comodità di calcolo.

		for(int i=1; i<=n; i++){
			for(int j=1; j<=n; j++){
					R[i-1][j-1] = D[n][i][j];
					P[i-1][j-1] = D2[n][i][j];
			}
		}		

// A seconda del valore contenuto nel campo flag viene restituita una delle due matrici.

		if (f==1)
		return R;
		else return P;
	
	}

// Per gestire la somma di infiniti, in quanto il valore INF è stato impostato come l'ultimo degli interi Integer
// utilizzabile, è necessaria una funzione che restituisca lo stesso valore INF se una delle due variabili da
// sommare è uguale a INF, o la somma dei due interi se questi due valori sono diversi da INF.

	public static int SommaInfiniti(int x, int y){
		if (x==INF || y==INF)
			return INF;
		else return x+y;
	}

// La procedura riceve in ingresso i valori nodo1 e nodo2 dei quali si vuole calcolare il cammino minimo, nonchè
// la matrice dei predecessori calcolata dalla procedura Floyd-Warshall.

	public void PrintAllPairsShortestPath(int[][] D, int nodo1, int nodo2){
		int[][] P = D;
		int i=nodo1;
		int j=nodo2;

// Se i=j, viene stampato il nodo i come tappa nel percorso dal nodo1 al nodo2, altrimenti viene chiamata
// ricorsivamente la procedura PrintAllPairsShortestPath passando come parametro la stessa matrice P, lo
// stesso nodo di partenza i, ma come nodo di destinazione l'elemento P[i][j]-1.

		if (i==j){
			outputArea.setText(outputArea.getText()+nome[i]+" -> ");
			return;}
			else{ PrintAllPairsShortestPath(P, i, P[i][j]-1);
				outputArea.setText(outputArea.getText()+nome[j]+" -> ");
				return;}
	}

}
