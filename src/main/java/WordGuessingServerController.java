import java.io.Console;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class WordGuessingServerController implements Initializable{
	
	@FXML
	private TabPane serverTabPane;
	
	@FXML
	public Tab GeneralTab;
	
	@FXML
	public HashMap<Integer, Tab> clientTabs = new HashMap<Integer, Tab>();
	
	@FXML
	private AnchorPane serverAnchorPane;
	
	@FXML
	public HashMap<Integer, ListView<String>> clientListViews = new HashMap<Integer, ListView<String>>();
	
	@FXML
	public ListView<String> serverEvents;
	
	public ListView<String> tabListView = new ListView<String>();
	
	@FXML
	public TextField ServerPortInput;
	
	@FXML
	public Button ServerPortInputBtn;
	
	@FXML
	public VBox serverPortInputVBox;
	
	WordGuessingServer serverConnection;
	WordGuessingServerController serverController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	public void startServerOnPort(ActionEvent e) {
		try {
			int port = Integer.parseInt(ServerPortInput.getText());
		
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/serverLog.fxml"));
			serverTabPane = loader.load();
	
	        serverController = loader.getController();
			
	        serverConnection = new WordGuessingServer(data -> {
	        	Platform.runLater(()->{
	        		serverController.serverEvents.getItems().add(data.toString());
	        	});
	        	
	        }, add -> {
	        	Platform.runLater(()->{
	        		serverController.addClientTab(Integer.parseInt(add.toString()));
	        	});
	        },  set -> {
	        	Platform.runLater(()->{
	        		serverController.addClientData(Integer.parseInt(set.toString()));
	        	});
	        }, tabData -> {
	        	Platform.runLater(()->{
	        		serverController.tabListView.getItems().add(tabData.toString());
	        	});
	        },
	        		port);
	        
	        serverTabPane.getStylesheets().add("/Styles/serverLogStyle.css");
	        
	        serverPortInputVBox.getScene().setRoot(serverTabPane);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
        
	}
	
	public void addClientTab(int count) {
		AnchorPane anchor = new AnchorPane();
		ListView<String> clientListView = new ListView<String>();
		clientListView.setPrefSize(327, 556);
		anchor.getChildren().addAll(clientListView);
		clientListView.getItems().add("Client #" + count + " has connected.");
	    Tab tab = new Tab("Client " + count, anchor);
	    
	    serverTabPane.getTabs().add(tab);
	    
	    clientTabs.put(count, tab);
	    clientListViews.put(count, clientListView);
	}
	
	public void addClientData(Integer count) {
		tabListView.setItems(clientListViews.get(count).getItems());
	}
	
	
	


}
