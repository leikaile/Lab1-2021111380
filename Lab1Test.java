import org.junit.Test;

public class Lab1Test {

    @Test
    public void testReadFileAndCreateGraph() {
        Lab1.readFileAndCreateGraph("D:\\LAB\\lab1\\src\\test.txt");
    }
    @Test
    public void testqueryBridgeWords() {
        System.out.println(Lab1.queryBridgeWords( "A" , "B"));
        System.out.println(Lab1.queryBridgeWords( "the" , "B"));
        System.out.println(Lab1.queryBridgeWords( "ring" , "the"));
        System.out.println(Lab1.queryBridgeWords( "freedom" , "the"));
    }



}