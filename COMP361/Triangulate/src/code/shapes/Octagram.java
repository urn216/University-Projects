package code.shapes;

import java.awt.Color;

import code.Polygon;
import code.Vector2;

public class Octagram implements Shape {
  public static Polygon generate() {
    double c = 0.5;
    double a = 0.1;
    double p = 0.4/a;
    return new Polygon(Color.white,
      new Vector2(c+a*Math.cos(Math.PI*( 0.0/8.0)), c+a*Math.sin(Math.PI*( 0.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*( 1.0/8.0)), c+p*a*Math.sin(Math.PI*( 1.0/8.0))),
      new Vector2(c+a*Math.cos(Math.PI*( 2.0/8.0)), c+a*Math.sin(Math.PI*( 2.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*( 3.0/8.0)), c+p*a*Math.sin(Math.PI*( 3.0/8.0))),
      new Vector2(c+a*Math.cos(Math.PI*( 4.0/8.0)), c+a*Math.sin(Math.PI*( 4.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*( 5.0/8.0)), c+p*a*Math.sin(Math.PI*( 5.0/8.0))),
      new Vector2(c+a*Math.cos(Math.PI*( 6.0/8.0)), c+a*Math.sin(Math.PI*( 6.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*( 7.0/8.0)), c+p*a*Math.sin(Math.PI*( 7.0/8.0))),
      new Vector2(c+a*Math.cos(Math.PI*( 8.0/8.0)), c+a*Math.sin(Math.PI*( 8.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*( 9.0/8.0)), c+p*a*Math.sin(Math.PI*( 9.0/8.0))),
      new Vector2(c+a*Math.cos(Math.PI*(10.0/8.0)), c+a*Math.sin(Math.PI*(10.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*(11.0/8.0)), c+p*a*Math.sin(Math.PI*(11.0/8.0))),
      new Vector2(c+a*Math.cos(Math.PI*(12.0/8.0)), c+a*Math.sin(Math.PI*(12.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*(13.0/8.0)), c+p*a*Math.sin(Math.PI*(13.0/8.0))),
      new Vector2(c+a*Math.cos(Math.PI*(14.0/8.0)), c+a*Math.sin(Math.PI*(14.0/8.0))),
      new Vector2(c+p*a*Math.cos(Math.PI*(15.0/8.0)), c+p*a*Math.sin(Math.PI*(15.0/8.0)))
    );
  }
}
