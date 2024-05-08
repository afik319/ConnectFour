import java.util.concurrent.atomic.AtomicInteger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

/**
 * Controller class for the Connect Four game.
 */
public class ConnectFourController {
	
	private static final int LINES_COUNT = 6;
	private static final int lAST_LINE_INDEX = LINES_COUNT - 1;
	private static final int COLUMNS_COUNT = 7;
	private static final int lAST_COLUMN_INDEX = COLUMNS_COUNT - 1;
	private static final int FIRST_COLUMN = 1;
	private static final int LINE_KIND = 0;
	private static final int COLUMN_KIND = 1;
	private static final int TR_TO_BL_KIND = 2;//top right to bottom left slant
	private static final int TL_TO_BR_KIND = 3; //top left to bottom right slant
	
	
	private static final int START_HEIGHT = 40;
	private static final int CLEAR_BUTTON_HEIGHT = 40;
	private static final int NUM_TO_INDEX_ADJUSTMENT = -1;
	
	private final int NOT_FINISHED_GAME = -1;
	private final double HALF_SIZE = 0.5;
	private final double TOKEN_RATIO = 0.98;
	private final int GAME_TIE = 0;
	private final int GAME_WON = 1;
	private static final int FIRST_PLAYER = 1;
	private static final int SECOND_PLAYER = 2;
	private static final int BUTTONS_CELL_ADDITION = 2;
	private static final int CLEAR_LEFT_COLUMN_INDEX = 3;
	private static final int CONNECT = 4;
	private static final int CONNECT_TOKENS_DISTANCE = CONNECT - 1;
	private static final int TOTAL_TOKENS = LINES_COUNT * COLUMNS_COUNT;
	private static final String FIRST_COLOR = "האדום";
	private static final String SECOND_COLOR = "הצהוב";
	private final String GAME_FINISHED_SOUND_FILE_NAME = "Game Finished.mp3";
	private final String GAME_START_SOUND_FILE_NAME = "Game start.mp3";
	//private final String FIRST_WON_ANNOUNCEMENT = "Red won.mp3";
	//private final String SECOND_WON_ANNOUNCEMENT = "Yellow won.mp3";
	
	// Game state variables
	private boolean firstTurn;
	private int[][] occupiedCell;
	private int[] columnsCapacity = new int[COLUMNS_COUNT];
	private double firstMiddleX;
	private double firstMiddleY;
	private double cellHeight;
	private double cellWidth;
	private double tokensRadius;
	private Button btns[];
	private int usedTokens;
	private boolean notFirstTime;
	
	 @FXML
	 private GridPane grid1;
	 @FXML
	    private Button clear;
	 @FXML
	    private VBox vbox1;
	 @FXML
	 private Pane pane1;
	  
	/**
	  * Initializes the Connect Four game.
	  */ 
	@FXML
    public void initialize() {   
		if(!notFirstTime) //first time
			makeSound(GAME_START_SOUND_FILE_NAME);
		firstMiddleX = (pane1.getPrefWidth() / COLUMNS_COUNT) * HALF_SIZE;
		firstMiddleY = START_HEIGHT + ((pane1.getPrefHeight() - START_HEIGHT) / LINES_COUNT) * HALF_SIZE;
		cellHeight = (pane1.getPrefHeight() - START_HEIGHT) / LINES_COUNT;
		cellWidth = pane1.getPrefWidth() / COLUMNS_COUNT;
		tokensRadius = (cellHeight * HALF_SIZE) * TOKEN_RATIO;
    	vbox1.setFillWidth(false);	
    	pane1.getChildren().clear();
    	createBoard();   	
    	createButton();
    	usedTokens = 0;  	
    	columnsCapacity = new int[COLUMNS_COUNT];
    	occupiedCell = new int[LINES_COUNT][COLUMNS_COUNT];
    	btnsSwitch(false);
    	firstTurn = true;
    	clearButtonSet();
    	
    }	
	
	private void clearButtonSet() {
		clear.setOnKeyPressed(
			    new EventHandler<KeyEvent>() {
			        public void handle(KeyEvent event) {
			        	keyPressed(event);
			        }
			    }
		);
	}
	
	/**
     * Creates the horizontal and vertical lines for the game board.
     */
    private void createLines() {
    	for(int i = 0 ; i < LINES_COUNT + BUTTONS_CELL_ADDITION ; i++) {
    		Line curLine = new Line(0 , START_HEIGHT + i * cellHeight ,
    				pane1.getPrefWidth() ,START_HEIGHT + i * cellHeight);
    		pane1.getChildren().add(curLine);
    	}
    	
    	double lastHeight = START_HEIGHT + cellHeight * (LINES_COUNT + 1) + CLEAR_BUTTON_HEIGHT;
    	Line lastLine = new Line(0 , lastHeight , pane1.getPrefWidth() , lastHeight );
    	pane1.getChildren().add(lastLine);
    }
    
    /**
     * Creates the vertical columns for the game board.
     */
    private void createColumns() {
    	double clearLineHeightAdd;
    	for(int i = 0 ; i <= COLUMNS_COUNT ; i++) {
        	clearLineHeightAdd = (CLEAR_LEFT_COLUMN_INDEX <= i && i <= CLEAR_LEFT_COLUMN_INDEX + 1)? clear.getPrefHeight() : 0;
    		Line curLine = new Line(i * cellWidth , START_HEIGHT ,
    				i * cellWidth , pane1.getPrefHeight() + cellHeight + clearLineHeightAdd);
    		pane1.getChildren().add(curLine);
    	}
    	
    	Line firstColumn = new Line(0 , START_HEIGHT , 0 , pane1.getPrefHeight() + cellHeight + clear.getPrefHeight());
    	Line lastColumn = new Line(cellWidth * COLUMNS_COUNT , START_HEIGHT , cellWidth * COLUMNS_COUNT , pane1.getPrefHeight() + cellHeight + clear.getPrefHeight());
    	pane1.getChildren().add(firstColumn);
    	pane1.getChildren().add(lastColumn);
    }
    
    /**
     * Draws the game board.
     */
    private void createBoard() {
    	createLines();
    	createColumns();	
    }
    
    /**
     * Creates the buttons for selecting columns in the game.
     */
    private void createButton() {
    	btns = new Button[COLUMNS_COUNT];
    	for(int i = 0 ; i < COLUMNS_COUNT ; i++) {
    		int index = i;
    		btns[i] = new Button("" + (i + 1));
    		btns[i].setPrefSize(grid1.getPrefWidth() / COLUMNS_COUNT , grid1.getPrefHeight() / COLUMNS_COUNT);
    		grid1.add(btns[i] , i , 0); //only one line in grid
    		btns[i].setPrefSize(cellWidth , cellHeight);
    		btns[i].setOnAction(
    			new EventHandler<ActionEvent>() {
    			public void handle(ActionEvent event){
    				if(columnsCapacity[index] < LINES_COUNT) {
    					if(columnsCapacity[index] == lAST_LINE_INDEX) {
    						btns[index].setDisable(true);
    					}
    					addToken(index);
    				}
    			} 
    		});
    		btns[i].setOnKeyPressed(
    			    new EventHandler<KeyEvent>() {
    			        public void handle(KeyEvent event) {
    			        	keyPressed(event);
    			        }
    			    }
    		);

    	}
    }
    
    /**
     * Adds a token to the specified column.
     * 
     * @param column The column index to add the token to.
     */
    private void addToken(int Column) {
    	
    	double curTokenX = firstMiddleX +  cellWidth * (Column);
    	
    	double lowMiddleY = firstMiddleY +  cellHeight * (LINES_COUNT - 1);
    	double curTokenY = lowMiddleY - cellHeight * (columnsCapacity[Column]);
    	
    	Circle curToken = new Circle(curTokenX , curTokenY , tokensRadius);

    	Color insideColor = firstTurn? Color.INDIANRED : Color.GOLD;
    	curToken.setFill(insideColor);
	    pane1.getChildren().add(curToken);
	    
    	occupiedCell[columnsCapacity[Column]][Column] = firstTurn? FIRST_PLAYER  : SECOND_PLAYER;
    	usedTokens++;
    	
    	int playerNumber = firstTurn? FIRST_PLAYER : SECOND_PLAYER;
    	int winStatus = winCheck(columnsCapacity[Column] , Column , playerNumber); 
    	if(winStatus != NOT_FINISHED_GAME) {
    		boolean tie = !(winStatus == GAME_WON);
    		winnerAnnouncement(tie);
    		btnsSwitch(true); //game over - so buttons off
    		clear.requestFocus();
    	}
    	columnsCapacity[Column]++;
    	firstTurn = !firstTurn; 
    }
    
    /**
     * Checks for a winning combination in a row or column.
     * 
     * @param line The row index to check.
     * @param column The column index to check.
     * @param playerNumber The player number (1 or 2) to check for.
     * @param isLineCheck Specifies whether to check a row (true) or a column (false).
     * @return True if a winning combination is found, otherwise false.
     */
    private boolean rowCheck(int line , int column , int maxCount , int playerNumber , boolean isLineCheck) {
    	int count , index;
    	count = index = 0;
    	int leftCoordinate = isLineCheck? line : index;
    	int rightCoordinate = isLineCheck? index : column;
    	
    	while((isLineCheck && rightCoordinate < maxCount) || (!isLineCheck && leftCoordinate < maxCount)) {
    		if(occupiedCell[leftCoordinate][rightCoordinate] == playerNumber)
    			count++; //counting how much tokens in a row
    		else count = 0;
    		if(count == CONNECT) { //four in a row - win
    			int rowKind = isLineCheck? LINE_KIND : COLUMN_KIND;
    			winnerRow(leftCoordinate , rightCoordinate , rowKind);
    			return true;
    		}
    		if(isLineCheck) //adjustment
    			rightCoordinate++;
    		else leftCoordinate++;
    	} 
    	return false;
    }
    
    /**
     * Calculates the center point of a token in the game grid.
     *
     * @param line   The line number (row) of the token.
     * @param column The column number of the token.
     * @return The center point of the token as a Point2D object.
     */
    private Point2D getTokenCenter(int line , int column) {
    	double centerX = firstMiddleX + column * cellWidth;
    	double centerY = firstMiddleY + (lAST_LINE_INDEX - line) * cellHeight;
    	Point2D center = new Point2D(centerX , centerY);
    	return center;
    }
    
    /**
     * Draws a line to indicate a winning row of tokens.
     *
     * @param line     The line number (row) of the starting token.
     * @param column   The column number of the starting token.
     * @param rowKind  The kind of winning row (0: horizontal, 1: vertical, 2: diagonal top-left to bottom-right, 3: diagonal top-right to bottom-left).
     */
    private void winnerRow(int line , int column , int rowKind) {
    	Point2D startLineCell = getTokenCenter(line , column);
    	Point2D lineEnd;
    	int[][] endLineCell = {
    			{line , line - CONNECT_TOKENS_DISTANCE , line - CONNECT_TOKENS_DISTANCE , line - CONNECT_TOKENS_DISTANCE} , 
    			{column - CONNECT_TOKENS_DISTANCE , column , column - CONNECT_TOKENS_DISTANCE , column + CONNECT_TOKENS_DISTANCE}};
    	lineEnd = getTokenCenter(endLineCell[0][rowKind] , endLineCell[1][rowKind]);
    	Line winLine = new Line(startLineCell.getX() , startLineCell.getY() , lineEnd.getX() , lineEnd.getY());
    	pane1.getChildren().add(winLine);
    }
    
    /**
     * Checks for a winning combination in a diagonal.
     * 
     * @param count The current count of consecutive tokens.
     * @param lineStart The starting row index for the diagonal check.
     * @param columnStart The starting column index for the diagonal check.
     * @param playerNumber The player number (1 or 2) to check for.
     * @return True if a winning combination is found, otherwise false.
     */
    private boolean slantCountCheck(AtomicInteger count , AtomicInteger lineStart , AtomicInteger columnStart , int playerNumber , boolean isTopLeftSlant) {
    	if(occupiedCell[lineStart.get()][columnStart.get()] == playerNumber)
			count.incrementAndGet();
		else count.set(0);
		if(count.get() == CONNECT) { //win found
			int rowKind = isTopLeftSlant? TL_TO_BR_KIND : TR_TO_BL_KIND;
			winnerRow(lineStart.get() , columnStart.get() , rowKind);
			return true;
		}
		lineStart.incrementAndGet(); //slants checked from top to bottom
		return false; //win not found
    }
    
    /**
     * Checks for a win condition in the game.
     * 
     * @param line The row index of the last added token.
     * @param column The column index of the last added token.
     * @param playerNumber The player number (1 or 2) of the last added token.
     * @return An integer representing the game state: 0 for tie, 1 for win, -1 for ongoing game.
     */
    private int winCheck(int line , int column , int playerNumber) {
    	if(rowCheck(line , column , COLUMNS_COUNT , playerNumber , true)) //check line for win
			return GAME_WON;
    	if(rowCheck(line , column , LINES_COUNT , playerNumber , false)) //check column for win
			return GAME_WON; 
    	
    	//variables initializing for top right to bottom left slant check
    	AtomicInteger count = new AtomicInteger(0);
    	int distanceTopLeft = Math.min(line, column);
    	AtomicInteger lineStart = new AtomicInteger(line - distanceTopLeft);
    	AtomicInteger columnStart = new AtomicInteger(column - distanceTopLeft);
    	
    	while(lineStart.get() <= lAST_LINE_INDEX && columnStart.get() <= lAST_COLUMN_INDEX) {
    		if(slantCountCheck(count , lineStart , columnStart , playerNumber , false)) {
    			
    			return GAME_WON;
    		}
    		columnStart.incrementAndGet();
    	} //top right to bottom left slant check
    	
    	//variables initializing for top left to bottom right slant check
    	count.set(0);
    	int distanceTopRight = Math.min(line, lAST_COLUMN_INDEX - column);
    	lineStart.set(line - distanceTopRight);
    	columnStart.set(column + distanceTopRight);
    	
    	while(lineStart.get() <= lAST_LINE_INDEX && columnStart.get() >= 0) {
    		if(slantCountCheck(count , lineStart , columnStart , playerNumber , true)) {
    			return GAME_WON;
    		}
    		columnStart.decrementAndGet();
    	} //top left to bottom right slant check
    	
    	if(usedTokens < TOTAL_TOKENS) //win not found and the board is not full
    		return NOT_FINISHED_GAME;
    	return GAME_TIE; //win not found and the board is full
    }
    
    /**
     * Disables or enables the column buttons based on the game state.
     * 
     * @param toDisable True to disable buttons, false to enable.
     */
	private void btnsSwitch(boolean toDisable) {
    	for(int i = 0 ; i < btns.length ; i++) {
    		btns[i].setDisable(toDisable);
    	}
    }
	
	@FXML
    void keyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.DELETE)
			clearPressed(new ActionEvent());
		else {
			String pressed = event.getText();
			int column = 0;
			if(isNum(pressed))
				column = Integer.parseInt(pressed);
			if(FIRST_COLUMN <= column && column <= COLUMNS_COUNT && !btns[column + NUM_TO_INDEX_ADJUSTMENT].isDisable())
				btns[column + NUM_TO_INDEX_ADJUSTMENT].fire();
		}		
    }
	
	private boolean isNum(String str) {
		return str.matches("-?\\d+");
	}
    
	/**
     * Displays a winner announcement dialog or a tie message.
     * 
     * @param tie True if the game ended in a tie, otherwise false.
     */
    private void winnerAnnouncement(boolean tie) {
    	String winner = firstTurn? FIRST_COLOR : SECOND_COLOR;
    	/*if(!tie) {
    		String winnerAnnouncement = firstTurn? FIRST_WON_ANNOUNCEMENT : SECOND_WON_ANNOUNCEMENT;
    		makeSound(winnerAnnouncement);
    	}*/
    	makeSound(GAME_FINISHED_SOUND_FILE_NAME);
    	Alert alert1 = new Alert(AlertType.ERROR); 
    	alert1.setTitle("המשחק הסתיים!");
    	if(!tie)
    		alert1.setHeaderText("השחקן " + winner + " ניצח!");
    	else alert1.setHeaderText("זהו תיקו!");
    	alert1.setContentText("ברכותיי!"); 
    	alert1.showAndWait();
    }
    
    /**
     * Resets the game to its initial state.
     * 
     * @param event The action event that triggered the method.
     */
    @FXML
    void clearPressed(ActionEvent event) {
    	notFirstTime = true;
    	initialize();
    }
    
    /**
     * Plays the specified sound file.
     * @param soundFileName The name of the sound file
     */
    private void makeSound(String soundFileName) {
    	MediaPlayer m1 = soundInitialize (soundFileName);
    	//m1.stop();  // Stop the MediaPlayer if it's currently playing
    	m1.seek(m1.getStartTime());  // Rewind the sound to the beginning
    	m1.play();
    }
    
    /**
     * Initializes a media player for the given sound file.
     * @param fileName The name of the sound file
     * @return The media player for the sound file
     */
    private MediaPlayer soundInitialize(String fileName) {
    	String newSoundString = fileName;
        Media newMedia = new Media(getClass().getResource(newSoundString).toString());
        MediaPlayer newMediaPlayer = new MediaPlayer(newMedia);
        return newMediaPlayer;
    }

}
