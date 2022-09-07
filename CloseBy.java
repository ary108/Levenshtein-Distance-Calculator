package com.company;

public class CloseBy {

    public CloseBy() {}

    //given two words, checks if there is one edit distance

    public static boolean closeBy(String y, String z){
        // if difference is greater than 1, then it cannot be a closeby neighbor
        if(Math.abs(y.length()-z.length()) > 1) {
            return false;
        }
        //if words don't equal, increment edits. move forward for longer string
        int yObserver = 0, zObserver = 0;

        // using y observer and z observer to parse through each string
        int edits = 0;
        //while it doesn't reach the end
        while(yObserver != y.length() && zObserver != z.length()) {
            if(y.charAt(yObserver) != z.charAt(zObserver)) {
                // already has one edit distance
                if(edits == 1) return false;
                //move on to last letter
                if(y.length() > z.length()) yObserver++;
                else if(y.length() < z.length()) zObserver ++;
                    // if same, move on to next letter
                else {
                    yObserver++;
                    zObserver++;
                }
            }
        }
        // observer hasn't reached the end of a word, so will need an edit
        if(yObserver < y.length() || zObserver < z.length()) {
            edits++;
        }
        // if true, they're one way neighbors, if false, they aren't one way neighbors
        return edits == 1;
    }
}
