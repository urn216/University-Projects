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
    new Vector2(0.28, 0),
    new Vector2(0.72, 0.14),
    "KNAPSACK",
    Font.BOLD,
    75,
    ColourPacks.DEFAULT_COLOUR_PACK,
    Tties
    );

    boolean[] Bties = {true, false, true, true};
    UIInteractable[] topButtons = {
      new UIButton("0-1", ()->ui.setMode(UIState.O_1)),
      new UIButton("0-N", ()->ui.setMode(UIState.O_N)),
    };
    UIElement outPanel = new ElemButtons(
    new Vector2(0.4, 0.32),
    new Vector2(0.6, 0.5),
    72,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    topButtons,
    Bties
    );

    UIInteractable[] O_1Buttons = {
      new UIButton("Dynamic Programming", ()->{ui.setMode(UIState.GEN_PROB); c.setKnapsack(0, 1);}),
      new UIButton("Back", ui::back),
    };
    UIElement O_1Panel = new ElemButtons(
    new Vector2(0.28, 0.32),
    new Vector2(0.72, 0.5),
    72,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    O_1Buttons,
    Bties
    );

    UIInteractable[] O_NButtons = {
      new UIButton("Brute Force", ()->{ui.setMode(UIState.GEN_PROB); c.setKnapsack(1, 0);}),
      new UIButton("Dynamic Programming", ()->{ui.setMode(UIState.GEN_PROB); c.setKnapsack(1, 1);}),
      new UIButton("Graph Search", ()->{ui.setMode(UIState.GEN_PROB); c.setKnapsack(1, 2);}),
      new UIButton("Back", ui::back),
    };
    UIElement O_NPanel = new ElemButtons(
    new Vector2(0.28, 0.32),
    new Vector2(0.72, 0.66),
    72,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    O_NButtons,
    Bties
    );

    UIInteractable[] GEN_PROBButtons = {
      new UILabel("Create a scenario for ", ()->{return c.getKnapsack();}),
      new UIButton("Random Problem", ()->{ui.setMode(UIState.DISPLAY); c.runKnapsackRandom();}),
      new UIButton("Cancel", ui::back),
    };
    UIElement GEN_PROBPanel = new ElemButtons(
    new Vector2(0.28, 0.24),
    new Vector2(0.72, 0.5),
    72,
    15,
    ColourPacks.DEFAULT_COLOUR_PACK,
    GEN_PROBButtons,
    Bties
    );

    boolean[] Dties = {false, true, false, false};
    UIInteractable[] DISPLAYButtons = {
      new UIButton("Return to Menu", () -> {ui.back(); c.setKnapsack(-1, -1);}),
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
    mainMenu.addElement(O_1Panel);
    mainMenu.addElement(O_NPanel);
    mainMenu.addElement(GEN_PROBPanel);
    mainMenu.addElement(DISPLAYPanel);
    mainMenu.addMode(UIState.DEFAULT, title);
    mainMenu.addMode(UIState.DEFAULT, outPanel);
    mainMenu.addMode(UIState.O_1, title, UIState.DEFAULT);
    mainMenu.addMode(UIState.O_1, O_1Panel);
    mainMenu.addMode(UIState.O_N, title, UIState.DEFAULT);
    mainMenu.addMode(UIState.O_N, O_NPanel);
    mainMenu.addMode(UIState.GEN_PROB, title, UIState.DEFAULT);
    mainMenu.addMode(UIState.GEN_PROB, GEN_PROBPanel);
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
