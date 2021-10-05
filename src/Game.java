import java.util.Random;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Game extends Application {

  private static final int WIDTH = 800;
  private static final int HEIGHT = 800;

  private static final int TILE_SIZE = 40;
  private static final int X_TILES = 20;
  private static final int Y_TILES = 20;

  private static Tile[][] tiles = new Tile[X_TILES][Y_TILES];

  private static int numBombs = 80;

  public class Tile extends StackPane {

    private Text text;
    private int bombsTouching;
    private int x;
    private int y;
    private boolean containsBomb;
    private Rectangle border;

    public Tile(int x, int y, boolean containsBomb) {
      this.x = x;
      this.y = y;
      this.containsBomb = containsBomb;
      this.bombsTouching = 0;

      if (this.containsBomb) {
        this.text = new Text("B");
      } else
        this.text = new Text();

      border = new Rectangle(TILE_SIZE, TILE_SIZE);

      border.setStroke(Color.WHITE);
      setAlignment(Pos.CENTER);

      text.setFont(new Font(12));

      setOnMouseClicked(e -> {
        showTile();
      });


      getChildren().addAll(border, text);
    }

    private boolean isBomb() {
      return this.containsBomb;
    }



    private void showTile() {
      border.setFill(null);
      if (containsBomb) {
        gameOver();
      }
    }

    private void updateBombs(int num) {
      bombsTouching = num;
      text.setText(String.valueOf(bombsTouching));
    }


  }

  private int isTouchingBomb(int x, int y) {
    int numBombsTouching = 0;

    if (tiles[x][y].isBomb())
      return numBombsTouching;

    if (x != 0 && y != 0 && x != X_TILES - 1 && y != Y_TILES - 1) {

      if (tiles[x + 1][y - 1].isBomb()) {
        numBombsTouching++;
      }
      if (tiles[x + 1][y].isBomb()) {
        numBombsTouching++;
      }
      if (tiles[x + 1][y + 1].isBomb()) {
        numBombsTouching++;
      }

      if (tiles[x][y - 1].isBomb()) {
        numBombsTouching++;
      }
      if (tiles[x][y + 1].isBomb()) {
        numBombsTouching++;
      }

      if (tiles[x - 1][y - 1].isBomb()) {
        numBombsTouching++;
      }
      if (tiles[x - 1][y].isBomb()) {
        numBombsTouching++;
      }
      if (tiles[x - 1][y + 1].isBomb()) {
        numBombsTouching++;
      }
    }

    return numBombsTouching;
  }

  private Parent createContent() {
    Pane root = new Pane();
    root.setPrefSize(800, 800);

    Random rnd = new Random();
    int[][] bombIndex = new int[numBombs][2];

    for (int i = 0; i < numBombs; i++) {
      int temp = i;
      int x = rnd.nextInt(X_TILES + 1);
      int y = rnd.nextInt(Y_TILES + 1);

      // delete when create the edges
      if (x == 0 || x == 19 || y == 0 || y == 19) {
        i = temp - 1;
        continue;
      }

      bombIndex[i][0] = x;
      bombIndex[i][1] = y;
    }

    for (int i = 0; i < X_TILES; i++) {
      for (int j = 0; j < Y_TILES; j++) {
        Tile tile = null;
        if (!isBombTile(i, j, bombIndex)) {
          tile = new Tile(i, j, false);

        } else {
          tile = new Tile(i, j, true);
        }
        tiles[i][j] = tile;
        tile.setTranslateX(i * TILE_SIZE);
        tile.setTranslateY(j * TILE_SIZE);
        root.getChildren().addAll(tile);
      }
    }

    for (int i = 0; i < X_TILES; i++) {
      for (int j = 0; j < Y_TILES; j++) {
        int tileBombs = isTouchingBomb(i, j);
        if (tileBombs != 0)
          tiles[i][j].updateBombs(tileBombs);
      }
    }

    /*
     * int bombsNear = 0;
     * 
     * for (int i = 0; i < 20; i++) { bombsNear = 0;
     * 
     * // top if (!tiles[i][0].isBomb() && i != 0 && i != 19) { if (tiles[i][1].isBomb()) {
     * bombsNear++; } if (tiles[i - 1][0].isBomb()) { bombsNear++; } if (tiles[i + 1][0].isBomb()) {
     * bombsNear++; } if (tiles[i + 1][1].isBomb()) { bombsNear++; } if (tiles[i - 1][1].isBomb()) {
     * bombsNear++; }
     * 
     * } else if (!tiles[i][0].isBomb() && i == 0) { if (tiles[1][0].isBomb()) { bombsNear++; } if
     * (tiles[0][1].isBomb()) { bombsNear++; } if (tiles[1][1].isBomb()) { bombsNear++; } } else if
     * (!tiles[i][0].isBomb() && i == 19) { if (tiles[18][0].isBomb()) { bombsNear++; } if
     * (tiles[19][1].isBomb()) { bombsNear++; } if (tiles[18][1].isBomb()) { bombsNear++; } }
     * 
     * if (bombsNear != 0) tiles[i][0].updateBombs(bombsNear);
     * 
     * // bottom bombsNear = 0; if (!tiles[i][19].isBomb() && i != 0 && i != 19) { if
     * (tiles[i][18].isBomb()) { bombsNear++; } if (tiles[i - 1][19].isBomb()) { bombsNear++; } if
     * (tiles[i + 1][19].isBomb()) { bombsNear++; } if (tiles[i + 1][18].isBomb()) { bombsNear++; }
     * if (tiles[i - 1][18].isBomb()) { bombsNear++; }
     * 
     * } else if (!tiles[i][19].isBomb() && i == 0) { if (tiles[0][18].isBomb()) { bombsNear++; } if
     * (tiles[1][19].isBomb()) { bombsNear++; } if (tiles[1][18].isBomb()) { bombsNear++; } } else
     * if (!tiles[i][19].isBomb() && i == 19) { if (tiles[19][18].isBomb()) { bombsNear++; } if
     * (tiles[18][18].isBomb()) { bombsNear++; } if (tiles[18][19].isBomb()) { bombsNear++; } }
     * 
     * if (bombsNear != 0) tiles[i][0].updateBombs(bombsNear);
     * 
     * // left bombsNear = 0; if (!tiles[0][i].isBomb() && i != 0 && i != 19) { if (tiles[0][i -
     * 1].isBomb()) { bombsNear++; } if (tiles[1][i].isBomb()) { bombsNear++; } if (tiles[0][i +
     * 1].isBomb()) { bombsNear++; } if (tiles[1][i + 1].isBomb()) { bombsNear++; } if (tiles[1][i -
     * 1].isBomb()) { bombsNear++; } }
     * 
     * if (bombsNear != 0) tiles[0][i].updateBombs(bombsNear);
     * 
     * // right bombsNear = 0; if (!tiles[19][i].isBomb() && i != 0 && i != 19) { if (tiles[19][i -
     * 1].isBomb()) { bombsNear++; } if (tiles[19][i].isBomb()) { bombsNear++; } if (tiles[19][i +
     * 1].isBomb()) { bombsNear++; } if (tiles[19][i + 1].isBomb()) { bombsNear++; } if (tiles[19][i
     * - 1].isBomb()) { bombsNear++; } }
     * 
     * if (bombsNear != 0) tiles[19][i].updateBombs(bombsNear); }
     */

    return root;

  }

  public static boolean isBombTile(int x, int y, int[][] bombIndex) {
    for (int i = 0; i < bombIndex.length; i++) {
      if (bombIndex[i][0] == x) {
        if (bombIndex[i][1] == y) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void start(Stage stage) throws Exception {
    Scene scene = new Scene(createContent());
    stage.setScene(scene);
    stage.show();
  }

  private void gameOver() {
    Alert alert = null;


    alert = new Alert(AlertType.CONFIRMATION, "You Lost");
    alert.showAndWait().filter(e -> e == ButtonType.OK);
    System.exit(0);
  }

  public static void main(String[] args) {
    launch(args);
  }


}
