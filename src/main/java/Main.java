

class Main {
    public static void main(String[] args) throws java.io.IOException, java.net.URISyntaxException {
        Book book1 = new Book("https://www.gutenberg.org/cache/epub/11/pg11.txt");
        book1.translateBook();
        Book book2 = new Book("https://gutenberg.org/cache/epub/1/pg1.txt");
        book2.translateBook();
        System.out.println(book1.getBookName()+" number of words: "+book1.getWordCount());
        System.out.println(book2.getBookName()+" number of words: "+book2.getWordCount());
    }
}