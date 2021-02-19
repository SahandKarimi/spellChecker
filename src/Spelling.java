/****************************************************************************
 Sahand Karimi
 February 13, 2021

 File Name:      Spelling.java
 Description:    Takes a file filled with words, and runs it over
 								 the spellchecker code to output for any potential
                 errors. Writes out the total number of words in the list.

 ****************************************************************************/
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Spelling {

	private final mapSpelling nWords = new mapSpelling();

	public Spelling(String file, boolean sig, String fileInput) throws IOException {
		File f = new File(file);
		FileReader in = new FileReader(f);
		char[] buffer = new char[(int)f.length()];
		in.read(buffer);
		int begin = 0;
		boolean isUpper = false;
		for (int i = 0; i < buffer.length; i++) {
			while ((('a' > buffer[i] || buffer[i] > 'z') && ('A' > buffer[i] || buffer[i] > 'Z')) && i < buffer.length - 1) {
				i++;
			}

			begin = i;
			while ((('a' <= buffer[i] && buffer[i] <= 'z') || (isUpper = ('A' <= buffer[i] && buffer[i] <='Z'))) && i < (buffer.length-1)){
				if(isUpper) buffer[i] = Character.toLowerCase(buffer[i]);
				i++;
			}
			String word  = new String(buffer, begin, i - begin);
			nWords.put(word, (short) (nWords.get_key(word) + 1));
		}
		in.close();
		if (sig){
			fixALl(fileInput);
		}
	}

	/**
	 * Function: edits
	 * Takes care of editing the wrong spelled word
	 *
	 * @param word of the string to be corrected
	 * @return the list
	 */
	private final ArrayList<String> edits(String word) {
		ArrayList<String> result = IntStream.range(0, word.length()).mapToObj(i -> word.substring(0, i) + word.substring(i + 1)).collect(Collectors.toCollection(ArrayList::new));
		IntStream.range(0, word.length() - 1).mapToObj(i -> word.substring(0, i) + word.substring(i + 1, i + 2) + word.substring(i, i + 1) + word.substring(i + 2)).forEach(result::add);
		{
			int i=0;
			while (i < word.length()) {
				for(char c = 'a'; c <= 'z'; ++c) result.add(word.substring(0, i) + c + word.substring(i+1));
				++i;
			}
		}
		int i=0;
		while (i <= word.length()) {
			for(char c = 'a'; c <= 'z'; ++c) result.add(word.substring(0, i) + c + word.substring(i));
			++i;
		}
		return result;
	}

	/**
	 * Function: correct
	 * Take care of going through the list and
	 * editing the word to the correct spelling
	 *
	 * @param word of the string to be corrected
	 * @return the corrected word
	 */
	public final String correct(String word) {
		if (!nWords.contains(word)) {
			ArrayList<String> list = edits(word);
			IdentityHashMap <Short, String> candidates = list.stream().filter(nWords::contains).collect(Collectors.toMap(nWords::get_key, s -> s, (a, b) -> b, IdentityHashMap::new));
			if (candidates.size() > 0)
				return candidates.get(Collections.max(candidates.keySet()));
			for (String s : list) {
				for (String w : edits(s))
					if (nWords.contains(w)) candidates.put(nWords.get_key(w), w);
			}
			return candidates.size() > 0 ? candidates.get(Collections.max(candidates.keySet())) : word;
		} else {
			return word;
		}
	}
	String rmPunctuation(String word){
		if (word.charAt(word.length() - 1) == '.' || word.charAt(word.length() - 1) == ',' || word.charAt(word.length() - 1) == ';'){
			word = word.substring(0, word.length() - 1);
		}
		return word;
	}

	/**
	 * Function: fixALl
	 * Goes through every word in the file and
	 * fixes each one by one
	 *
	 * @param file of the list of strings to be
	 *             corrected
	 * @return void
	 */
	public void fixALl(String file) throws IOException {

		File f1= new File(file); //Creation of File Descriptor for input file
		PrintWriter writer = new PrintWriter("C:/Users/sahan/IdeaProjects/spellChecker/src/output.txt");

		String[] words=null;
		FileReader fr = new FileReader(f1);
		BufferedReader br = new BufferedReader(fr);
		String s;
		int count = 0;
		int missed = 0;
		while((s=br.readLine())!=null)
		{
			words=s.split(" ");
			for (String word : words)
			{
				count++;
				word = rmPunctuation(word);
				if (!nWords.contains(word.toLowerCase()))
				{

					String corrected = correct(word);
					writer.write(word);
					writer.write(" ");
					writer.write("corrected: ");
					writer.write(corrected);
					writer.write(" ");
					missed++;

				}

			}
			writer.write('\n');
			writer.write("missed word count: " + missed);
			writer.write('\n');
			writer.write("total word count: " + count);
			writer.close();

		}


	}

	/**
	 * Function: main
	 * main method (argument passed by user)
	 * Gives the absolute path of the file
	 *
	 * @param args of the arguments listed by user
	 * @return void
	 */
	public static void main(String args[]) throws IOException {

		if(args.length > 0) {
		new Spelling("C:/Users/sahan/IdeaProjects/spellChecker/src/words.txt" , true, args[0]);
		}

	}

}
