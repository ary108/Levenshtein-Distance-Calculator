package com.company;

import java.io.*;
import java.util.*;

public class DictionaryWords {
    // create a hashmap of all words in dictionary and one way neighbros
    private HashMap<String, ArrayList<String>> nearbyWords = new HashMap<>();

    public DictionaryWords() {
        //check if we have file, else create serialized object file with the hashmap
        File f = new File(System.getProperty("user.home")+"\\Downloads\\neighbors_map.ser");
        if(f.exists()){
            try(ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)))) {
                @SuppressWarnings("unchecked")
                        //create a hashmap to store the neighbors map serialized object file
                    HashMap<String, ArrayList<String>> temp = (HashMap<String, ArrayList<String>>) ois.readObject();
                //set them both equal
                nearbyWords = temp;
                // exceptions to make sure nothing goes wrong
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }

        nearbyWords = new HashMap<>();
        System.out.println("Please be patient while we read the dictionary");
        // arraylist to hold words
        ArrayList<String> allWords = new ArrayList<>();

        //read in every word from the dictionary file
        try(BufferedReader br = new BufferedReader(new FileReader(System.getProperty("user.home") + "/Downloads/dictionary.txt"))) {
            //read every line till done
            String line;
            while((line = br.readLine()) != null) {
                allWords.add(line);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //for each word in the dictionary, run closeby method
        allWords.parallelStream().forEach((String word) -> {
           ArrayList<String> wordsNearby = new ArrayList<>();
           for (String chosen:allWords) {
               //if it is a closeby neighbor, add it to the list of one way neighbors in hashmap
                if(CloseBy.closeBy(chosen,word)) {
                    wordsNearby.add(chosen);
                }
           }

           //synchronize to put it in an orderly way, since we're using multiple threads
           synchronized (this) {
               nearbyWords.put(word,wordsNearby);
           }
        });

        //write the neighbors serialized file
        try(ObjectOutputStream outputStream =
                new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)))) {
            outputStream.writeObject(nearbyWords);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // how many edits does it take to get from one to another
    public int editCount(String a, String b) {

        //get rid of whitespace and make lowercase
        a = a.trim().toLowerCase();
        b = b.trim().toLowerCase();

        //checks that both of them are in the hashmap
        if(!(nearbyWords.containsKey(a) && nearbyWords.containsKey(b))) return -1;

        //create new queue to check if words are equal to the target word
        Queue<Status> q = new ArrayDeque<>();
        //keep track of all the words we've checked
        HashSet<String> seen = new HashSet<>();
        //add first word
        seen.add(a);
        //create queue of statuses, each one holding the word and the edits it has
        q.add(new Status(a,0));
        while(!q.isEmpty()) {
            Status current = q.remove();
            //if words are equal, return the number of edits the word has
            if(current.word.equals(b)) return current.edit;

            // get all the neighbors of the current word
            for(String nearbyWord: nearbyWords.get(current.word)) {
                //if we haven't already checked it,
                if(!seen.contains(nearbyWord)) {
                    //add to the the queue
                    q.add(new Status(nearbyWord,current.edit+1));
                    seen.add(nearbyWord);
                }
            }
        }
        //return -1 if we ran through the entire method and there isn't a path
        return -1;
    }
}
