public class SizeTest extends TestUtils{
    FinalCache<String, String> cache = new FinalCache<>(100);


    @Override
    void newEntryHandler(String entry) {

    }

    @Override
    void doomHandler(String entry) {
    }

    @Override
    void accessHandler(String entry) {
    }

    @Override
    void removalHandler(String entry) {
    }
}
