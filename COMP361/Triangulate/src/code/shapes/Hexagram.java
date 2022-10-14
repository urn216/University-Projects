package code.shapes;

import java.awt.Color;

import code.Polygon;
import code.Vector2;

public class Hexagram implements Shape {
  public static Polygon generate() {
    double c = 0.5;
    double a = 0.25;
    double p = 0.4/a;
    return new Polygon(Color.white,
      new Vector2(c-a, c),
        new Vector2(c+p*a*Math.cos(Math.PI*(5.0/6.0)), c+p*a*Math.sin(Math.PI*(5.0/6.0))),
      new Vector2(c+a*Math.cos(Math.PI*(2.0/3.0)), c+a*Math.sin(Math.PI*(2.0/3.0))),
        new Vector2(c+p*a*Math.cos(Math.PI*(3.0/6.0)), c+p*a*Math.sin(Math.PI*(3.0/6.0))),
      new Vector2(c+a*Math.cos(Math.PI/3.0), c+a*Math.sin(Math.PI/3.0)),
        new Vector2(c+p*a*Math.cos(Math.PI*(1.0/6.0)), c+p*a*Math.sin(Math.PI*(1.0/6.0))),
      new Vector2(c+a, c),
        new Vector2(c+p*a*Math.cos(Math.PI*(11.0/6.0)), c+p*a*Math.sin(Math.PI*(11.0/6.0))),
      new Vector2(c+a*Math.cos(Math.PI*(5.0/3.0)), c+a*Math.sin(Math.PI*(5.0/3.0))),
        new Vector2(c+p*a*Math.cos(Math.PI*(9.0/6.0)), c+p*a*Math.sin(Math.PI*(9.0/6.0))),
      new Vector2(c+a*Math.cos(Math.PI*(4.0/3.0)), c+a*Math.sin(Math.PI*(4.0/3.0))),
        new Vector2(c+p*a*Math.cos(Math.PI*(7.0/6.0)), c+p*a*Math.sin(Math.PI*(7.0/6.0)))
    );
  }
}
