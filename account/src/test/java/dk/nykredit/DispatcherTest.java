package dk.nykredit;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DispatcherTest {

    @Test
    public void testDispatch() throws Exception {
        Map<String, Runnable> commands = new HashMap<>();

        commands.put("v1", () -> version1("This was "));
        commands.put("v2", () -> version2("This was "));

        String cmd = "v2";
        commands.get(cmd).run();
    }

    public void version1 (String acceptHeader) {
        System.out.println(acceptHeader + "Version 1");
    }

    private void version2(String acceptHeader) {
        System.out.println(acceptHeader + "Version 2");
    }

}
