import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;

public class MineSweeperBONUS extends Application {
	int [][] mine;
	MineButton [][] buttons;
	boolean alive;
	boolean hasWon;
	boolean isStarted;
	int count;
	int bomb;
	Timeline timeline;
	int sec;
	boolean firstMove;
	Difficulty chosenLevel = new Difficulty(1);
	GridPane minesweeper;

	ArrayList<String> name;
	ArrayList <Integer> shortestTime;

	public static void main(String []args) {
		launch(args);
	}

	@Override
	public void start(Stage theStage){
		minesweeper = new GridPane();
		mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
		buttons = new MineButton [chosenLevel.getRow()][chosenLevel.getCol()];
		alive = true;
		hasWon = false;
		isStarted= false;
		count = 0;
		bomb = chosenLevel.getBombSize();
		sec = 0;
		firstMove = true;
		name = new ArrayList<String>();
		shortestTime = new ArrayList<Integer>();

		MenuBar menuBar = new MenuBar();
		Menu differentLevel = new Menu("Difficulty");
		MenuItem easy = new MenuItem("Beginner");
		MenuItem notSoEasy = new MenuItem("Intermediate");
		MenuItem hard = new MenuItem("Expert");
		differentLevel.getItems().addAll(easy,notSoEasy,hard);
		Menu view = new Menu("View");
		MenuItem highScore = new MenuItem("High Scores");
		view.getItems().add(highScore);
		highScore.setOnAction(e -> {
			try {
				File scores = new File("src/scores.txt");
				Scanner fileReader = new Scanner(scores);
				while(fileReader.hasNext()) {
					String winnerName = fileReader.next();
					int time = fileReader.nextInt();
					name.add(winnerName);
					shortestTime.add(time);
				}
			}catch(FileNotFoundException ex) {
				System.out.println("No file");
			}
			printHighScore();
		});
		Menu help = new Menu("Help");
		MenuItem howToPlay = new MenuItem("How to Play?");
		help.getItems().add(howToPlay);
		howToPlay.setOnAction(e -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("How to Play?");
			alert.setHeaderText("Guideline");
			alert.setContentText("http://www.instructables.com/id/How-to-beat-Minesweeper/");
			alert.showAndWait();
		});
		menuBar.getMenus().addAll(differentLevel, view, help);

		VBox menu = new VBox();
		menu.getChildren().add(menuBar);
		///////////////////////////////////////Difficulty Part//////////////////////////////////////////////////
		Button beginner = new Button("Beginner");
		beginner.setStyle("-fx-text-fill: green; -fx-background-color: #3c7fb1,linear-gradient(#fafdfe, #e8f5fc),linear-gradient(#eaf6fd 0%, #d9f0fc 49%, #bee6fd 50%, #a7d9f5 100%)");
		Button intermediate = new Button("Intermediate");
		intermediate.setStyle("-fx-text-fill:red; -fx-background-color: #3c7fb1,linear-gradient(#fafdfe, #e8f5fc),linear-gradient(#eaf6fd 0%, #d9f0fc 49%, #bee6fd 50%, #a7d9f5 100%)");
		Button expert = new Button("Expert");
		expert.setStyle("-fx-text-fill: darkblue; -fx-background-color: #3c7fb1,linear-gradient(#fafdfe, #e8f5fc),linear-gradient(#eaf6fd 0%, #d9f0fc 49%, #bee6fd 50%, #a7d9f5 100%)");

		HBox difficulty = new HBox();
		Region region1 = new Region();
		Region region2 = new Region();
		HBox.setHgrow(region1, Priority.ALWAYS);	
		HBox.setHgrow(region2, Priority.ALWAYS);
		difficulty.getChildren().addAll(beginner,region1,intermediate,region2,expert);

		///////////////////////////////////////Smiley Part//////////////////////////////////////////////////////
		Smiley smiley = new Smiley();
		HBox topMenu = new HBox();

		HBox bombCounter = new HBox();
		Digit hundredth = new Digit();
		Digit tenth = new Digit();
		tenth.setGraphic(tenth.digit[1]);
		Digit oneth = new Digit();
		bombCounter.getChildren().addAll(hundredth,tenth,oneth);

		HBox timer = new HBox();
		Digit timerHundredth = new Digit();
		Digit timerTenth = new Digit();
		Digit timerOneth = new Digit();
		timer.getChildren().addAll(timerHundredth, timerTenth, timerOneth);

		Region r1 = new Region();				
		Region r2 = new Region();
		HBox.setHgrow(r1, Priority.ALWAYS);		
		HBox.setHgrow(r2, Priority.ALWAYS);
		//HBox.setHgrow(smiley, Priority.ALWAYS);
		//topMenu.setPrefHeight(40);

		topMenu.setStyle("-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:3; -fx-border-radius: 0.001;");
		topMenu.getChildren().addAll(bombCounter, r1, smiley, r2, timer);

		///////////////////////////////////////Minesweeper Part//////////////////////////////////////////////////
		HBox minesweeperHbox = new HBox();
		minesweeperHbox.getChildren().add(minesweeper);
		minesweeperHbox.setStyle("-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:3; -fx-border-radius: 0.001;");
		minesweeper.setAlignment(Pos.TOP_CENTER);
		buttons= new MineButton[chosenLevel.getRow()][chosenLevel.getCol()];

		mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
		mine = settingMine(mine);	

		PlayGame(smiley,tenth,oneth);

		timeline = new Timeline(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				sec ++;
				timerHundredth.setGraphic(timerHundredth.digit[sec/100]);
				timerTenth.setGraphic(timerTenth.digit[(sec%100)/10]);
				timerOneth.setGraphic(timerOneth.digit[(sec%100)%10]);
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(false);

		if(isStarted) {
			System.out.println(" isStarted: " + isStarted);
			timeline.play();
			isStarted = false;
		}

		beginner.setOnAction(e -> {
			if(chosenLevel.getBombSize() != 10) {
				chosenLevel = new Difficulty(1);
				buttons= new MineButton[chosenLevel.getRow()][chosenLevel.getCol()];
				mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
				resetLevel(tenth, oneth, timerOneth, timerOneth, timerOneth, smiley);
				mine = settingMine(mine);
				minesweeper.getChildren().clear();
				PlayGame(smiley,tenth,oneth);
				setStageSize(theStage, chosenLevel.getLevel());
			}else {
				reset(timerOneth, timerOneth, timerOneth, timerOneth, timerOneth, smiley);
			}
		});

		easy.setOnAction(e -> {
			if(chosenLevel.getBombSize() != 10) {
				chosenLevel = new Difficulty(1);
				buttons= new MineButton[chosenLevel.getRow()][chosenLevel.getCol()];
				mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
				resetLevel(tenth, oneth, timerOneth, timerOneth, timerOneth, smiley);
				mine = settingMine(mine);
				minesweeper.getChildren().clear();
				PlayGame(smiley,tenth,oneth);
				setStageSize(theStage, chosenLevel.getLevel());
			}else {
				reset(timerOneth, timerOneth, timerOneth, timerOneth, timerOneth, smiley);
			}
		});

		intermediate.setOnAction(e -> {
			if(chosenLevel.getBombSize() != 40) {
				chosenLevel = new Difficulty(2);
				buttons= new MineButton[chosenLevel.getRow()][chosenLevel.getCol()];
				mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
				mine = settingMine(mine);
				resetLevel(tenth, oneth, timerOneth, timerOneth, timerOneth, smiley);
				minesweeper.getChildren().clear();
				PlayGame(smiley,tenth,oneth);
				setStageSize(theStage, chosenLevel.getLevel());
			}else {
				reset(timerOneth, timerOneth, timerOneth, timerOneth, timerOneth, smiley);
			}
		});

		notSoEasy.setOnAction(e -> {
			if(chosenLevel.getBombSize() != 40) {
				chosenLevel = new Difficulty(2);
				buttons= new MineButton[chosenLevel.getRow()][chosenLevel.getCol()];
				mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
				mine = settingMine(mine);
				resetLevel(tenth, oneth, timerOneth, timerOneth, timerOneth, smiley);
				minesweeper.getChildren().clear();
				PlayGame(smiley,tenth,oneth);
				setStageSize(theStage, chosenLevel.getLevel());
			}else {
				reset(timerOneth, timerOneth, timerOneth, timerOneth, timerOneth, smiley);
			}
		});

		expert.setOnAction(e -> {
			if(chosenLevel.getBombSize() != 99) {
				chosenLevel = new Difficulty(3);
				System.out.println("After Size of row: " + chosenLevel.getRow() + "After Size of col: " + chosenLevel.getCol());
				buttons= new MineButton[chosenLevel.getRow()][chosenLevel.getCol()];
				mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
				mine = settingMine(mine);
				resetLevel(tenth, oneth, timerOneth, timerOneth, timerOneth, smiley);
				minesweeper.getChildren().clear();
				PlayGame(smiley,tenth,oneth);
				setStageSize(theStage, chosenLevel.getLevel());
			}else {
				reset(timerOneth, timerOneth, timerOneth, timerOneth, timerOneth, smiley);
			}
		});

		hard.setOnAction(e -> {
			if(chosenLevel.getBombSize() != 99) {
				chosenLevel = new Difficulty(3);
				System.out.println("After Size of row: " + chosenLevel.getRow() + "After Size of col: " + chosenLevel.getCol());
				buttons= new MineButton[chosenLevel.getRow()][chosenLevel.getCol()];
				mine = new int [chosenLevel.getRow()][chosenLevel.getCol()];
				mine = settingMine(mine);
				resetLevel(tenth, oneth, timerOneth, timerOneth, timerOneth, smiley);
				minesweeper.getChildren().clear();
				PlayGame(smiley,tenth,oneth);
				setStageSize(theStage, chosenLevel.getLevel());
			}else {
				reset(timerOneth, timerOneth, timerOneth, timerOneth, timerOneth, smiley);
			}
		});

		smiley.setOnAction(e ->{
			reset(tenth, oneth,timerHundredth, timerTenth, timerOneth, smiley);
		});

		BorderPane pane = new BorderPane();
		pane.setTop(new CustomPane(difficulty));
		pane.setCenter(new CustomPane(topMenu));
		pane.setBottom(new CustomPane(minesweeperHbox));
		pane.setStyle("-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:3; -fx-border-radius: 0.001;");
		pane.setPadding(new Insets(5,5,5,5));

		VBox finalPane = new VBox();
		finalPane.getChildren().add(0, menu);
		finalPane.getChildren().add(1, pane);

		theStage.setTitle("MineSweeper");
		theStage.setScene(new Scene(finalPane));
		theStage.sizeToScene();
		theStage.setResizable(false);
		//theStage.getIcons().add(new Image("file:res/cutie.gif"));
		theStage.getIcons().add(new Image("file:res/pikachu.png"));
		theStage.show();
	}

	private void printHighScore() {
		String s = "";
		for(int i = 0; i < name.size(); i++) {
			if(i == 0)
				s =  "Beginner:\t\t" + name.get(0) + "\t\t\t" + shortestTime.get(0) + "\t\tseconds\n";
			else if(i == 1)
				s += "Intermediate:\t" + name.get(1) + "\t\t\t" + shortestTime.get(1) + "\t\tseconds\n";
			else if(i == 2)
				s += "Expert:\t\t" + name.get(2) + "\t\t\t" + shortestTime.get(2) + "\t\tseconds\n";

		}
		name = new ArrayList<String>();
		shortestTime = new ArrayList<Integer>();

		//JFrame frame = new JFrame("JOptionPane showMessageDialog example");
		//JOptionPane.showMessageDialog(frame, s, "High Scores", JOptionPane.INFORMATION_MESSAGE);

		Alert score = new Alert(AlertType.INFORMATION);
		score.setTitle("Fastest MineSweeper");
		score.setHeaderText(null);
		score.setContentText(s);
		score.showAndWait();

	}

	private void setStageSize(Stage theStage, int level) {
		int [] size = new int [2];
		if(level == 1) {
			size[0] = 271;
			size[1] = 402;
		}else if(level == 2) {
			size[0] = 511;
			size[1] = 642;
		}else if(level == 3) {
			size[0] = 991;
			size[1] = 642;
		}
		theStage.setWidth(size[0]);
		theStage.setHeight(size[1]);
	}

	public int [][] settingMine(int[][] mine) {
		int bombNum = chosenLevel.getBombSize();
		while(bombNum != 0) {
			int rand1 = (int)(Math.random() * chosenLevel.getRow());
			int rand2 = (int)(Math.random() * chosenLevel.getCol());
			if(mine[rand1][rand2] == 0) {
				mine [rand1][rand2] = 10;
				bombNum --;
			}
		}

//		for(int i = 0; i < mine.length ; i ++) {
//			for(int j = 0; j < mine[i].length ; j++) {
//				System.out.print(mine[i][j] + " ");
//			}
//			System.out.println();
//		}

		System.out.println();

		for(int i = 0 ; i < mine.length; i++) {
			for(int j = 0; j < mine[i].length; j++) {

				if(mine[i][j] == 10) {
					for(int n = -1; n <= 1; n++) {
						for(int m = -1; m <= 1; m++ ) {
							if(isValid(i+n, j+m))
								if(mine[i+n][j+m] != 10)
									mine[i+n][j+m]++;
						}
					}
				}
			}
		}

		//for(int[] row : mine)
			//System.out.println(Arrays.toString(row));
		return mine;
	}

	private boolean isValid(int r, int c) {
		return (r >= 0 && r < chosenLevel.getRow() && c >= 0 && c < chosenLevel.getCol());
	}

	private void changes(int n, MineButton yippie, Smiley smiley) {
		yippie.setGraphic(yippie.num[n]);
		yippie.isClicked = true;
		smiley.setGraphic(smiley.smile);
		count++;
	}

	private void PlayGame(Smiley smiley, Digit tenth, Digit oneth) {
		for(int i = 0; i < chosenLevel.getRow(); i++) {
			for(int j = 0; j < chosenLevel.getCol(); j++) {

				buttons[i][j] = new MineButton(mine[i][j]);
				MineButton yippie = buttons[i][j];
				int currentI = i;
				int currentJ = j;

				if(alive == true) {
					yippie.setOnMousePressed( e -> {
						if(alive == true && hasWon == false)  
							smiley.setGraphic(smiley.oSmile);				
					});
					yippie.setOnMouseReleased(e -> {
						if(alive == true && hasWon == false)  
							smiley.setGraphic(smiley.smile);
					});
				}

				yippie.setOnMouseClicked(e -> {
					isStarted = true;

					if(isStarted == true && hasWon == false && alive)
						timeline.play();

					if(firstMove && yippie.state != 0) {
						boolean isGreaterThan0 = true;
						while(isGreaterThan0) {
							if(yippie.state == 0) {
								isGreaterThan0 = false;
								firstMove = false;
							}else {
								for(int p = 0; p < mine.length; p++) {
									for(int q = 0; q < mine[p].length; q++) {
										mine[p][q] = 0;
									}
								}
								mine = settingMine(mine);
								for(int p = 0; p < mine.length; p++) {
									for(int q = 0; q < mine[p].length; q++) {
										buttons[p][q].state = mine[p][q];
									}
								}	
							}
						}
					}else
						firstMove = false;

					if(alive && hasWon == false && yippie.isFlagged == false && e.getButton() == MouseButton.PRIMARY) {
						////////Open 8 grids around////////
						if(yippie.isClicked == true) {
							System.out.println("checked");
							int num = 0;
							MineButton buttonAround = null;
							for(int n = -1; n <= 1; n++) {
								for(int m = -1; m <= 1; m++) {
									if(isValid(currentI+n, currentJ+m)) {
										buttonAround = buttons[currentI+n][currentJ+m]; 
										if(buttonAround.isFlagged == true)
											num++;
									}
								}
							}
							if(num >= yippie.state) {
								MineButton getOpened = null;
								for(int n = -1; n <= 1; n++) {
									for(int m = -1; m <= 1; m++) {
										if(isValid(currentI+n, currentJ+m)) {
											getOpened = buttons[currentI+n][currentJ+m];
											if(getOpened.state == 0) {
												recursion(yippie, currentI, currentJ);
											}
											else if(getOpened.state < 10 && getOpened.isClicked == false && getOpened.isFlagged == false) {
												getOpened.isClicked = true;
												getOpened.setGraphic(getOpened.num[getOpened.state]);
												count++;
											}else if(getOpened.state == 10 && getOpened.isFlagged == false) {
												getOpened.isClicked = true; 
												hasWon = false;
												alive = false;
											}

										}
									}
								}
								//System.out.println("count: " + count);
								if(alive == false) {
									revealBomb(buttons,smiley);

								}
							}

							/////////////////////end of opening 8 grids/////////////
						}else if(yippie.isFlagged == false && yippie.isClicked == false) {
							switch(yippie.state){
							case 0:
								isStarted = true;
								int n = yippie.state;
								changes(n, yippie, smiley);
								recursion(yippie,currentI,currentJ);
								break;
							case 1:
							case 2:
							case 3:
							case 4:
							case 5:
							case 6:
							case 7:
							case 8:
							case 9:
								isStarted = true;
								int m = yippie.state;
								changes(m, yippie, smiley);

								System.out.println("count: " + count);
								break;
							case 10: 
								alive = false;
								System.out.println(" isStarted: " + isStarted);
								revealBomb(buttons,smiley);
								yippie.setGraphic(yippie.num[10]);
								break;
							}
						}
					}else if(alive && hasWon == false && yippie.isClicked == false && e.getButton() == MouseButton.SECONDARY) {
						if(yippie.isFlagged == false) {
							isStarted = true;
							smiley.setGraphic(smiley.smile);
							yippie.setGraphic(yippie.flag);
							yippie.isFlagged = true;
							bomb --;
							count++;
							System.out.println("count: " + count);
						}else if(yippie.isFlagged == true) {
							isStarted = true;
							smiley.setGraphic(smiley.smile);
							yippie.setGraphic(yippie.imageCover);
							yippie.isFlagged = false;
							yippie.isClicked = false;
							bomb ++;
							count --;
							System.out.println("count: " + count);
						}
						if(bomb < 0)
							bomb = 0;
						tenth.setGraphic(tenth.digit[bomb/ 10]);
						oneth.setGraphic(oneth.digit[bomb % 10]);

					}
					if(count == chosenLevel.getCount())
						hasWon = true;
					if(hasWon == true) {
						smiley.setGraphic(smiley.sunglassesSmile);
						timeline.pause();
						int level = chosenLevel.getLevel() - 1;
						try {
							File scores = new File("src/scores.txt");
							Scanner in = new Scanner(scores);
							while(in.hasNext()) {
								String winnerName = in.next();
								int time = in.nextInt();
								name.add(winnerName);
								shortestTime.add(time);
							}
							if(sec < shortestTime.get(level) || shortestTime.get(level) == null) {
								TextInputDialog dialog = new TextInputDialog();
								dialog.setTitle("High Score");
								dialog.setHeaderText("You are the winner!");
								dialog.setContentText("Please enter your name:");
								Optional<String> newWinner = dialog.showAndWait();
								if(newWinner.isPresent()) {
									name.set(level, newWinner.get());
									shortestTime.set(level,sec);
								}
								System.out.println("why");
								writeFile();
								printHighScore();
							}
						}catch(FileNotFoundException ex) {
							System.out.println("No file");
						}
					}
				});

				minesweeper.add(yippie, i, j);
			}
		}
	}

	private void writeFile() {
		try {
			PrintWriter output = new PrintWriter("src/scores.txt");
			for(int i = 0; i < name.size(); i++) {
				output.println(name.get(i) + " " + shortestTime.get(i));
			}
			output.close();
		}catch(Exception e) {

		}
	}

	private void revealBomb(MineButton [][] buttons,Smiley smiley) {
		smiley.setGraphic(smiley.deadSmile);
		isStarted = false;
		timeline.pause();
		for(int x = 0; x < mine.length; x++) {
			for(int y = 0; y < mine[x].length; y++) {
				MineButton isABomb = buttons[x][y];
				if(mine[x][y] == 10 && isABomb.isClicked == true && isABomb.getGraphic() == isABomb.imageCover) {
					isABomb.setGraphic(isABomb.num[10]);
				}else if(mine[x][y] == 10 && isABomb.getGraphic() == isABomb.imageCover) {
					isABomb.setGraphic(isABomb.bomb);
				}else if(mine[x][y] != 10 && isABomb.isFlagged == true) {
					isABomb.setGraphic(isABomb.xBomb);
				}
			}
		}
	}

	private void recursion(MineButton getOpened, int currentI, int currentJ) {
		for(int n = -1; n <= 1; n++) {
			for(int m = -1; m <= 1; m++) {
				if(isValid(currentI+n, currentJ+m)) {
					getOpened = buttons[currentI+n][currentJ+m];
					if(getOpened.isFlagged == true || getOpened.isClicked == true) {
						continue;
					}
					else if(getOpened.state == 0 && getOpened.isClicked == false) {
						getOpened.isClicked = true;
						getOpened.setGraphic(getOpened.num[getOpened.state]);
						count++;
						recursion(getOpened,currentI+n,currentJ+m);
					}
					else if(getOpened.state > 0 && getOpened.isClicked == false) {
						getOpened.isClicked = true;
						getOpened.setGraphic(getOpened.num[getOpened.state]);
						count++;

					}
				}
			}
		}
		System.out.println("count: " + count);
	}

	public void reset(Digit tenth, Digit oneth, Digit timerHundredth, Digit timerTenth,Digit timerOneth, Smiley smiley) {
		alive = true;
		hasWon = false;
		count = 0;
		bomb = chosenLevel.getBombSize();
		isStarted = false;
		sec = 0;
		firstMove = true;
		tenth.setGraphic(tenth.digit[chosenLevel.bombSize/10]);
		oneth.setGraphic(oneth.digit[chosenLevel.bombSize%10]);
		timeline.stop();
		timerHundredth.setGraphic(timerHundredth.digit[0]);
		timerTenth.setGraphic(timerTenth.digit[0]);
		timerOneth.setGraphic(timerOneth.digit[0]);
		smiley.setGraphic(smiley.smile);
		for(int i = 0; i < mine.length; i++) {
			for(int j = 0; j < mine[i].length; j++) {
				mine[i][j] = 0;
				buttons[i][j].setGraphic(buttons[i][j].imageCover);
				buttons[i][j].state = 0;
				buttons[i][j].isClicked = false;
				buttons[i][j].isFlagged = false;
			}
		}
		mine = settingMine(mine);
		for(int i = 0; i < mine.length; i++) {
			for(int j = 0; j < mine[i].length; j++) {
				buttons[i][j].state = mine[i][j];
			}
		}
	}

	public void resetLevel(Digit tenth, Digit oneth, Digit timerHundredth, Digit timerTenth,Digit timerOneth, Smiley smiley) {
		alive = true;
		hasWon = false;
		count = 0;
		bomb = chosenLevel.getBombSize();
		isStarted = false;
		sec = 0;
		firstMove = true;
		System.out.println("it is here!!!!"  + "bomb size: " + bomb);
		tenth.setGraphic(tenth.digit[bomb/10]);
		oneth.setGraphic(oneth.digit[bomb%10]);
		timeline.stop();
		timerHundredth.setGraphic(timerHundredth.digit[0]);
		timerTenth.setGraphic(timerTenth.digit[0]);
		timerOneth.setGraphic(timerOneth.digit[0]);
		smiley.setGraphic(smiley.smile);
	}
}

class MineButton extends Button{
	boolean isClicked = false;
	boolean isFlagged = false;
	int state = 0;
	ImageView imageCover, bomb, xBomb, flag;
	ImageView [] num = new ImageView [11];

	public MineButton(int value) {
		state = value;
		double size = 30;
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);

		for(int i = 0; i < num.length; i++) {
			if(i == 10) {
				num[i] = new ImageView(new Image("file:res/mine-red.png"));
			}else
				num[i] = new ImageView(new Image("file:res/" + i + ".png"));
			num[i].setFitWidth(size);
			num[i].setFitHeight(size);
		}

		imageCover = new ImageView(new Image("file:res/cover.png"));
		bomb = new ImageView(new Image("file:res/mine-grey.png"));
		xBomb = new ImageView(new Image("file:res/mine-misflagged.png"));
		flag = new ImageView(new Image("file:res/flag.png"));

		imageCover.setFitWidth(size);
		imageCover.setFitHeight(size);

		bomb.setFitWidth(size);
		bomb.setFitHeight(size);

		xBomb.setFitWidth(size);
		xBomb.setFitHeight(size);

		flag.setFitWidth(size);
		flag.setFitHeight(size);

		setGraphic(imageCover);
	}
}

class Smiley extends Button{
	ImageView smile,oSmile,sunglassesSmile,deadSmile;

	public Smiley() {
		double size = 40;
		setMinWidth(size);
		setMaxWidth(size);
		setMinHeight(size);
		setMaxHeight(size);

		smile = new ImageView(new Image("file:res/face-smile.png"));
		oSmile = new ImageView(new Image("file:res/face-O.png"));
		sunglassesSmile = new ImageView(new Image("file:res/face-win.png"));
		deadSmile = new ImageView(new Image("file:res/face-dead.png"));

		smile.setFitWidth(size);
		smile.setFitHeight(size);

		oSmile.setFitWidth(size);
		oSmile.setFitHeight(size);

		sunglassesSmile.setFitWidth(size);
		sunglassesSmile.setFitHeight(size);

		deadSmile.setFitWidth(size);
		deadSmile.setFitHeight(size);

		setGraphic(smile);

	}
}

class Digit extends Button{
	ImageView [] digit = new ImageView [10];
	public Digit() {
		double size = 40;
		double width = 20;
		setMinWidth(width);
		setMaxWidth(width);
		setMinHeight(size);
		setMaxHeight(size);

		for(int i = 0; i < digit.length; i++) {
			digit[i] = new ImageView(new Image("file:res/digits/" + i + ".png"));
			digit[i].setFitWidth(width);
			digit[i].setFitHeight(size);
		}

		setGraphic(digit[0]);
	}
}

class Difficulty{
	int level;
	int [] gridSize = new int[2];
	int bombSize;
	int count;
	public Difficulty(int level) {
		if(level == 1) {
			this.level = level;
			gridSize[0] = 8;
			gridSize[1] = 8;
			bombSize = 10;

		}else if(level == 2) {
			gridSize[0] = 16;
			gridSize[1] = 16;
			bombSize = 40;
		}else if(level == 3) {
			gridSize[0] = 32;
			gridSize[1] = 16;
			bombSize = 99;
		}
		this.level = level;
		count = gridSize[0] * gridSize[1];
	}
	public int getRow() {
		return gridSize[0];
	}
	public int getCol() {
		return gridSize[1];
	}
	public int getBombSize() {
		return bombSize;
	}
	public int getCount() {
		return count;
	}
	public int getLevel() {
		return level;
	}
}

class CustomPane extends StackPane{
	public CustomPane(HBox pane){
		getChildren().add(pane);
		setPadding(new Insets(3,3,3,3));
		setAlignment(Pos.TOP_CENTER);
	}
}