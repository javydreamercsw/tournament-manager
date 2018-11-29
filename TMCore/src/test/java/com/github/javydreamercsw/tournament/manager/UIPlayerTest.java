package com.github.javydreamercsw.tournament.manager;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;


/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class UIPlayerTest
{

  /**
   * Test of win method, of class UIPlayer.
   */
  @Test
  public void testWin()
  {
    System.out.println("win");
    UIPlayer instance = new UIPlayer("Test", 0);
    assertEquals(0, instance.getRecord().getWins());
    instance.getRecord().win();
    assertEquals(1, instance.getRecord().getWins());
  }

  /**
   * Test of loss method, of class UIPlayer.
   */
  @Test
  public void testLoss()
  {
    System.out.println("loss");
    UIPlayer instance = new UIPlayer("Test", 0);
    assertEquals(0, instance.getRecord().getLosses());
    instance.getRecord().loss();
    assertEquals(1, instance.getRecord().getLosses());
  }

  /**
   * Test of draw method, of class UIPlayer.
   */
  @Test
  public void testDraw()
  {
    System.out.println("draw");
    UIPlayer instance = new UIPlayer("Test", 0);
    assertEquals(0, instance.getRecord().getDraws());
    instance.getRecord().draw();
    assertEquals(1, instance.getRecord().getDraws());
  }
}
