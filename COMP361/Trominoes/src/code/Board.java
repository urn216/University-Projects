package code;

import java.util.Arrays;

import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Color;

/**
* The possible directions for a tromino to face
*/
enum Dir {
  /** Pointing to the upper right */
  UR,
  /** Pointing to the upper left */
  UL,
  /** Pointing to the lower right */
  LR,
  /** Pointing to the lower left */
  LL
}

/**
* A class representing a 2 dimensional integer vector
*/
class Vector2 {
  public final int x, y;

  /**
  * Constructs an integer vector with 2 dimensions
  *
  * @param x the first coordinate
  *
  * @param y the second coordinate
  */
  public Vector2(int x, int y) {
    this.x = x;
    this.y = y;
  }
}

/**
* A class representing a board of trominoes
*/
public class Board {
  private final int bSize;

  private Color[] board;
  private String trominoes = "";

  /**
  * Constructs a board of size n with a blank tile at a given set of coordinates
  *
  * @param n the width of the board. Must be a power of 2!
  *
  * @param x the x coordinate of the blank tile
  *
  * @param y the y coordinate of the blank tile
  */
  public Board(int n, int x, int y) {
    bSize = n;
    board = new Color[n*n];
    Arrays.fill(board, Color.black);

    tile(n, new Vector2(x, y), new Vector2(n/2, n/2));
  }

  /**
  * tiles a board or section of a board with trominoes through recursion
  *
  * @param n the width of the board. Must be a power of 2!
  *
  * @param L the x and y coordinates of the blank tile
  *
  * @param C the x and y coordinates of the centre of this segment of the board
  */
  private void tile(int n, Vector2 L, Vector2 C) {

    //base case. put a tromino in this spot, facing away from the empty tile
    if (n == 2) {
      placeTromino(C.x, C.y, Dir.values()[L.x%2 + 2*(L.y%2)]);
      return;
    }

    //otherwise, split the board into four equal sub-boards
    int N = n/2;
    //and place a tromino in the centre of this board, facing away from the empty tile
    int dir = L.x/N + 2*(L.y/N);
    placeTromino(C.x, C.y, Dir.values()[dir]);

    //generate an array of vectors for the centre four spots of this board (where the tromino we just placed is)
    Vector2[] ms = {
      new Vector2(N-1, N-1),
      new Vector2( 0 , N-1),
      new Vector2(N-1,  0 ),
      new Vector2( 0 ,  0 )
    };

    //replace whichever of our m vectors isn't under the centre tromino with the empty tile
    ms[dir] = new Vector2(L.x%N, L.y%N);

    //tile recursively for each of our four new sub-boards, giving each its corresponding m vector - or 'empty tile'.
    tile(N, ms[3], new Vector2(C.x+N/2, C.y+N/2));
    tile(N, ms[2], new Vector2(C.x-N/2, C.y+N/2));
    tile(N, ms[1], new Vector2(C.x+N/2, C.y-N/2));
    tile(N, ms[0], new Vector2(C.x-N/2, C.y-N/2));
  }

  /**
  * Places a tromino in a given spot facing a given direction
  *
  * @param x the x coordinate of the tromino
  *
  * @param y the y coordinate of the tromino
  *
  * @param d the direction towards which the tromino should face
  */
  private void placeTromino(int x, int y, Dir d) {
    trominoes += (x + " " + y + " " + d + "\n");
    Color c = new Color(Color.HSBtoRGB((float)Math.random(), 1, 1));
    if (d!=Dir.UR) board[(y-1)*bSize+x-1] = c;
    if (d!=Dir.UL) board[(y-1)*bSize+x] = c;
    if (d!=Dir.LR) board[(y)*bSize+x-1] = c;
    if (d!=Dir.LL) board[(y)*bSize+x] = c;
  }

  public String toString() {
    return trominoes;
  }

  /**
  * Draws the board
  *
  * @param g the Graphics2D object to draw to
  *
  * @param sSize the screen size vertically. (we're drawing a square, so we restrict it to the window height)
  */
  public void draw(Graphics2D g, double sSize) {
    //find the width of each tile
    double width = sSize/bSize;

    //for each tile, we draw it in its correct spot, at the correct size, in the colour of the tromino present
    for (int i = 0; i < bSize; i++) {
      for (int j = 0; j < bSize; j++) {
        g.setColor(board[i*bSize+j]);
        g.fill(new Rectangle2D.Double(j*width, sSize-(i+1)*width, width, width));

        //optionally, give each tile a black outline. I think I like it better without. But I've done all the assignment questions with outlines enabled
        // g.setColor(Color.black);
        // g.draw(new Rectangle2D.Double(j*width, sSize-(i+1)*width, width, width));
      }
    }

    //then, we print out all the trominoes to the screen next to the board, in the order they were placed
    g.setColor(Color.white);
    String[] lines = trominoes.split("\n");
    double spacing = sSize/lines.length;
    double pos = spacing/2;
    for (String s : lines) {
      g.drawString(s, (int)(sSize*1.1), (int)pos);
      pos+=spacing;
    }
  }
}
