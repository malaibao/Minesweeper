import java.util.Arrays;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class newMineSweeper extends Application {
	int [][] mine;
	MineButton [][] buttons;
	boolean alive;
	boolean hasWon;
	boolean isStarted;
	int count;
	int bomb;
	Timeline timeline;
	int millis;

	public static void main(String []args) {
		launch(args);
	}

	@Override
	public void start(Stage theStage){
		mine = new int [8][8];
		alive = true;
		hasWon = false;
		isStarted= false;
		count = 0;
		bomb = 10;

		//Smiley Part
		Smiley smiley = new Smiley();
		HBox topMenu = new HBox();
		/*
		smiley.setOnAction(e ->{
			alive = true;
			hasWon = false;
			count = 0;
			mine = new int [8][8];
			mine = settingMine(mine);
			smiley.setGraphic(smiley.smile);
			for(int i = 0; i < mine.length; i++) {
				for(int j = 0; j < mine[i].length; j++) {
					buttons[i][j].setGraphic(buttons[i][j].imageCover);
					//buttons[i][j].state = 0;
					//buttons[i][j].isClicked = false;
					//buttons[i][j].isFlagged = false;
				}
			}
		});
		 */
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
		topMenu.setStyle("-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:3; -fx-border-radius: 0.001;");
		topMenu.getChildren().addAll(bombCounter, r1, smiley, r2, timer);

		//Minesweeper Part
		GridPane minesweeper = new GridPane();
		HBox minesweeperHbox = new HBox();
		minesweeperHbox.getChildren().add(minesweeper);
		minesweeperHbox.setStyle("-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:3; -fx-border-radius: 0.001;");
		minesweeper.setAlignment(Pos.TOP_CENTER);
		buttons= new MineButton[8][8];

		mine = new int [8][8];
		mine = settingMine(mine);		

		for(int i = 0; i < 8; i++) {
			for(int j = 0; j < 8; j++) {

				buttons[i][j] = new MineButton();
				MineButton yippie = buttons[i][j];

				int r = mine[i][j];

				if(alive == true) { // && yippie.isFlagged == false && yippie.isClicked == false) {
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

					if(alive && hasWon == false && yippie.isFlagged == false && yippie.isClicked == false && e.getButton() == MouseButton.PRIMARY) {
						if(yippie.isFlagged == false) {
							yippie.state += r;
							switch(yippie.state){
							case 0:
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
								int n = yippie.state;
								changes(n, yippie, smiley);
								break;
							case 10: 
								smiley.setGraphic(smiley.deadSmile);
								alive = false;
								isStarted = false;
								timeline.pause();
								System.out.println(" isStarted: " + isStarted);
								for(int x = 0; x < mine.length; x++) {
									for(int y = 0; y < mine[x].length; y++) {
										MineButton isABomb = new MineButton();
										if(mine[x][y] == 10) {
											isABomb = buttons[x][y];
											isABomb.setGraphic(isABomb.bomb);
										}
									}
								}
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
						}else if(yippie.isFlagged == true) {
							isStarted = true;
							smiley.setGraphic(smiley.smile);
							yippie.setGraphic(yippie.imageCover);
							yippie.isFlagged = false;
							yippie.isClicked = false;
							bomb ++;
							count --;
						}
						if(bomb < 0)
							bomb = 0;
						tenth.setGraphic(tenth.digit[bomb/ 10]);
						oneth.setGraphic(oneth.digit[bomb % 10]);

					}
					if(count == 64)
						hasWon = true;
					if(hasWon == true)
						smiley.setGraphic(smiley.sunglassesSmile);
				});

				minesweeper.add(yippie, i, j);
			}
		}

		timeline = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				timeChange(hundredth, tenth, oneth);
			}
		}));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(false);

		if(isStarted) {
			System.out.println(" isStarted: " + isStarted);
			timeline.play();
			isStarted = false;
		}


		smiley.setOnAction(e ->{
			try {
				start(theStage);
			}catch(Exception ex) {	
			}
		});

		BorderPane pane = new BorderPane();
		pane.setTop(new CustomPane(topMenu));
		pane.setCenter(new CustomPane(minesweeperHbox));
		pane.setStyle("-fx-background-color: #bfbfbf; -fx-border-color: #787878 #fafafa #fafafa #787878; -fx-border-width:3; -fx-border-radius: 0.001;");
		pane.setPadding(new Insets(5,5,5,5));

		theStage.setTitle("MineSweeper");
		theStage.setScene(new Scene(pane));
		theStage.show();

	}

	public int [][] settingMine(int[][] mine) {
		int bombNum = 10;
		while(bombNum != 0) {
			int rand1 = (int)(Math.random() * 8);
			int rand2 = (int)(Math.random() * 8);
			if(mine[rand1][rand2] == 0) {
				mine [rand1][rand2] = 10;
				bombNum --;
			}
		}

		for(int i = 0; i < mine.length ; i ++) {
			for(int j = 0; j < mine[i].length ; j++) {
				System.out.print(mine[i][j] + " ");
			}
			System.out.println();
		}

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

		for(int[] row : mine)
			System.out.println(Arrays.toString(row));
		return mine;
	}

	private boolean isValid(int r, int c) {
		return (r >= 0 && r < 8 && c >= 0 && c < 8);
	}

	private void changes(int n, MineButton yippie, Smiley smiley) {
		yippie.setGraphic(yippie.num[n]);
		yippie.isClicked = true;
		smiley.setGraphic(smiley.smile);
		count++;
	}

	private void timeChange(Digit hundredth, Digit tenth, Digit oneth) {
		int a = 0; //sec		
		a = millis/1000;
		System.out.println("value of sec : " + a);
		hundredth.setGraphic(hundredth.digit[a/100]);
		tenth.setGraphic(tenth.digit[(a/10)%10]);
		oneth.setGraphic(oneth.digit[a%10]);
	}
}

class MineButton extends Button{
	boolean isClicked = false;
	boolean isFlagged = false;
	int state = 0;
	ImageView imageCover, bomb, xBomb, flag;
	ImageView [] num = new ImageView [11];

	public MineButton() {
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
		double size = 25;
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
		double size = 25;
		double width = 10;
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

class CustomPane extends StackPane{
	public CustomPane(HBox pane){
		getChildren().add(pane);
		setPadding(new Insets(5,5,5,5));
	}
}