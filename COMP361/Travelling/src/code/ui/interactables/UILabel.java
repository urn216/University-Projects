package code.ui.interactables;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Font;

import code.ui.UIInteractable;
import code.ui.UIActionGetter;

/**
* Class for making functional Buttons
*/
public class UILabel extends UIInteractable {
  private final UIActionGetter<?> getter;

  /**
  * Constructor for UILabels
  */
  public UILabel(String name, UIActionGetter<?> getter) {
    this.name = name;
    this.primeAction = ()->{};
    this.getter = getter;
    locked = true;
  }

  public void draw(Graphics2D g, float x, float y, float width, float height, Color bodyCol, Color textCol, Color inBodyCol, Color inTextCol, Color lockedBodyCol) {
    Font font = new Font("Copperplate", Font.BOLD, (int) Math.round((height/2)));
    metrics = g.getFontMetrics(font);
    g.setFont(font);
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;

    String text = name + getter.get().toString();

    g.setColor(textCol);
    g.drawString(text, x+(width-metrics.stringWidth(text))/2, y+((height - metrics.getHeight())/2) + metrics.getAscent());
  }
}
