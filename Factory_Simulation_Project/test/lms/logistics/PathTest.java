package lms.logistics;

import lms.logistics.belts.Belt;
import lms.logistics.container.Producer;
import lms.logistics.container.Receiver;
import org.junit.Test;

import static org.junit.Assert.*;


public class PathTest {

    Belt b1 = new Belt(1);
    Belt b2 = new Belt(2);
    Belt b3 = new Belt(3);
    Producer p1 = new Producer(4,new Item("a"));
    Producer p2 = new Producer(5,new Item("b"));
    Producer p3 = new Producer(6,new Item("b"));
    Producer p4 = new Producer(5, new Item("a"));
    Receiver r1 = new Receiver(6,new Item("a"));

    Receiver r2 = new Receiver(7,new Item("b"));

    Path t1 = new Path(p1);
    Path t2 = new Path(b1);
    Path t3 = new Path(r1);
    Path t4 = new Path (b2,t1,t3);
    Path t5 = new Path (t4);
    Path t6 = new Path(b3,t4,new Path(p2));
    @Test
    public void constructorTest(){
        assertEquals(new Path(b1).getNode(),b1);
        assertNull(new Path(b1).getNext());
        assertNull(new Path(b1).getPrevious());
        assertEquals(new Path(p1).getNode(),p1);
        assertEquals(new Path(r1).getNode(),r1);

        assertNotNull(t4.getNode());
        assertNotNull(t1.getNode());
        assertNotNull(t5.getNode());
        assertEquals(t4.getNode(),b2);
        assertEquals(t4.getPrevious(),t1);
        assertEquals(t4.getNext(),t3);

        assertNotNull(t5.getNode());
        assertEquals(t5.getNode(),b2);
        assertEquals(t5.getPrevious(),t1);
        assertEquals(t5.getNext(),t3);

    }
    @Test
    public void linkTest() {
        assertEquals(t6.tail(), new Path(p2));
        assertEquals(t6.head(), new Path(p1));

        assertEquals(t4.tail(), new Path(r1));
        assertEquals(t4.head(), new Path(p1));


        t6.setNext(new Path(r2));
        assertEquals(t6.tail(), new Path(r2));
        t6.setPrevious(new Path(b3));
        assertEquals(t6.head(), new Path(b3));

        t1.setNext(t6);
        assertEquals(t1.head(), new Path(p1));
        t1.setPrevious(t6);
        assertEquals(t1.tail(), new Path(r2));

        assertEquals(t1.getNext(),t6);
        assertEquals(t1.getPrevious(),t6);

        assertEquals(t2.tail(), t2);
        assertEquals(t2.head(), t2);

        assertTrue(t1.equals(new Path(t1)));
        assertFalse(t1.equals(t2));
        assertFalse(new Path(p3).equals(new Path(p2)));
        assertFalse(new Path(p4).equals(new Path(p2)));

    }

    @Test
    public void stringTest() {
        assertEquals(t1.toString(),"START -> <Producer-4> -> END");

        assertEquals(t2.toString(),"START -> <Belt-1> -> END");
        t1.setNext(t2);
        assertEquals(t1.toString(),"START -> <Producer-4> -> <Belt-1> -> END");
        t2.setNext(new Path(r2));
        assertEquals(t1.toString(),"START -> <Producer-4> -> <Belt-1> -> <Receiver-7> -> END");
        t3.setNext(t1);
        t1.setPrevious(t3);
        assertEquals(t1.toString(),"START -> <Receiver-6> -> <Producer-4> -> <Belt-1> -> <Receiver-7> -> END");
        t2.setNext(t6);
        assertEquals(t1.toString(),"START -> <Receiver-6> -> <Producer-4> -> <Belt-1> -> <Belt-3> -> <Producer-5> -> END");

    }

}
