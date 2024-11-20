//A few assumptions.......

//Words will be separated by spaces. 
//There can be punctuation in a word, we will only add/keep punctuation at the end of a string if it is at the end of a string.
//    for examples: Hello.==> Ellohay.    Good-bye! ==> Ood-byegay!    so... ==> osay...

import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Book {
    private final String vowels = "aeiouy";
    private final String punctuation = ";:'\",.?!$*()&\n_“‘’”[]";
    private final String numbers = "1234567890";
    private final URL url;
    private final String bookName;
    private int wordCount = 0;
    private final ProgressBar progressBar;
    private String text = "";
    private int currentWord = 0;

    public Book(String url) throws java.io.IOException, URISyntaxException {
        int bytesRead = 0;
        this.url = new URI(url).toURL();


        URLConnection connection = this.url.openConnection();
        int size = connection.getContentLength();
        progressBar = ProgressBar.builder().setInitialMax(size).setTaskName("Downloading Book...").setUnit(" bytes", 1).setUpdateIntervalMillis(300).setStyle(ProgressBarStyle.builder().colorCode((byte) 37).fractionSymbols(" ▏▎▍▌▋▊▉").block('█').build()).setMaxRenderedLength(120).build();

        Scanner myReader = new Scanner(this.url.openStream());

        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            bytesRead += data.length() + 1;
            text += data + "\n";
            progressBar.stepTo(bytesRead);
        }

        progressBar.stepTo(size);
        myReader.close();
        progressBar.close();

        this.findWordCount();

        int newLineIndex = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                newLineIndex = i;
                break;
            }
        }
        this.bookName = text.substring(32, newLineIndex);
    }

    public String pigLatin(String word) {
        int consonantIndex = 0;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (numbers.contains(String.valueOf(c)) && i == 0) {
                return word;
            }
            if (vowels.contains(String.valueOf(c)) && !(c == 'y' && i == 0)) { // treat y as consonant if it is in the
                // beginning, otherwise y is treated as a
                // vowel
                consonantIndex = i - 1;
                break;
            }
        }
        if (consonantIndex == -1) {
            return word + "yay";
        } else {
            return word.substring(consonantIndex + 1) + word.substring(0, consonantIndex + 1) + "ay";
        }

    }

    public int startPunctuation(String word) {
        int punctuationIndex;

        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!punctuation.contains(String.valueOf(c))) {
                if (i == 0) {
                    return -1;
                } else {
                    punctuationIndex = i - 1;
                    return punctuationIndex;
                }
            }
        }
        return -1;
    }

    public int endPunctuation(String word) {
        int punctuationIndex;
        StringBuilder tmp = new StringBuilder();

        for (int i = word.length() - 1; i >= 0; i--) {
            tmp.append(word.charAt(i));

        }
        word = tmp.toString();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!punctuation.contains(String.valueOf(c))) {
                if (i == 0) {
                    return -1;
                } else {
                    punctuationIndex = i - 1;
                    return word.length() - punctuationIndex;
                }
            }
        }
        return -1;
    }

    public String translateWord(String word) {
        currentWord += 1;
        boolean capitalize = false;
        if (this.isWholeWordPunctuation(word)) {
            return word;
        }
        String beginningPunctuation = "";
        String endingPunctuation = "";

        if (this.startPunctuation(word) != -1) {
            beginningPunctuation = word.substring(0, this.startPunctuation(word) + 1);
            word = word.substring(this.startPunctuation(word) + 1);
        }
        if (this.endPunctuation(word) != -1) {
            endingPunctuation = word.substring(this.endPunctuation(word) - 1);
            word = word.substring(0, this.endPunctuation(word) - 1);
        }
        if (this.sameCase(word) && word.length() > 1) {
            return beginningPunctuation + this.pigLatin(word).toUpperCase() + endingPunctuation;
        }
        if (Character.isUpperCase(word.charAt(0))) {
            capitalize = true;
            word = word.substring(0, 1).toLowerCase() + word.substring(1);
        }

        if (capitalize) {
            return beginningPunctuation + this.pigLatin(word).substring(0, 1).toUpperCase() + this.pigLatin(word).substring(1) + endingPunctuation;
        } else {
            return beginningPunctuation + this.pigLatin(word) + endingPunctuation;
        }

    }

    public String translateSentence(String sentence, boolean progressBar) {
        sentence = sentence.replaceAll("\n", " \n");
        ProgressBar bar = null;
        if (progressBar) {
            bar = ProgressBar.builder().setInitialMax(wordCount).setTaskName("Translating Words... ").setUnit(" words", 1).setUpdateIntervalMillis(300).setStyle(ProgressBarStyle.builder().colorCode((byte) 37).fractionSymbols(" ▏▎▍▌▋▊▉").block('█').build()).setMaxRenderedLength(120).build();
        }
        StringBuilder retSentence = new StringBuilder();
        String[] words = sentence.split(" ");
        ArrayList<String> translatedWords = new ArrayList<>();
        for (String s : words) {
            translatedWords.add(this.translateWord(s));
            if (progressBar) {
                bar.stepTo(currentWord);
            }
        }
        if (progressBar) {
            bar.stepTo(wordCount);
            bar.close();
            bar = ProgressBar.builder().setInitialMax(wordCount).setTaskName("Joining Translated Words... ").setUnit(" words", 1).setUpdateIntervalMillis(300).setStyle(ProgressBarStyle.builder().colorCode((byte) 37).fractionSymbols(" ▏▎▍▌▋▊▉").block('█').build()).setMaxRenderedLength(120).build();
        }
        currentWord = 0;
        for (int i = 0; i < translatedWords.size(); i++) {
            currentWord += 1;
            if (i == translatedWords.size() - 1) {
                if (progressBar) {
                    bar.stepTo(currentWord);
                }
                retSentence.append(translatedWords.get(i));
            } else {
                if (progressBar) {
                    bar.stepTo(currentWord);
                }
                retSentence.append(translatedWords.get(i)).append(" ");
            }
        }
        if (progressBar) {
            bar.stepTo(wordCount);
            bar.close();
        }
        return retSentence.toString();
    }

    public void translateBook() throws IOException {
        int startUnnecessary = 0;
        int endUnnecessary = 0;
        String startText;
        String endText;
        startUnnecessary = this.text.indexOf("*** START OF THE PROJECT GUTENBERG EBOOK");
        for (int i = startUnnecessary + 6; i < text.length(); i++) {
            if (text.startsWith("***", i - 3)) {
                startUnnecessary = i;
                break;
            }

        }
        startText = text.substring(0, startUnnecessary);
        text = text.substring(startUnnecessary);
        endUnnecessary = this.text.indexOf("*** END OF THE PROJECT GUTENBERG EBOOK");

        endText = text.substring(endUnnecessary);
        text = text.substring(0, endUnnecessary);
        this.writeOutBook(startText + translateSentence(this.text, true) + endText);
    }

    public int getWordCount(){
        return wordCount;
    }

    private void writeOutBook(String text) throws IOException {
        String outDirectory = "Anslatedtray Extstay";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outDirectory + "/" + this.translateSentence(this.bookName, false) + ".txt"));
            writer.write(text);
            writer.close();
        } catch (java.io.FileNotFoundException e) {
            Files.createDirectories(Paths.get(outDirectory));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outDirectory + "/" + this.translateSentence(this.bookName, false) + ".txt"));
            writer.write(text);
            writer.close();
        }
    }

    private void findWordCount() {
        String[] splitWords = this.text.replaceAll("\\s+", " ").split("\\s|\n");
        List<String> listSplitWords = Arrays.asList(splitWords);
        List<String> words = new ArrayList<>(listSplitWords);
        for (int i = 0; i < words.size(); i++) {
            String a = words.get(i);
            if (this.isWholeWordPunctuation(a.toLowerCase())) {
                words.remove(i);
            }
        }

        this.wordCount = words.size();
    }

    private boolean isWholeWordPunctuation(String word) {
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!punctuation.contains(String.valueOf(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean sameCase(String str) {
        for (char c : str.toCharArray()) {
            if (!(Character.isUpperCase(c)) || (Character.isLowerCase(c))) return false;
        }
        return true;
    }
}