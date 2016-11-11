import java.util.ArrayList;
import java.util.Collections;

public class HilbertCurve {
    static enum Direction{
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
    ArrayList<Direction> rotate(ArrayList<Direction> inputSequence){
        ArrayList<Direction> sequence = new ArrayList<>();
        for(Direction d : inputSequence){
            if(d == Direction.UP){
                sequence.add(Direction.RIGHT);
            }else if(d == Direction.RIGHT){
                sequence.add(Direction.DOWN);
            }else if(d == Direction.DOWN){
                sequence.add(Direction.LEFT);
            }else if(d == Direction.LEFT){
                sequence.add(Direction.UP);
            }
        }
        return sequence;
    }
    ArrayList<Direction> rotate(ArrayList<Direction> inputSequence, int num){
        if(num == 0){
            return inputSequence;
        }else{
            return rotate(rotate(inputSequence,num - 1));
        }
    }
    ArrayList<Direction> swap(ArrayList<Direction> inputSequence){
        ArrayList<Direction> sequence = new ArrayList<>();
        for(Direction d : inputSequence){
            if(d == Direction.UP){
                sequence.add(Direction.DOWN);
            }else if(d == Direction.RIGHT){
                sequence.add(Direction.LEFT);
            }else if(d == Direction.DOWN){
                sequence.add(Direction.UP);
            }else if(d == Direction.LEFT){
                sequence.add(Direction.RIGHT);
            }
        }
        Collections.reverse(sequence);
        return sequence;
    }
    ArrayList<Direction> generateHilbert(int n){
        ArrayList<Direction> startSequence = new ArrayList<>();
        if(n == 0){
            startSequence = new ArrayList<Direction>();
            startSequence.add(Direction.UP);
            startSequence.add(Direction.RIGHT);
            startSequence.add(Direction.DOWN);
            return startSequence;
        }else{
            startSequence = generateHilbert(n - 1);
        }
        ArrayList<Direction> sequence = new ArrayList<>();
        sequence.addAll(swap(rotate(startSequence,1)));
        sequence.add(Direction.UP);
        sequence.addAll((rotate(startSequence,0)));
        sequence.add(Direction.RIGHT);
        sequence.addAll((rotate(startSequence,0)));
        sequence.add(Direction.DOWN);
        sequence.addAll(swap(rotate(startSequence,3)));
        return sequence;
    }
    public static void main(String[] args) {
        HilbertCurve hc = new HilbertCurve();
        ArrayList<Direction> seq = (hc.generateHilbert(2));
        for(Direction i : seq){
            if(i == Direction.UP){
                System.out.print('U');
            } else if(i == Direction.RIGHT){
                System.out.print('R');
            } else if(i == Direction.DOWN){
                System.out.print('D');
            } else if(i == Direction.LEFT){
                System.out.print('L');
            }
        }
    }
}
