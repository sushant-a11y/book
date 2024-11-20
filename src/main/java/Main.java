

class Main {
    public static void main(String[] args) throws java.io.IOException, java.net.URISyntaxException {
        Book aliceInWonderland = new Book("https://www.gutenberg.org/cache/epub/11/pg11.txt");
        aliceInWonderland.translateBook();
        Book declarationOfIndependence = new Book("https://gutenberg.org/cache/epub/1/pg1.txt");
        declarationOfIndependence.translateBook();
    }

}