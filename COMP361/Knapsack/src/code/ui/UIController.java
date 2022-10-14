package code.ui;

import code.ui.interactables.UISlider;
import code.ui.interactables.UITextfield;

import code.Core;

import java.awt.event.KeyEvent;

import java.util.*;
import java.awt.Graphics2D;

/**
* UI class
*/
public class UIController {

  private HashMap<String, UIPane> panes;
  private UIPane current;

  private UIInteractable highlighted = null;
  private UITextfield activeTextfield = null;
  private UISlider activeSlider = null;

  public UIController() {
    panes = new HashMap<String, UIPane>();
  }

  public void putPane(String s, UIPane p) {
    panes.put(s, p);
  }

  public UIPane getPane(String name) {
    return panes.get(name);
  }

  public UIPane setCurrent(String name) {
    current = panes.get(name);
    current.reset();
    return current;
  }

  public void setMode(UIState name) {
    current.setMode(name);
  }

  public UIState getMode() {
    return current.getMode();
  }

  public void back() {
    current.retMode();
  }

  public void transOut() {
    current.transOut();
  }

  public void transIn() {
    current.transIn();
  }

  public boolean isTransitioning() {
    return current.isTransitioning();
  }

  public UIInteractable getClickable(double x, double y) {
    return current == null ? null : current.getClickable(x, y);
  }

  public void resetClickables() {
    current.resetClickables();
  }

  /**
  * Sets the highlighted UIInteractable
  *
  * @param highlighted The UIInteractable to set as highlighted
  */
  public void setHighlighted(UIInteractable highlighted) {if (highlighted == null || !highlighted.isLocked()) this.highlighted = highlighted;}

  /**
  * Gets the highlighted UIInteractable
  *
  * @return The UIInteractable currently highlighted
  */
  public UIInteractable getHighlighted() {return this.highlighted;}

  /**
  * Sets the active UITextfield
  *
  * @param textfield The UITextfield to set as active
  */
  public void setActiveTextfield(UITextfield textfield) {this.activeTextfield = textfield;}

  /**
  * Gets the active UITextfield
  *
  * @return The UITextfield currently active
  */
  public UITextfield getActiveTextfield() {return this.activeTextfield;}

  public void cursorMove(int x, int y) {
    setHighlighted(getClickable(x, y));
  }

  public void press() {
    if (highlighted == null || highlighted.isLocked()) return;
    highlighted.setIn();
    if (highlighted instanceof UISlider) activeSlider = (UISlider)highlighted;
  }

  public void release() {
    selectInteractable(highlighted);
    activeSlider = null;
  }

  public void draw(Graphics2D g, int screenSizeX, int screenSizeY) {
    if (current != null) {current.draw(g, 1.0*screenSizeY/(Core.DEFAULT_SCREEN_SIZE_Y*1.0), screenSizeX, screenSizeY, highlighted);}
  }

  /**
  * Types a key into the activeTextField. Assumes activeTextField is not null
  *
  * @param e The KeyEvent to type into activeTextField
  */
  public void typeKey(KeyEvent e) {
    int keyCode = e.getKeyCode();
    if (keyCode == KeyEvent.VK_ENTER) {
      if (highlighted == null || highlighted == activeTextfield) {
        activeTextfield.enterAct();
        return;
      }
    }
    if (keyCode >= 32 && keyCode <= 122) {activeTextfield.print(e.getKeyChar()); return;}
    if (keyCode == KeyEvent.VK_BACK_SPACE) {activeTextfield.backspace(); return;}
  }

  /**
  * Changes the value held in the activeSlider.
  *
  * @param x The x coordinate of the cursor
  */
  public void useSlider(int x) {
    if (activeSlider != null) activeSlider.moveNode(x);
  }

  /**
  * Activates a given UIInteractable
  *
  * @param interact The UIInteractable to activate
  */
  private void selectInteractable(UIInteractable interact) {
    activeTextfield = null;
    if (interact != null && interact.isIn()) {
      interact.primeAct();
      if (interact instanceof UITextfield) return;
    }
    resetClickables();
  }
}
