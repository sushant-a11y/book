

class Main {
    public static void main(String[] args) throws java.io.IOException, java.net.URISyntaxException {
        Book aliceInWonderland = new Book("https://www.gutenberg.org/cache/epub/11/pg11.txt", "Alice'syay Adventuresyay inyay Onderlandway.txt", "Alice\'s Adventures in Wonderland");
        aliceInWonderland.translateBook();
    }

}