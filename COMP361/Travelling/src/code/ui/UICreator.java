package code.ui;

import code.Core;

import code.math.Vector2;

import code.ui.elements.*;
import code.ui.interactables.*;

import java.awt.Color;
import java.awt.Font;

public class UICreator {
  // private static final UIElement VIRTUAL_KEYBOARD = new ElemKeyboard();

  /**
  * Creates the UI pane for the main menu.
  */
  public static UIPane createMain(Core c, UIController ui) {
    UIPane mainMenu = new UIPane();
    boolean[] Tties = {true, false, false, false};
    UIElement title = new ElemTitle(
    new Vector2(0, 0),
    new Vector2(1, 0.14),
    "Travelling Salesman Problem",
    Font.BOLD,
    75,
    ColourPacks.DEFAULT_COLOUR_PACK,
    Tties
    );

    boolean[] Bties = {true, false, true, true};
    UIInteractable[] topButtons = {
      new UIButton("Simulated Annealing", ()->{ui.setMode(UIState.DATASET); c.setMethod(0);}),
      new UIButton("Genetic Algorithm", ()->{ui.setMode(UIState.DATASET); c.setMethod(1);}),
    };
    UIElement outPanel = new ElemButtons(
    new Vector2(0.25, 0.32),
    new Vector2(0.75, 0.5),
    72,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    topButtons,
    Bties
    );

    UIInteractable[] GEN_PROBButtons = {
      new UILabel("Select a dataset for ", ()->{return c.getMethod();}),
      new UIButton("a280", ()->{ui.setMode(UIState.DISPLAY); c.setTSP("../a280.tsp");}),
      new UIButton("Cancel", ui::back),
    };
    UIElement GEN_PROBPanel = new ElemButtons(
    new Vector2(0.18, 0.24),
    new Vector2(0.82, 0.5),
    72,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    GEN_PROBButtons,
    Bties
    );

    boolean[] Dties = {false, true, false, false};
    UIInteractable[] DISPLAYButtons = {
      new UIButton("Return to Menu", () -> {ui.back(); c.resetTSP();}),
    };
    UIElement DISPLAYPanel = new ElemButtons(
    new Vector2(0.28, 0.906),
    new Vector2(0.72, 1.0),
    72,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    DISPLAYButtons,
    Dties
    );

    mainMenu.addElement(title);
    mainMenu.addElement(outPanel);
    mainMenu.addElement(GEN_PROBPanel);
    mainMenu.addElement(DISPLAYPanel);
    mainMenu.addMode(UIState.DEFAULT, title);
    mainMenu.addMode(UIState.DEFAULT, outPanel);
    mainMenu.addMode(UIState.DATASET, title, UIState.DEFAULT);
    mainMenu.addMode(UIState.DATASET, GEN_PROBPanel);
    mainMenu.addMode(UIState.DISPLAY, DISPLAYPanel, UIState.DEFAULT);

    return mainMenu;
  }


}

class ColourPacks {
  public static final Color DEFAULT_BACKGROUND = new Color(100, 100, 100, 127);
  public static final Color DEFAULT_SCREEN_TINT = new Color(50, 50, 50, 127);
  public static final Color DEFAULT_BUTTON_OUT_ACC = new Color(200, 200, 200);
  public static final Color DEFAULT_BUTTON_BACKGROUND = new Color(160, 160, 160, 160);
  public static final Color DEFAULT_BUTTON_IN_ACC = new Color(0, 255, 255);
  public static final Color DEFAULT_BUTTON_LOCKED = new Color(180, 180, 180);
  public static final Color DEFAULT_BUTTON_HOVER = new Color(0, 180, 180);
  public static final Color[] DEFAULT_COLOUR_PACK = {
    DEFAULT_BACKGROUND, DEFAULT_SCREEN_TINT, DEFAULT_BUTTON_OUT_ACC, DEFAULT_BUTTON_BACKGROUND, DEFAULT_BUTTON_IN_ACC, DEFAULT_BUTTON_LOCKED, DEFAULT_BUTTON_HOVER
  };
}
