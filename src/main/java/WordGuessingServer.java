import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;


public class WordGuessingServer extends WordGame{

	int count = 1;	
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	Server server;
	WordGuessingServerController serverController;
	int port = 5555;
	
	private Consumer<Serializable> callback;
	private Consumer<Serializable> addTab;
	private Consumer<Serializable> setTab;
	private Consumer<Serializable> addData;
	
	WordGuessingServer(Consumer<Serializable> call, Consumer<Serializable> add, Consumer<Serializable> set, Consumer<Serializable> data, int port){
		callback = call;
		addTab = add;
		setTab = set;
		addData = data;
		this.port = port;
		server = new Server();
		server.start();
	}
	
	public class Server extends Thread{
		
		public void run() {
		
			try(ServerSocket serverSocket = new ServerSocket(port);){
				callback.accept("Word Guessing Server is running on port " + port + ". Waiting for clients...");
				System.out.println("Word Guessing Server is running. Waiting for clients...");
		  
			
		    	while(true) {
		
					ClientThread c = new ClientThread(serverSocket.accept(), count);
					callback.accept("Client has connected to server: " + "client #" + count);
					addTab.accept(count);
					clients.add(c);
					new Thread(c).start();
					count++;
				
			    }
			}//end of try
				catch(IOException e) {
					callback.accept("Socket did not launch. Unable to establish connection to Word Guessing Server.");
					System.out.println("Unable to establish connection to Word Guessing Server.");
					e.printStackTrace();
			}
		}//end of while
	}
	

		class ClientThread implements Runnable{
			
		
			Socket connection;
			int count;
			ObjectInputStream in;
			ObjectOutputStream out;
			WordGame game;
			GameObject gameData = new GameObject();
			
			ClientThread(Socket s, int count){
				this.connection = s;
				this.count = count;	
				this.game = new WordGame();
			}
			
			
			public void run(){
					
				try {
					in = new ObjectInputStream(connection.getInputStream());
					out = new ObjectOutputStream(connection.getOutputStream());
					connection.setTcpNoDelay(true);	
				}
				catch(Exception e) {
					System.out.println("Streams not open");
				}
					
				while (true) {
				    try {
				        // Read the object from the client
				        Object receivedObject = in.readUnshared();

				        if (receivedObject instanceof GameObject) {	  
				            gameData.updateState((GameObject) receivedObject);
				            
				            categoryAction();
		
				        } else {
				            // Handle the case where the received object is not a WordGame
				            System.out.println("Invalid input from the client");
				        }
				    } catch (Exception e) {
				        e.printStackTrace();
				        callback.accept("Client has disconnected from the server: client #" + count);
				        setTab.accept(count);
		                addData.accept("client #" + count + " has disconnected from the server");
				        System.out.println("Client has disconnected from the server: client #" + count);
				        clients.remove(this);
				        break;
				    }
				}

				}//end of run
			
			public void outputString(char input) {
				ArrayList<Integer> idxs = game.isLetterInWord(input);
				boolean found = true;

				for (int i = 0; i < game.wordToGuess.length(); i++) {
					for (int j = 0; j < idxs.size(); j++) {
						if (i == idxs.get(j)) {
							gameData.currString.setCharAt(i, game.wordToGuess.charAt(i));
							found = false;
						} 
					}
				}
				
				if (found) {
					gameData.setCurrGuesses(gameData.getCurrGuesses() + 1);
				}
				
			}
			
			public StringBuilder outputStringFirst() {
				StringBuilder curr = new StringBuilder();
				for(int i = 0; i < game.wordToGuess.length(); i++) {
					curr.append("*");
				}
				return curr;
			}
			
			public void categoryAction() {
				if (gameData.getNewcategory()) {
					String category = gameData.getCategoryChoice();
	                game.wordToGuess = getRandomWord(category);
	                setTab.accept(count);
	                addData.accept("client #" + count + " has chosen: " + category);
	                addData.accept("client #" + count + " word is: " + game.wordToGuess);
	                gameData.setCurrString(outputStringFirst());
	                gameData.setNewCategory(false);
	                try {
//						out.reset();
						out.writeUnshared(gameData);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					guessAction();
				}
			}
			
			public void guessAction() {
				if (gameData.getCurrGuess().length() > 0) {
		            outputString(gameData.getCurrGuess().toCharArray()[0]);
		            setTab.accept(count);
		            addData.accept("client #" + count + " has guessed: " + gameData.getCurrGuess() +
		                        " current client view: " + gameData.getCurrString());
		            checkForRoundFinish();
		            gameData.print();
		            try {
//						out.reset();
						out.writeUnshared(gameData);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			public void checkForRoundFinish() {
				if (gameData.getCurrString().toString().equals(game.wordToGuess)) {
					gameData.getCategoriesCompleted().add(gameData.getCategoryChoice());
					gameData.setNewCategory(true);
					gameData.setCurrGuesses(0);
					
					if (gameData.getCategoriesCompleted().size() == 3) {
						gameData.roundOver = "won";
					}
					setTab.accept(count);
					addTab.accept("client #" + count + " has completed category: " + gameData.getCategoryChoice());
					
				} else if (gameData.getCurrGuesses() > 6) {
					gameData.setNewCategory(true);
					gameData.setAttempts(gameData.getAttempts() + 1);
					gameData.setCurrGuesses(0);
					
					if (gameData.getAttempts() == 3) {
						gameData.roundOver = "lost";
					}
					setTab.accept(count);
					addData.accept("client #" + count + " has failed category: " + gameData.getCategoryChoice());
				}
			}
			
			
		}//end of client thread
}


	
	

	
