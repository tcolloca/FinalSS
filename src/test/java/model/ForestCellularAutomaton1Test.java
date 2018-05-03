package model;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import model.ForestCellularAutomaton1.VoidCell;

@RunWith(JUnit4.class)
public class ForestCellularAutomaton1Test {

  private static final double SQRT_3 = Math.sqrt(3);
  private static final double[] wind = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
  private static final double[][] fireSpreads = {{2, 1}, {1.5, 1}};
  private static final double[][][] winds = {{wind, wind}, {wind, wind}};
  private static final double[][] heights = {{2, 3}, {1, 1}};
  private static final double[][] ignitions = {{1, 0}, {0.3, 0}};

  private ForestCellularAutomaton1 defForest;

  @Before
  public void setUp() {
    defForest = new ForestCellularAutomaton1(fireSpreads, winds, heights, ignitions,
        x -> x > 0 ? 1.5 : (x == 0 ? 1 : 0.5), 1);
  }

  @Test
  public void test_neighDeltas() {
    assertThat(ForestCellularAutomaton1.ODD_NEAR_NEIGHS[0], equalTo(new int[] {-1, 0}));
    assertThat(ForestCellularAutomaton1.ODD_NEAR_NEIGHS[1], equalTo(new int[] {0, 1}));
    assertThat(ForestCellularAutomaton1.ODD_NEAR_NEIGHS[2], equalTo(new int[] {1, 1}));
    assertThat(ForestCellularAutomaton1.ODD_NEAR_NEIGHS[3], equalTo(new int[] {1, 0}));
    assertThat(ForestCellularAutomaton1.ODD_NEAR_NEIGHS[4], equalTo(new int[] {1, -1}));
    assertThat(ForestCellularAutomaton1.ODD_NEAR_NEIGHS[5], equalTo(new int[] {0, -1}));

    assertThat(ForestCellularAutomaton1.ODD_DISTANT_NEIGHS[0], equalTo(new int[] {-1, 1}));
    assertThat(ForestCellularAutomaton1.ODD_DISTANT_NEIGHS[1], equalTo(new int[] {0, 2}));
    assertThat(ForestCellularAutomaton1.ODD_DISTANT_NEIGHS[2], equalTo(new int[] {2, 1}));
    assertThat(ForestCellularAutomaton1.ODD_DISTANT_NEIGHS[3], equalTo(new int[] {2, -1}));
    assertThat(ForestCellularAutomaton1.ODD_DISTANT_NEIGHS[4], equalTo(new int[] {0, -2}));
    assertThat(ForestCellularAutomaton1.ODD_DISTANT_NEIGHS[5], equalTo(new int[] {-1, -1}));

    assertThat(ForestCellularAutomaton1.EVEN_NEAR_NEIGHS[0], equalTo(new int[] {-1, 0}));
    assertThat(ForestCellularAutomaton1.EVEN_NEAR_NEIGHS[1], equalTo(new int[] {-1, 1}));
    assertThat(ForestCellularAutomaton1.EVEN_NEAR_NEIGHS[2], equalTo(new int[] {0, 1}));
    assertThat(ForestCellularAutomaton1.EVEN_NEAR_NEIGHS[3], equalTo(new int[] {1, 0}));
    assertThat(ForestCellularAutomaton1.EVEN_NEAR_NEIGHS[4], equalTo(new int[] {0, -1}));
    assertThat(ForestCellularAutomaton1.EVEN_NEAR_NEIGHS[5], equalTo(new int[] {-1, -1}));

    assertThat(ForestCellularAutomaton1.EVEN_DISTANT_NEIGHS[0], equalTo(new int[] {-2, 1}));
    assertThat(ForestCellularAutomaton1.EVEN_DISTANT_NEIGHS[1], equalTo(new int[] {0, 2}));
    assertThat(ForestCellularAutomaton1.EVEN_DISTANT_NEIGHS[2], equalTo(new int[] {1, 1}));
    assertThat(ForestCellularAutomaton1.EVEN_DISTANT_NEIGHS[3], equalTo(new int[] {1, -1}));
    assertThat(ForestCellularAutomaton1.EVEN_DISTANT_NEIGHS[4], equalTo(new int[] {0, -2}));
    assertThat(ForestCellularAutomaton1.EVEN_DISTANT_NEIGHS[5], equalTo(new int[] {-2, -1}));
  }

  @Test
  public void test_discretize() {
    assertThat(ForestCellularAutomaton1.discretize(-1.2), equalTo(0.0));
    assertThat(ForestCellularAutomaton1.discretize(-0.2), equalTo(0.0));
    assertThat(ForestCellularAutomaton1.discretize(0), equalTo(0.0));
    assertThat(ForestCellularAutomaton1.discretize(0.02), equalTo(0.0));
    assertThat(ForestCellularAutomaton1.discretize(0.18), equalTo(0.2));
    assertThat(ForestCellularAutomaton1.discretize(0.99), equalTo(1.0));
    assertThat(ForestCellularAutomaton1.discretize(1.2), equalTo(1.0));
  }

  @Test
  public void test_getAutomatonCell() {
    ForestCellularAutomaton1 forest =
        new ForestCellularAutomaton1(new double[][] {{1}, {2}}, new double[][][] {{wind}, {wind}},
            new double[][] {{1}, {2}}, new double[][] {{1}, {2}}, null, 0.0);
    assertThat(forest.getAutomatonCell(-1, 0), instanceOf(VoidCell.class));
    assertThat(forest.getAutomatonCell(0, -1), instanceOf(VoidCell.class));
    assertThat(forest.getAutomatonCell(0, 1), instanceOf(VoidCell.class));
    assertThat(forest.getAutomatonCell(2, 0), instanceOf(VoidCell.class));
    assertThat(forest.getAutomatonCell(1, 0).getBurnt(), equalTo(2.0));
  }

  @Test
  public void test_getAssociativeNeighs() {
    assertThat(ForestCellularAutomaton1.getAssociateNeighs(0), equalTo(new int[] {0, 1}));
    assertThat(ForestCellularAutomaton1.getAssociateNeighs(3), equalTo(new int[] {3, 4}));
    assertThat(ForestCellularAutomaton1.getAssociateNeighs(5), equalTo(new int[] {5, 0}));
  }

  @Test
  public void test_getAssociativeDeltas() {
    assertThat(ForestCellularAutomaton1.getAssociateNeighsDeltas(2, 0),
        equalTo(new int[][] {{-1, 0}, {-1, 1}}));
    assertThat(ForestCellularAutomaton1.getAssociateNeighsDeltas(4, 5),
        equalTo(new int[][] {{-1, -1}, {-1, 0}}));
    assertThat(ForestCellularAutomaton1.getAssociateNeighsDeltas(3, 5),
        equalTo(new int[][] {{0, -1}, {-1, 0}}));
  }

  @Test
  public void test_getWindCoef() {
    assertThat(defForest.getWindCoef(1, 1, 5, true), equalTo(1.0));
  }

  @Test
  public void test_getHeightCoef() {
    assertThat(defForest.getHeightCoef(1, 1, 5, false), equalTo((0.5 + 1 + 1.5 + 0.5) / 4));
    assertThat(defForest.getHeightCoef(0, 0, 2, false), equalTo((1.5 + 1 + 0.5 + 1.5) / 4));
    assertThat(defForest.getHeightCoef(0, 0, 1, false), equalTo(0.0));
    assertThat(defForest.getHeightCoef(1, 1, 1, true), equalTo(0.0));
    assertThat(defForest.getHeightCoef(1, 1, 0, true), equalTo(0.5));
    assertThat(defForest.getHeightCoef(0, 1, 3, true), equalTo(1.5));
    assertThat(defForest.getHeightCoef(0, 0, 2, true), equalTo(0.5));
  }

  @Test
  public void test_getFireSpreadCoef() {
    assertThat(defForest.getFireSpreadCoef(1, 1, 5, false),
        equalTo(2.0 * Math.PI * SQRT_3 / 27.0 * Math.pow(SQRT_3 * 1.0 / 2.0 - 1.0 / 1.5, 2.0)));
    assertThat(defForest.getFireSpreadCoef(0, 0, 2, false),
        equalTo(2.0 * Math.PI * SQRT_3 / 27.0 * Math.pow(SQRT_3 * 2.0 / 2.0 - 2.0 / 1.5, 2.0)));
    assertThat(defForest.getFireSpreadCoef(1, 1, 0, true),
        equalTo(2.0 * SQRT_3 / 9.0 * 1.0 / 2.0 * (SQRT_3 + Math.PI / 2.0 * 1.0 / 2.0)));
    assertThat(defForest.getFireSpreadCoef(0, 1, 4, true),
        equalTo(2.0 * SQRT_3 / 9.0 * 1.0 / 2.0 * (SQRT_3 + Math.PI / 2.0 * 1.0 / 2.0)));

    double alpha = Math.PI / 6.0 - Math.acos(SQRT_3 / 4.0 + SQRT_3 / 12.0 * Math.sqrt(12.0 - 3.0));
    assertThat(defForest.getFireSpreadCoef(0, 0, 2, true),
        closeTo(2.0 / 3.0 * (1.0 + Math.sin(Math.PI / 6.0 - alpha) + SQRT_3 * alpha), 1e-15));
  }

  @Test
  public void test_getNext() {
    double w01_00 = defForest.getWindCoef(0, 1, 4, true);
    double w01_10 = defForest.getWindCoef(0, 1, 3, true);
    double h01_00 = defForest.getHeightCoef(0, 1, 4, true);
    double h01_10 = defForest.getHeightCoef(0, 1, 3, true);
    double r01_00 = defForest.getFireSpreadCoef(0, 1, 4, true);
    double r01_10 = defForest.getFireSpreadCoef(0, 1, 3, true);

    double w10_00 = defForest.getWindCoef(1, 0, 0, true);
    double h10_00 = defForest.getHeightCoef(1, 0, 0, true);
    double r10_00 = defForest.getFireSpreadCoef(1, 0, 0, true);

    double w11_00 = defForest.getWindCoef(1, 1, 5, false);
    double w11_10 = defForest.getWindCoef(1, 1, 5, true);
    double h11_00 = defForest.getHeightCoef(1, 1, 5, false);
    double h11_10 = defForest.getHeightCoef(1, 1, 5, true);
    double r11_00 = defForest.getFireSpreadCoef(1, 1, 5, false);
    double r11_10 = defForest.getFireSpreadCoef(1, 1, 5, true);

    double s00 = defForest.getAutomatonCell(0, 0).getBurnt();
    double s10 = defForest.getAutomatonCell(1, 0).getBurnt();
    double s01 = defForest.getAutomatonCell(0, 1).getBurnt();
    double s11 = defForest.getAutomatonCell(1, 1).getBurnt();

    defForest.next();

    assertThat(defForest.getCell(0, 0).getValue(), equalTo(1.0));

    assertThat(defForest.getCell(0, 1).getValue(), equalTo(ForestCellularAutomaton1
        .discretize(s01 + w01_00 * h01_00 * r01_00 * s00 + w01_10 * h01_10 * r01_10 * s10)));

    assertThat(defForest.getCell(1, 0).getValue(),
        equalTo(ForestCellularAutomaton1.discretize(s10 + w10_00 * h10_00 * r10_00 * s00)));

    assertThat(defForest.getCell(1, 1).getValue(), equalTo(ForestCellularAutomaton1
        .discretize(s11 + w11_00 * h11_00 * r11_00 * s00 + w11_10 * h11_10 * r11_10 * s10)));
  }
}
