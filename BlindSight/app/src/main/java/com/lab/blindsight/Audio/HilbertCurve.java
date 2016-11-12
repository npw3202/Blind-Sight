package com.lab.blindsight.Audio;

/**
 * Created by Nicholas on 11/12/2016.
 */
import java.util.ArrayList;
import java.util.Collections;


public class HilbertCurve {
    static enum Direction{
        UP,
        RIGHT,
        DOWN,
        LEFT
    }
    static ArrayList<Direction> rotate(ArrayList<Direction> inputSequence){
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
    static ArrayList<Direction> rotate(ArrayList<Direction> inputSequence, int num){
        if(num == 0){
            return inputSequence;
        }else{
            return rotate(rotate(inputSequence,num - 1));
        }
    }
    static ArrayList<Direction> swap(ArrayList<Direction> inputSequence){
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
    static ArrayList<Direction> generateHilbert(int n){
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
    static int[][] generateWalk(int n){
        ArrayList<Direction> direction = generateHilbert(n);
        int[][] walk = new int[(int) Math.pow(2, n+1)][(int) Math.pow(2, n+1)];
        int iter = 0;
        int currX = 0;
        int currY = 0;
        for(Direction d : direction){
            walk[currX][currY] = iter;
            iter++;
            if(d == Direction.UP){
                currY += 1;
            }else if(d == Direction.DOWN){
                currY -= 1;
            }else if(d == Direction.RIGHT){
                currX += 1;
            }else if(d == Direction.LEFT){
                currX -= 1;
            }
        }
        return walk;
    }
    static ArrayList<Double> generateWalk(double[][] input){
        int max = Math.max(input.length, input[0].length);
        int n = (int) (Math.ceil(Math.log10(max)/Math.log10(2))) - 1;
        ArrayList<Direction> direction = generateHilbert(n);
        int currX = 0;
        int currY = 0;
        ArrayList<Double> result = new ArrayList<>();
        for(Direction d : direction){
            result.add(input[currX][currY]);
            if(d == Direction.UP){
                currY += 1;
            }else if(d == Direction.DOWN){
                currY -= 1;
            }else if(d == Direction.RIGHT){
                currX += 1;
            }else if(d == Direction.LEFT){
                currX -= 1;
            }
        }
        result.add(input[currX][currY]);
        return result;
    }
}
