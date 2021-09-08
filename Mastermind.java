
import tester.*;
import java.awt.Color;
import javalib.worldimages.*;
import javalib.worldcanvas.*;
import javalib.funworld.World;
import javalib.funworld.WorldScene;
import java.util.Random;

interface ISequence {
  // represent a sequence of Colors in list format

  // Gets the length of the sequence
  int length();

  // compares two sequences
  boolean compareSequence(ISequence toComp);

  // Returns the Color at given index
  Color grab(int index);

  // Returns the index of the first occurrence of the given value
  int indexOf(Color toFind);

  // Returns new sequence with Color at index removed
  ISequence remove(int index);

  // compare this ISequence to the given ConsSequence
  boolean compareCons(ConsSequence toComp);

  // compare this ISequence to the given MtSequence
  boolean compareEmpty(MtSequence toComp);

  // Check if this Sequence contains the given Color
  boolean contains(Color toFind);

  // Returns the number of exact matches to given sequence
  int countExact(ISequence toComp);

  // Calls countExact on the rest of this passing toComp
  int countExactHelper(ISequence toComp);

  // Returns the number of inexact matches to given sequence
  int countInExact(ISequence toComp);

  // Compares the first value in the sequence to the given Color
  boolean compFirst(Color toComp);

  // Finds the total number of matches between this sequence and the given
  // sequence
  int countMatches(ISequence toComp);

  // Returns a new ISequence with all the duplicates found in toComp removed
  ISequence removeDupes(ISequence toComp);

  // draw this ISequence
  WorldImage drawSequence();

  // draw the score (Exact, inexact matches) comparing this ISequence to the given
  // pattern
  WorldImage drawScore(ISequence pattern);

  // add the given Color into this ISequence just before the MtSequence at the end
  // (helper for dealing with player input)
  ISequence addBeforeEnd(Color c);
}

class ConsSequence implements ISequence {
  // represent a sequence with multiple colors
  Color first; // a color in the sequence
  ISequence rest; // the rest of the sequence

  ConsSequence(Color first, ISequence rest) {
    this.first = first;
    this.rest = rest;
  }
  /*
   * ConsSequence Template:
   * 
   * Fields: - Color first - ISequence rest
   * 
   * Methods: //the length of this ConsSequence int length()
   * 
   * // compares two sequences boolean compareSequence(ISequence toComp);
   * 
   * // Returns the Color at given index Color grab(int index)
   * 
   * // Returns the index of the first occurrence of the given value in thie
   * ConsSequence int indexOf(Color toFind)
   * 
   * // Returns a new ConsSequence with Color at index removed ISequence
   * remove(int index)
   * 
   * //compare this ConsSequence to the given ConsSequence boolean
   * compareCons(ConsSequence toComp)
   * 
   * //compare this ConsSequence to the given MtSequence boolean
   * compareEmpty(MtSequence toComp)
   * 
   * // Check if this ConsSequence contains the given Color boolean contains(Color
   * toFind)
   * 
   * // Returns the number of exact matches to given sequence int
   * countExact(ISequence toComp)
   * 
   * // Calls countExact on the rest of this passing toComp int
   * countExactHelper(ISequence toComp)
   * 
   * // Returns the number of inexact matches to given sequence int
   * countInExact(ISequence toComp)
   * 
   * // Compares the first value in this ConsSequence to the given Color boolean
   * compFirst(Color toComp)
   * 
   * // Finds the total number of matches between this ConsSequence and the given
   * // sequence int countMatches(ISequence toComp)
   * 
   * // Returns a new ISequence with all the duplicates found in toComp removed
   * ISequence removeDupes(ISequence toComp)
   * 
   * // draw this ISequence WorldImage drawSequence()
   * 
   * //draw the score (Exact, inexact matches) comparing this ISequence to the
   * given pattern WorldImage drawScore(ISequence pattern)
   * 
   * //add the given Color into this ISequence just before the MtSequence at the
   * end //(helper for dealing with player input) ISequence addBeforeEnd(Color c)
   * 
   * Methods on Fields: See Isequence interface, I don't want this to be a 100
   * line comment
   */

  // compare this ConsSequence with the given ISequence
  public boolean compareSequence(ISequence toComp) {
    return toComp.compareCons(this);
  }

  // compare this ConsSequence with the given ConsSequence
  public boolean compareCons(ConsSequence toComp) {
    return this.first.equals(toComp.first) && this.rest.compareSequence(toComp.rest);
  }

  // compare this ConsSequence with the given MtSequence
  public boolean compareEmpty(MtSequence toComp) {
    return false;
  }

  // calculate the length of this ConsSequence
  public int length() {
    return 1 + this.rest.length();
  }

  // return the color at the given index in this ConsSequence
  public Color grab(int index) {
    if (index == 0) {
      return this.first;
    }
    else if (index > 0) {
      return this.rest.grab(index - 1);
    }
    throw new IllegalArgumentException("Grabbed with index < 0");
  }

  // return a new ConsSequence without the Color at the given index
  public ISequence remove(int index) {
    if (index == 0) {
      return this.rest;
    }
    else {
      return new ConsSequence(this.first, this.rest.remove(index - 1));
    }
  }

  // does this ConsSequence contain the given Color?
  public boolean contains(Color toFind) {
    return this.first.equals(toFind) || this.rest.contains(toFind);
  }

  // calculate the number of exact matches between this ConsSequence and given
  // ISequence
  public int countExact(ISequence toComp) {
    if (toComp.compFirst(this.first)) {
      return 1 + toComp.countExactHelper(this.rest);
    }
    return toComp.countExactHelper(this.rest);
  }

  // calculate the number of Inexact matches between this ConsSequence and given
  // ISequence
  public int countInExact(ISequence toComp) {
    return this.countMatches(toComp) - this.countExact(toComp);
  }

  // does the first Color in this ConsSequence equal the given Color?
  public boolean compFirst(Color toComp) {
    return this.first.equals(toComp);
  }

  // feeds toComp back into countExact removing the first in this ConsSequence
  public int countExactHelper(ISequence toComp) {
    return this.rest.countExact(toComp);
  }

  // calculate the total number of matches between this ConsSequence and the given
  // ISequence
  public int countMatches(ISequence toComp) {
    return (this.length() - this.removeDupes(toComp).length());
  }

  // find the first occurrence of the given Color in this ConsSequence
  public int indexOf(Color toFind) {
    if (this.first.equals(toFind)) {
      return 0;
    }
    return 1 + this.rest.indexOf(toFind);
  }

  // return a new ISequence without any duplicates between this ConsSequence and
  // the given ISequence
  public ISequence removeDupes(ISequence toComp) {
    if (toComp.contains(this.first)) {
      return this.rest.removeDupes(toComp.remove(toComp.indexOf(this.first)));
    }
    return new ConsSequence(this.first, this.rest.removeDupes(toComp));
  }

  // draw this ConsSequence
  public WorldImage drawSequence() {
    return new BesideAlignImage(AlignModeY.TOP, this.drawFirst(), this.rest.drawSequence());
  }

  // draw the first Color in this ConsSequence
  public WorldImage drawFirst() {
    return new CircleImage(30, OutlineMode.SOLID, this.first);
  }

  // draw the score as determined by comparing this ConsSequence and the given
  // ISequence
  public WorldImage drawScore(ISequence pattern) {
    if (this.length() == pattern.length()) {
      String exact = "E:" + this.countExact(pattern) + " ";
      String inExact = "I:" + this.countInExact(pattern);
      return new TextImage(exact + inExact, 40, Color.white);
    }
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

  // add the given Color just before the end of this ConsSequence
  public ISequence addBeforeEnd(Color c) {
    return new ConsSequence(this.first, this.rest.addBeforeEnd(c));
  }

}

class MtSequence implements ISequence {
  // represent an empty "Mt" sequence of Colors

  /*
   * MtSequence Template: Fields: N/A Methods: see ISequence interface, I don't
   * want another 50-line comment template Methods on Fields: N/A
   */

  // compare this MtSequence to the given ISequence
  public boolean compareSequence(ISequence toComp) {
    return toComp.compareEmpty(this);
  }

  // this MtSequence is not the same as a ConsSequence
  public boolean compareCons(ConsSequence toComp) {
    return false;
  }

  // this MtSequence is the same as the given MtSequence
  public boolean compareEmpty(MtSequence toComp) {
    return true;
  }

  // an MtSequence has no length
  public int length() {
    return 0;
  }

  // Can't grab a Color in an MtSequence
  public Color grab(int index) {
    throw new IllegalArgumentException("Tried to grab at index longe than ISequence");
  }

  // can't remove something in an MtSequence
  public ISequence remove(int index) {
    throw new IllegalArgumentException("Tried to remove at index longe than ISequence");
  }

  // this MtSequence doesn't have whatever you're looking for. I promise.
  public boolean contains(Color toFind) {
    return false;
  }

  // this MtSequence matches nothing from the given ISequence
  public int countExact(ISequence toComp) {
    return 0;
  }

  // this MtSequence matches nothing from the given ISequence
  public int countInExact(ISequence toComp) {
    return 0;
  }

  // this MtSequence has no associated color
  public boolean compFirst(Color toComp) {
    return false;
  }

  // this MtSequence doesn't contribute to counting matches
  public int countExactHelper(ISequence toComp) {
    return 0;
  }

  // this MtSequence doesn't contribute to counting matches
  public int countMatches(ISequence toComp) {
    return 0;
  }

  // this MtSequence has no duplicate Colors with the given ISequence
  public ISequence removeDupes(ISequence toComp) {
    return this;
  }

  // this MtSequence does not have the Color you're looking for. Move along.
  public int indexOf(Color toFind) {
    throw new IllegalArgumentException("Tried to find in empty list");
  }

  // this MtSequence is empty and should appear as such
  public WorldImage drawSequence() {
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

  // this MtSequence contributes nothing toward drawing the scores
  public WorldImage drawScore(ISequence pattern) {
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

  // add the given color onto this MtSequence
  public ISequence addBeforeEnd(Color c) {
    return new ConsSequence(c, this);
  }

}

interface ILoISequence {
  // represent Lists of ISequences

  // Gets the length of the ILoISequence
  int length();

  // Returns true if this ILoISequence contains the given ISequence
  boolean contains(ISequence toFind);

  // draw this ILoISequence
  WorldImage drawILoISequence();

  // draw the scores comparing this ILoISequence wih the given ISequence
  WorldImage drawScores(ISequence pattern);

}

class ConsLoISequence implements ILoISequence {
  // represent an ILoISequence with multiple sublists
  ISequence first; // the first sublist, an ISequence
  ILoISequence rest; // the rest of the sublists, an ILoISequence

  ConsLoISequence(ISequence first, ILoISequence rest) {
    this.first = first;
    this.rest = rest;
  }

  /*
   * ConsLoISequence Template: Fields: - ISequence first - ConsLoISequence rest
   *
   * Methods: // Gets the length of this ConsLoISequence int length()
   * 
   * // Returns true if this ConsLoISequence contains the given ISequence boolean
   * contains(ISequence toFind)
   * 
   * //draw this ConsLoISequence WorldImage drawILoISequence()
   * 
   * //draw the scores comparing this ConsLoISequence with the given ISequence
   * WorldImage drawScores(ISequence pattern)
   * 
   * Methods on Fields: see ISequence template
   */

  // compute the length of this ConsLoISequence
  public int length() {
    return 1 + this.rest.length();
  }

  // does this ConsLoISequence contain the given ISequence?
  public boolean contains(ISequence toFind) {
    return this.first.compareSequence(toFind) || this.rest.contains(toFind);
  }

  // draw this ConsLoISequence
  public WorldImage drawILoISequence() {
    return new AboveAlignImage(AlignModeX.LEFT, this.first.drawSequence(),
        this.rest.drawILoISequence());
  }

  // draw the scores resulting from compring this ConsLoISequence to the given
  // ISequence
  public WorldImage drawScores(ISequence pattern) {
    return new AboveAlignImage(AlignModeX.LEFT, this.first.drawScore(pattern),
        this.rest.drawScores(pattern));
  }

}

class MtLoISequence implements ILoISequence {
  // represent an empty ILoISequence

  /*
   * Fields: N/A
   * 
   * Methods: // Gets the length of this MtLoISequence int length()
   * 
   * // Returns true if this MtLoISequence contains the given ISequence (it
   * doesn't) boolean contains(ISequence toFind)
   * 
   * //draw this MtLoISequence WorldImage drawILoISequence()
   * 
   * //draw the scores comparing this MtLoISequence with the given ISequence
   * WorldImage drawScores(ISequence pattern)
   *
   * Methods on Fields: N/A
   */

  // this MtLoISequence has a length of 0
  public int length() {
    return 0;
  }

  // this MtLoISequence does not have the ISequence you're looking for. Move
  // along.
  public boolean contains(ISequence toFind) {
    return false;
  }

  // draw this MtLoISequence (it's a blank spot)
  public WorldImage drawILoISequence() {
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

  // draw the scores resulting from comparing this MtLoISequence with the given
  // pattern
  public WorldImage drawScores(ISequence pattern) {
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

}

class Mastermind extends World {
  // represent our World as a "Mastermind"

  static ISequence sequence = new MtSequence();
  // this is used for showing the player's inputs before entering them into the
  // world.
  // once you confirm entry, you see the scores from that guess, we show the
  // player
  // this sequence (their guesses) until they decide to finalize their guess.

  ISequence pattern; // the target pattern the player tries to guess
  ILoISequence attempts; // the list of guesses the player has made
  int numAttempts; // the maximum number of attempts allowed, must be > 0
  boolean allowDuplicate; // whether or not there can be duplicates in the pattern
  // (the player is still always allowed to guess duplicates if they want to; it's
  // not our job to
  // stop the player from shooting themselves in the foot)
  int patternLength; // how long the target pattern is (how many colors it has)
  ISequence colorstoUse; // the list of colors that could be in the pattern

  Mastermind(ISequence pattern, ILoISequence attempts, int numAttempts, boolean allowDuplicate,
      int patternLength, ISequence colorstoUse) {
    if (patternLength <= 0) {
      throw new IllegalArgumentException("Bad Pattern Length, " + patternLength);
    }

    if (colorstoUse.length() <= 0) {
      throw new IllegalArgumentException("Bad List of Colors, " + colorstoUse.length());
    }

    if (numAttempts <= 0) {
      throw new IllegalArgumentException("Bad Number of Attempts, " + numAttempts);
    }

    if (!allowDuplicate && patternLength > colorstoUse.length()) {
      throw new IllegalArgumentException("Cannot generate pattern without duplicates");
    }
    this.pattern = pattern;
    this.numAttempts = numAttempts;
    this.allowDuplicate = allowDuplicate;
    this.patternLength = patternLength;
    this.colorstoUse = colorstoUse;
    this.attempts = attempts;
  }

  /*
   * Mastermind Template: Fields: - ISequence pattern - ILoISequence attempts -
   * int numAttempts - boolean allowDuplicate - int patternLength - ISequence
   * colorstoUse
   *
   * Methods: ISequence generatePattern (called statically to allow it being
   * called in the constructor) boolean won String winConditionStr boolean
   * gameOver int shiftForSequence (called statically since sequence is a static
   * variable for player input) WorldImage drawWorld WorldImage drawBackground
   * WorldImage drawCircles WorldImage drawEmpty WorldImage showAnswer WorldImage
   * drawGuesses WorldImage drawScores WorldScene makeScene Mastermind onKeyEvent
   * Methods on Fields: see appropriate templates/interfaces. I'm not going to
   * make you read a 200+ line comment
   *
   */

  // Convenience constructor for creating a new Mastermind with a random start
  Mastermind(int numAttempts, boolean allowDuplicate, int patternLength, ISequence colorstoUse) {

    this(Mastermind.generatePattern(allowDuplicate, patternLength, colorstoUse),
        new MtLoISequence(), numAttempts, allowDuplicate, patternLength, colorstoUse);
  }

  // generate a pattern to be guessed using the necessary parameters
  static ISequence generatePattern(boolean allowDuplicate, int patternLength,
      ISequence colorstoUse) {
    int toGrab = (new Random()).nextInt(colorstoUse.length());

    if (patternLength > 0) {
      if (allowDuplicate) {

        return new ConsSequence(colorstoUse.grab(toGrab),
            generatePattern(allowDuplicate, patternLength - 1, colorstoUse));
      }
      else {
        if (colorstoUse.length() < patternLength) {
          throw new IllegalArgumentException("Cannot generate pattern without duplicates");
        }

        return new ConsSequence(colorstoUse.grab(toGrab),
            generatePattern(allowDuplicate, patternLength - 1, colorstoUse.remove(toGrab)));
      }
    }
    return new MtSequence();
  }

  // for testing generate pattern with a "random" (nonrandom) seed
  static ISequence generatePattern(boolean allowDuplicate, int patternLength, ISequence colorstoUse,
      int seed) {
    int toGrab = (new Random(seed)).nextInt(colorstoUse.length());

    if (patternLength > 0) {
      if (allowDuplicate) {

        return new ConsSequence(colorstoUse.grab(toGrab),
            generatePattern(allowDuplicate, patternLength - 1, colorstoUse, seed));
      }
      else {
        if (colorstoUse.length() < patternLength) {
          throw new IllegalArgumentException("Cannot generate pattern without duplicates");
        }

        return new ConsSequence(colorstoUse.grab(toGrab),
            generatePattern(allowDuplicate, patternLength - 1, colorstoUse.remove(toGrab), seed));
      }
    }
    return new MtSequence();
  }

  // did you win?
  boolean won() {
    return this.attempts.contains(this.pattern);
  }

  // return a string describing the Win/Lose situation
  String winConditionStr() {
    if (this.won()) {
      return "WINNER!";
    }
    return "LOSER!";
  }

  // is the game over?
  boolean gameOver() {
    return this.won() || this.attempts.length() >= this.numAttempts;
  }

  // adjust image alignments based on the number of guesses the player has made
  // and the
  // current state of their entry sequence (helper function for drawing the World)
  static int shiftForSequence() {
    int l = sequence.length();
    if (l > 0) {
      l = 1;
    }
    return l;
  }

  // draw this bad boy (return an image representation of the state of the world)
  WorldImage drawWorld() {
    WorldImage image = this.drawBackground();
    image = new VisiblePinholeImage(
        image.movePinhole(-30 * Math.max(this.colorstoUse.length(), 2 + this.patternLength),
            -30 * (this.numAttempts + 2)));
    // draw the background

    WorldImage palette = this.colorstoUse.drawSequence();
    palette = palette.movePinholeTo(image.pinhole);
    palette = new VisiblePinholeImage(palette.movePinhole(0, -(30 * (this.numAttempts + 2)) + 30));
    WorldImage withPalette = new OverlayImage(palette, image);
    // draw the palette on the bottom and place it on the background

    WorldImage circles = this.drawCircles(this.numAttempts + 1);
    circles = circles.movePinholeTo(withPalette.pinhole);
    circles = new VisiblePinholeImage(
        circles.movePinhole(((60 * Math.max(this.colorstoUse.length(), 2 + this.patternLength))
            - (60 * this.patternLength)) / 2, +30));
    WorldImage withCircles = new OverlayImage(circles, withPalette);
    // draw the "holes" for the "pegs" and place that over the background

    WorldImage answer = this.showAnswer(this.gameOver());
    answer = answer.movePinholeTo(withCircles.pinhole);
    answer = new VisiblePinholeImage(
        answer.movePinhole(((60 * Math.max(this.colorstoUse.length(), 2 + this.patternLength))
            - (60 * this.patternLength)) / 2, 30 * (this.numAttempts + 2) - 30));
    WorldImage withAnswer = new OverlayImage(answer, withCircles);
    // draw the pattern to be guessed and hide it if needed, place that on the
    // background too

    WorldImage guesses = this.drawGuesses();
    guesses = guesses.movePinholeTo(withAnswer.pinhole);
    guesses = new VisiblePinholeImage(guesses.movePinhole(
        (60 * Math.max(this.colorstoUse.length(), 2 + this.patternLength) - 60 * this.patternLength)
            / 2,
        -30 * (this.numAttempts + 2 - Mastermind.shiftForSequence()) + 30 * this.attempts.length()
            + 60));
    WorldImage withGuesses = new OverlayImage(guesses, withAnswer);
    // draw the guesses and put them over the background

    WorldImage scores = this.drawScores();
    scores = scores.movePinholeTo(withGuesses.pinhole);
    scores = new VisiblePinholeImage(scores.movePinhole(
        (60 * Math.max(this.colorstoUse.length(), 2 + this.patternLength) - 60 * this.patternLength)
            / -2,
        -30 * (this.numAttempts + 2) + 30 * this.attempts.length() + 60));
    WorldImage withScores = new OverlayImage(scores, withGuesses);
    // draw the scores and put them on the background

    WorldImage note = this.showNote(this.gameOver());
    note = note.movePinholeTo(withScores.pinhole);
    note = new VisiblePinholeImage(note.movePinhole(
        (60 * Math.max(this.colorstoUse.length(), 2 + this.patternLength) - 60 * this.patternLength)
            / -2,
        30 * (this.numAttempts + 2) - 15));
    WorldImage withNote = new OverlayImage(note, withScores);
    // add the victory/defeat message and show it if necessary

    return withNote;
  }

  // draw the background
  WorldImage drawBackground() {
    return new RectangleImage(60 * Math.max(this.colorstoUse.length(), 2 + this.patternLength),
        60 * (this.numAttempts + 2), OutlineMode.SOLID, Color.GRAY);
  }

  // draw the "holes" for the "pegs" that the guesses will go in
  WorldImage drawCircles(int num) {
    if (num > 0) {
      return new AboveImage(drawEmpty(this.patternLength), this.drawCircles(num - 1));
    }
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

  // draw the holes, helper that draws one row
  WorldImage drawEmpty(int num) {
    if (num > 0) {
      return new BesideImage(new CircleImage(30, OutlineMode.OUTLINE, Color.black),
          this.drawEmpty(num - 1));
    }
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

  // draw and show the pattern to be guessed if necessary
  WorldImage showAnswer(boolean show) {
    if (show) {
      // System.out.println(this.patternLength);
      // System.out.println(this.pattern.length());
      // System.out.println(this.pattern);
      return this.pattern.drawSequence();
    }
    return new RectangleImage(60 * this.patternLength, 60, OutlineMode.SOLID, Color.black);
  }

  // draw the guess patterns
  WorldImage drawGuesses() {
    return new AboveAlignImage(AlignModeX.LEFT, sequence.drawSequence(),
        this.attempts.drawILoISequence());
  }

  // draw the scores from the patterns that have been guessed
  WorldImage drawScores() {
    return this.attempts.drawScores(this.pattern);
  }

  // draw the win condition message and show it if needed
  WorldImage showNote(boolean show) {
    if (show) {
      return new TextImage(this.winConditionStr(), 20, Color.red);
    }
    return new RectangleImage(0, 0, OutlineMode.SOLID, Color.white);
  }

  // call drawWorld in the bigBang loop
  public WorldScene makeScene() {
    return new WorldScene(1000, 1000).placeImageXY(this.drawWorld(), 0, 0);
  }

  // handle key inputs to play the game and alter the Mastermind accordingly
  public Mastermind onKeyEvent(String key) {

    if (!this.gameOver()) {
      if (("123456789").contains(key)) {
        int k = Integer.valueOf(key);
        if (k <= this.colorstoUse.length() && sequence.length() < this.patternLength) {
          Color toAdd = this.colorstoUse.grab(k - 1);
          sequence = sequence.addBeforeEnd(toAdd);
        }
      }

      else if (key.equals("enter")) {
        if (sequence.length() < this.patternLength) {
          return this;
        }
        else {

          Mastermind temp = new Mastermind(this.pattern,
              new ConsLoISequence(sequence, this.attempts), this.numAttempts, this.allowDuplicate,
              this.patternLength, this.colorstoUse);
          sequence = new MtSequence();
          return temp;
        }
      }

      else if (key.equals("backspace")) {
        if (sequence.length() > 0) {
          sequence = sequence.remove(0);
        }
      }
      return this;
    }
    return this;
  }
}

class MastermindTester {
  Color purple = new Color(140, 90, 140);
  ISequence colors3 = new ConsSequence(Color.red,
      new ConsSequence(Color.green, new ConsSequence(Color.blue, new MtSequence())));

  ISequence colors1 = new ConsSequence(Color.red,
      new ConsSequence(Color.green, new ConsSequence(Color.pink, new MtSequence())));
  ISequence colors2 = new ConsSequence(Color.yellow,
      new ConsSequence(Color.orange, new ConsSequence(Color.blue, colors1)));
  ISequence guess1 = new ConsSequence(Color.red,
      new ConsSequence(Color.orange, new ConsSequence(Color.yellow, new MtSequence())));
  ISequence guess2 = new ConsSequence(Color.yellow,
      new ConsSequence(Color.green, new ConsSequence(Color.blue, new MtSequence())));
  ISequence guess3 = new ConsSequence(Color.blue,
      new ConsSequence(purple, new ConsSequence(Color.yellow, new MtSequence())));
  ISequence testPattern3Long = new ConsSequence(Color.green,
      new ConsSequence(Color.red, new ConsSequence(Color.blue, new MtSequence())));
  ILoISequence guesses3 = new ConsLoISequence(guess3,
      new ConsLoISequence(guess2, new ConsLoISequence(guess1, new MtLoISequence())));
  ILoISequence guesses3v2 = new ConsLoISequence(testPattern3Long,
      new ConsLoISequence(guess1, new ConsLoISequence(guess2,
          new ConsLoISequence(new ConsSequence(Color.blue, guess3), new MtLoISequence()))));
  ISequence testPattern = new ConsSequence(Color.green,
      new ConsSequence(Color.red, new ConsSequence(Color.blue, colors1)));

  Mastermind game1 = new Mastermind(5, true, 2, colors1);
  Mastermind game2 = new Mastermind(8, false, 3, colors2);
  Mastermind game3 = new Mastermind(testPattern3Long, guesses3, 8, true, 3, colors2);
  Mastermind win = new Mastermind(new ConsSequence(Color.red, new MtSequence()),
      new ConsLoISequence(new ConsSequence(Color.red, new MtSequence()), new MtLoISequence()), 2,
      false, 1, colors1);
  Mastermind loss = new Mastermind(new ConsSequence(Color.blue, new MtSequence()),
      new ConsLoISequence(new ConsSequence(Color.red, new MtSequence()), new MtLoISequence()), 1,
      false, 1, colors1);

  // test onKeyEvent
  boolean testonKeyEvent(Tester t) {
    return t.checkExpect(game1.onKeyEvent("h"), game1)
        && t.checkExpect(game1.onKeyEvent("1"), game1)
        // game doesn't change until you hit enter
        && t.checkExpect(game1.onKeyEvent("enter"), game1)
        // game doesn't change unless sequence is full
        && t.checkExpect(game2.onKeyEvent("enter"), game2)
        && t.checkExpect(game1.onKeyEvent("h"), game1)
        // test adding to sequence and backspace
        && t.checkExpect(game2.onKeyEvent("enter").sequence,
            new ConsSequence(Color.red, new MtSequence()))
        && t.checkExpect(game2.onKeyEvent("enter").onKeyEvent("backspace").sequence,
            new MtSequence());
  }

  // test that makeScene passes to drawWorld correctly
  boolean testMakeScene(Tester t) {
    return t.checkExpect(game1.makeScene(),
        new WorldScene(1000, 1000).placeImageXY(game1.drawWorld(), 0, 0));
  }

  // testShowNote
  boolean testShowNote(Tester t) {
    return t.checkExpect(game1.showNote(false),
        new RectangleImage(0, 0, OutlineMode.SOLID, Color.white))
        && t.checkExpect(win.showNote(true), new TextImage(win.winConditionStr(), 20, Color.red))
        && t.checkExpect(loss.showNote(true), new TextImage(loss.winConditionStr(), 20, Color.red));
  }

  // testDrawScores passes information correctly
  boolean testDrawScores(Tester t) {
    return t.checkExpect(win.drawScores(),
        new ConsLoISequence(new ConsSequence(Color.red, new MtSequence()), new MtLoISequence())
            .drawScores(new ConsSequence(Color.red, new MtSequence())));
  }

  // test drawGuesses passes info correctly
  boolean testDrawGuesses(Tester t) {
    return t.checkExpect(loss.drawGuesses(),
        new AboveAlignImage(AlignModeX.LEFT, new MtSequence().drawSequence(),
            new ConsLoISequence(new ConsSequence(Color.red, new MtSequence()), new MtLoISequence())
                .drawILoISequence()));
  }

  // test showAnswer
  boolean testShowAnswer(Tester t) {
    return t.checkExpect(win.showAnswer(true),
        new ConsSequence(Color.red, new MtSequence()).drawSequence())
        && t.checkExpect(win.showAnswer(false),
            new RectangleImage(60, 60, OutlineMode.SOLID, Color.black))
        && t.checkExpect(game1.showAnswer(true), game1.pattern.drawSequence()) && t.checkExpect(
            game1.showAnswer(false), new RectangleImage(120, 60, OutlineMode.SOLID, Color.black));
  }

  // test drawEmpty
  boolean testDrawEmpty(Tester t) {
    return t.checkExpect(game2.drawEmpty(1),
        new BesideImage(new CircleImage(30, OutlineMode.OUTLINE, Color.black),
            new RectangleImage(0, 0, OutlineMode.SOLID, Color.white)));
    // note this one test confirms both the zero and nonzero cases work because
    // drawEmpty is
    // a recursive method and both outputs must match in order to pass the test
  }

  // test drawCircle
  boolean testDrawCircles(Tester t) {
    return t.checkExpect(game2.drawCircles(1), new AboveImage(game2.drawEmpty(game2.patternLength),
        new RectangleImage(0, 0, OutlineMode.SOLID, Color.white)));
    // note this one test confirms both the zero and nonzero cases work because
    // drawCircles is
    // a recursive method and both outputs must match in order to pass the test
  }

  // test drawBackground
  boolean testDrawBackground(Tester t) {
    return t.checkExpect(game2.drawBackground(),
        new RectangleImage(360, 600, OutlineMode.SOLID, Color.gray))
        && t.checkExpect(win.drawBackground(),
            new RectangleImage(180, 240, OutlineMode.SOLID, Color.gray));
  }

  // test drawWorld
  boolean testDrawWorld(Tester t) {

    // pre-computation
    WorldImage image = game3.drawBackground();
    image = new VisiblePinholeImage(
        image.movePinhole(-30 * Math.max(colors2.length(), 2 + 3), -30 * (8 + 2)));
    // draw the background

    WorldImage palette = colors2.drawSequence();
    palette = palette.movePinholeTo(image.pinhole);
    palette = new VisiblePinholeImage(palette.movePinhole(0, -(30 * (8 + 2)) + 30));
    WorldImage withPalette = new OverlayImage(palette, image);
    // draw the palette on the bottom and place it on the background

    WorldImage circles = game3.drawCircles(8 + 1);
    circles = circles.movePinholeTo(withPalette.pinhole);
    circles = new VisiblePinholeImage(
        circles.movePinhole(((60 * Math.max(colors2.length(), 2 + 3)) - (60 * 3)) / 2, +30));
    WorldImage withCircles = new OverlayImage(circles, withPalette);
    // draw the "holes" for the "pegs" and place that over the background

    WorldImage answer = game3.showAnswer(false);
    answer = answer.movePinholeTo(withCircles.pinhole);
    answer = new VisiblePinholeImage(answer
        .movePinhole(((60 * Math.max(colors2.length(), 2 + 3)) - (60 * 3)) / 2, 30 * (8 + 2) - 30));
    WorldImage withAnswer = new OverlayImage(answer, withCircles);
    // draw the pattern to be guessed and hide it if needed, place that on the
    // background too

    WorldImage guesses = game3.drawGuesses();
    guesses = guesses.movePinholeTo(withAnswer.pinhole);
    guesses = new VisiblePinholeImage(
        guesses.movePinhole((60 * Math.max(colors2.length(), 2 + 3) - 60 * 3) / 2,
            -30 * (8 + 2 - Mastermind.shiftForSequence()) + 30 * game3.attempts.length() + 60));
    WorldImage withGuesses = new OverlayImage(guesses, withAnswer);
    // draw the guesses and put them over the background

    WorldImage scores = game3.drawScores();
    scores = scores.movePinholeTo(withGuesses.pinhole);
    scores = new VisiblePinholeImage(
        scores.movePinhole((60 * Math.max(colors2.length(), 2 + 3) - 60 * 3) / -2,
            -30 * (8 + 2) + 30 * game3.attempts.length() + 60));
    WorldImage withScores = new OverlayImage(scores, withGuesses);
    // draw the scores and put them on the background

    WorldImage note = game3.showNote(game3.gameOver());
    note = note.movePinholeTo(withScores.pinhole);
    note = new VisiblePinholeImage(note
        .movePinhole((60 * Math.max(colors2.length(), 2 + 3) - 60 * 3) / -2, 30 * (8 + 2) - 15));
    WorldImage theWorld = new OverlayImage(note, withScores);
    // add the victory/defeat message and show it if necessary

    return t.checkExpect(game3.drawWorld(), theWorld);
  }
  // since we know all of the subcomponents work properly, it's okay to test
  // drawWorld only once
  // to make sure the data passes though correctly

  boolean testDrawMastermind(Tester t) {
    WorldCanvas c = new WorldCanvas(1000, 1000);
    WorldScene s = new WorldScene(1000, 1000);
    /*
     * return c.drawScene(s.placeImageXY(game4.drawWorld(), 0,
     * 0).placeImageXY(game3.drawWorld(), 500, 0)) && c.show();
     */
    // return game3.bigBang(1000, 1000, 24); //uncomment this line to play some
    // Mastermind
    return true;
  }

  // Testing length()
  boolean testlength(Tester t) {
    return t.checkExpect(this.colors1.length(), 3) && t.checkExpect((new MtSequence()).length(), 0)
        && t.checkExpect(this.guesses3.length(), 3);
  }

  // Testing compareSequence()
  boolean testcompareSequence(Tester t) {
    return t.checkExpect(this.colors1.compareSequence(this.colors1), true)
        && t.checkExpect(this.colors2.compareSequence(this.colors1), false)
        && t.checkExpect(this.colors1.compareSequence(new MtSequence()), false)
        && t.checkExpect((new MtSequence()).compareSequence(new MtSequence()), true);
  }

  // Testing grab()
  boolean testgrab(Tester t) {
    return t.checkExpect(this.colors1.grab(0), Color.red)
        && t.checkExpect(this.colors2.grab(2), Color.blue)
        && t.checkExpect(this.guess3.grab(this.guess3.length() - 1), Color.yellow);
  }

  // Testing indexOf()
  boolean testindexOf(Tester t) {
    return t.checkExpect(this.colors1.indexOf(Color.green), 1)
        && t.checkExpect(this.colors2.indexOf(Color.yellow), 0)
        && t.checkExpect(this.guess1.indexOf(Color.yellow), 2);
  }

  // Testing remove()
  boolean testremove(Tester t) {
    return t.checkExpect(this.colors1.remove(0),
        new ConsSequence(Color.green, new ConsSequence(Color.pink, new MtSequence())))
        && t.checkExpect(this.guess1.remove(this.guess1.length() - 1),
            new ConsSequence(Color.red, new ConsSequence(Color.orange, new MtSequence())))
        && t.checkExpect(this.colors1.remove(1),
            new ConsSequence(Color.red, new ConsSequence(Color.pink, new MtSequence())));
  }

  // Testing compareCons()
  boolean testcompareCons(Tester t) {
    return t.checkExpect(this.colors1.compareCons((ConsSequence) this.colors1), true)
        && t.checkExpect(this.colors1.compareCons((ConsSequence) this.colors2), false)
        && t.checkExpect((new MtSequence()).compareCons((ConsSequence) this.colors1), false);
  }

  // Testing compareEmpty()
  boolean testcompareEmpty(Tester t) {
    return t.checkExpect(this.colors1.compareEmpty(new MtSequence()), false)
        && t.checkExpect((new MtSequence()).compareEmpty(new MtSequence()), true);
  }

  // Testing contains()
  boolean testcontains(Tester t) {
    return t.checkExpect(this.colors1.contains(Color.green), true)
        && t.checkExpect(this.colors1.contains(Color.black), false)
        && t.checkExpect((new MtSequence()).contains(Color.blue), false);
  }

  // Testing countExact()
  boolean testcountExact(Tester t) {
    return t.checkExpect(this.colors3.countExact(colors3), 3)
        && t.checkExpect(this.colors3.countExact(
            new ConsSequence(Color.blue, new ConsSequence(Color.green, new MtSequence()))), 1)
        && t.checkExpect(this.colors3.countExact(new MtSequence()), 0);
  }

  // Testing countExactHelper()
  boolean testcountExactHelper(Tester t) {
    return t
        .checkExpect(this.colors3.countExactHelper(
            new ConsSequence(Color.GREEN, new ConsSequence(Color.BLUE, new MtSequence()))), 2)
        && t.checkExpect(this.colors3.countExactHelper(colors3), 0);
  }

  // Testing countInExact()
  boolean testcountInExact(Tester t) {
    return t
        .checkExpect(colors3.countInExact(new ConsSequence(Color.blue,
            new ConsSequence(Color.red, new ConsSequence(Color.green, new MtSequence())))), 3)
        && t.checkExpect(this.colors3.countInExact(new MtSequence()), 0)
        && t.checkExpect(this.colors3.countInExact(this.colors3), 0);
  }

  // Testing countMatches()
  boolean testcountMatches(Tester t) {
    return t.checkExpect(this.colors3
        .countMatches(colors3), 3) && t
            .checkExpect(
                this.colors3.countMatches(new ConsSequence(Color.blue,
                    new ConsSequence(Color.red, new ConsSequence(Color.green, new MtSequence())))),
                3);
  }

  // Testing compFirst()
  boolean testcompFirst(Tester t) {
    return t.checkExpect(this.colors3.compFirst(Color.red), true)
        && t.checkExpect(this.colors3.compFirst(Color.blue), false)
        && t.checkExpect((new MtSequence()).compFirst(Color.red), false);
  }

  // Testing removeDupes()
  boolean testremoveDupes(Tester t) {
    return t.checkExpect(this.colors3.removeDupes(this.colors3), new MtSequence()) && t.checkExpect(
        this.colors3.removeDupes(new ConsSequence(Color.pink,
            new ConsSequence(Color.black, new ConsSequence(Color.CYAN, new MtSequence())))),
        this.colors3);
  }

  // Testing drawFirst()
  boolean testdrawFirst(Tester t) {
    return t.checkExpect(((ConsSequence) this.colors3).drawFirst(),
        new CircleImage(30, OutlineMode.SOLID, Color.red))
        && t.checkExpect(((ConsSequence) this.colors2).drawFirst(),
            new CircleImage(30, OutlineMode.SOLID, Color.yellow));
  }

  // Testing drawSequence()
  boolean testdrawSequence(Tester t) {
    return t.checkExpect(colors3.drawSequence(), new BesideAlignImage(AlignModeY.TOP,
        ((ConsSequence) this.colors3).drawFirst(),
        new BesideAlignImage(AlignModeY.TOP, new CircleImage(30, OutlineMode.SOLID, Color.green),
            new BesideAlignImage(AlignModeY.TOP, new CircleImage(30, OutlineMode.SOLID, Color.blue),
                new RectangleImage(0, 0, OutlineMode.SOLID, Color.white)))))
        && t.checkExpect((new MtSequence().drawSequence()),
            new RectangleImage(0, 0, OutlineMode.SOLID, Color.white));
  }

  // Testing generatePattern()
  boolean testinggeneratePattern(Tester t) {
    return t.checkExpect(this.game1.generatePattern(true, 3, this.colors3, 6),
        new ConsSequence(Color.green,
            (new ConsSequence(Color.green, (new ConsSequence(Color.green, new MtSequence()))))))
        && t.checkExpect(this.game1.generatePattern(false, 2, this.colors3, 8),
            new ConsSequence(Color.green, new ConsSequence(Color.blue, new MtSequence())));
  }

  // Testing winConditionStr()
  boolean testingwinConditionStr(Tester t) {
    return t.checkExpect(this.loss.winConditionStr(), "LOSER!")
        && t.checkExpect(this.win.winConditionStr(), "WINNER!");
  }

  // Testing gameOver()
  boolean testinggameOver(Tester t) {
    return t.checkExpect(this.win.gameOver(), true) && t.checkExpect(this.loss.gameOver(), true)
        && t.checkExpect(this.game1.gameOver(), false);
  }

  boolean testShiftForSequence(Tester t) {
    return t.checkExpect(Mastermind.shiftForSequence(), 0);
    // sequence is currently 0, if you change the sequence, the output will change
    // since we can't change it here, we only have this case.
    // we know the other case works because the images align properly in the other
    // test
    // drawing methods
  }

}
