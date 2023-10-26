package lms.grid;
import lms.logistics.Item;
import lms.logistics.container.Producer;
import lms.logistics.container.Receiver;
import org.junit.Test;
import lms.logistics.belts.Belt;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class GameGridTest {

    GameGrid g1 = new GameGrid(3);
    GameGrid g2 = new GameGrid(10);

    Map<Coordinate, GridComponent> testGrid = g1.getGrid();

    Belt b1 = new Belt(3);
    Producer p1 = new Producer(3,new Item("a"));
    Receiver r1 = new Receiver(3,new Item("a"));

    @Test
    public void constructorTest() {
        assertEquals(g1.getRange(),3);
        assertEquals(g2.getRange(),10);
        assertEquals(g1.getGrid(), testGrid);
        assertTrue(g1.getRange()>=0);
        
    }
    @Test
    public void coordinateTest() {
        g1.getGrid().put(new Coordinate(-1,-1),b1);
        assertEquals(g1.getGrid(), testGrid);

        g1.setCoordinate(new Coordinate(-1,-1),b1);
        g1.setCoordinate(new Coordinate(1,-1),p1);
        assertEquals(g1.getGrid().get(new Coordinate(1,-1)),p1);
        assertEquals(g1.getGrid().get(new Coordinate(-1,-1)),b1);
        assertEquals(g1.getGrid().get(new Coordinate(1,-1)),p1);
        assertEquals(g1.getGrid().get(new Coordinate(-1,-1)),b1);
        g1.setCoordinate(new Coordinate(0,0),r1);
        assertEquals(g1.getRange(),3);
        assertEquals(g1.getGrid().keySet().size(),37);
        assertEquals(g2.getGrid().keySet().size(),331);

    }

}
