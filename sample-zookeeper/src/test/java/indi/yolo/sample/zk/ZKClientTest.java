package indi.yolo.sample.zk;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ZKClientTest {

    @BeforeEach
    public void setUp() {
//        PropertyConfigurator.configure(Paths.get(System.getProperty("user.dir"),
//                "src/main/resources", "log4j.properties").toString());
        ZKClient.getInstance().connect("127.0.0.1:2181");
    }

    @AfterEach
    public void tearDown(){
        ZKClient.getInstance().close();
    }

    @Test
    public void delete() throws Exception {
        ZKClient.getInstance().delete("/test");
    }

    @Test
    public void read() throws Exception {
        System.out.println(ZKClient.getInstance().read("/"));
    }

    @Test
    public void ls() throws Exception {
        for (String l : ZKClient.getInstance().ls("/")) {
            System.out.println(l);
        }
    }
}