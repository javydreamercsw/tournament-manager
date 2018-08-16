package net.sourceforge.javydreamercsw.tournament.manager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerTest
{

  /**
   * Test of win method, of class Player.
   */
  @Test
  public void testWin()
  {
    System.out.println("win");
    Player instance = new Player("Test", 0);
    assertEquals(0, instance.getRecord().getWins());
    instance.getRecord().win();
    assertEquals(1, instance.getRecord().getWins());
  }

  /**
   * Test of loss method, of class Player.
   */
  @Test
  public void testLoss()
  {
    System.out.println("loss");
    Player instance = new Player("Test", 0);
    assertEquals(0, instance.getRecord().getLosses());
    instance.getRecord().loss();
    assertEquals(1, instance.getRecord().getLosses());
  }

  /**
   * Test of draw method, of class Player.
   */
  @Test
  public void testDraw()
  {
    System.out.println("draw");
    Player instance = new Player("Test", 0);
    assertEquals(0, instance.getRecord().getDraws());
    instance.getRecord().draw();
    assertEquals(1, instance.getRecord().getDraws());
  }
}
