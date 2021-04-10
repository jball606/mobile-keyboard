import com.beust.jcommander.internal.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
I am a scala developer, so I figured out how to do this in Java, though I would have had it done
quicker and in a lot less code had I used something more advanced.
It felt like I was working with stone knives and bearskins
 http://www.webster-dictionary.org/definition/stone+knives+and+bearskins
**/

public class keyboard {

    public static void main(String[] args) throws IOException {

        String s = "";
        int i;

        AutocompleteProvider AP = new AutocompleteProvider();
        AP.train(args[0]);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (!s.equals("!q")) {

            System.out.print("Enter String ");
            s = br.readLine();

            if(!s.equals("!q")) {
                if(s.length() > 0) {
                    List<Candidate> words = AP.getWords(s.trim());
                    if (words.size() > 0) {
                        String[] output = new String[words.size()];
                        for (i = 0; i < words.size(); i++) {
                            output[i] = words.get(i).getWord() + " (" + words.get(i).getConfidence() + ")";
                        }

                        System.out.println(String.join(", ", output));
                    } else {
                        System.out.println("No Results");
                    }
                } else {
                    System.out.println("Nothing Typed in");
                }
            } else {
                System.out.println("Goodbye");
            }
        }
    }

}


class Candidate {
    String word;
    int confidence;

    public Candidate(String word, int confidence) {
        this.word = word;
        this.confidence = confidence;
    }
    //returns the autocomplete candidate
    String getWord() {
        return word;
    }

    //returns the confidence*for the candidate
    Integer getConfidence() {
        return confidence;
    }

}

class AutocompleteProvider {

    Candidate[] dictionary = null;

    //returns list of candidates ordered by confidence*
    List<Candidate> getWords(String fragment) {
        List<Candidate> finallist = new ArrayList<>();

        int count, sortcount;

        Candidate[] cleanlist =  Arrays
                .stream(dictionary)
                .filter(i ->
                        i.getWord().contains(fragment.toLowerCase())  //I started with only checking front of the word, (nee) would get a result, but made it smarter to check all patterns (eed)
                ) //feeling a little at home, filter that data
                .toArray(Candidate[]::new);


        //weird way to do a groupedBy but Java doesn't have that one yet, so I get
        //the confidence numbers so I can ord by them first and order by alpha second

        Integer[] confidence_numbers = Arrays.stream(cleanlist).map(Candidate::getConfidence).distinct().sorted(Comparator.reverseOrder()).toArray(Integer[]::new);
        for(count=0;count<confidence_numbers.length;count++) {
            int finalCount = count;  //My IDE said this was important.  I assume it is a Java thing
            Candidate[] result = Arrays
                    .stream(cleanlist)
                    .filter(i -> i.getConfidence().equals(confidence_numbers[finalCount]))
                    .sorted(Comparator.comparing(Candidate::getWord))
                    .toArray(Candidate[]::new);


            for(sortcount=0;sortcount<result.length;sortcount++) {
                finallist.add(result[sortcount]);  //getting those responses back into a printable array
            }
        }

        return Lists.newArrayList(finallist);  //Seems I have to spend half my time converting data into new formats in these languages
    }

    //trains the algorithm with the provided passage
    void train(String passage) {
        String clean_passage = passage.replace(".","")
                .replace(",","")
                .replace(";","");  //Need to remove sentence structure for better matching, I am sure there is a regex somewhere that does this too.

        String[] words = clean_passage.toLowerCase().split(" "); //Split by space and lowercase so search works better

        dictionary = Arrays  //I am going to scala this thing the best I can
                .stream(words)
                .distinct() //Need a unique list
                .filter(i -> i.length() > 2 ) //Don't waste my time with words less then 2 characters
                .map( my_word -> new Candidate(my_word, Arrays.stream(words).filter(i -> i.equals(my_word)).toArray(String[]::new).length))
                .toArray(Candidate[]::new);

    }

}
