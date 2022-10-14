package code.shapes;

import java.awt.Color;

import code.Vector2;
import code.Polygon;

public class Ngon {
  public static Polygon generate(int num) {
    double c = 0.5;
    double a = 0.4;
    Vector2[] ps = new Vector2[num];

    for (double i = 0; i < ps.length; i++) {
      double ang = i/(ps.length/2.0);
      ps[(int)i] = new Vector2(c+a*Math.cos(Math.PI*ang), c+a*Math.sin(Math.PI*ang));
    }

    return new Polygon(Color.white, ps);
  }
}
