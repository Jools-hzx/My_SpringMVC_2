import com.hspedu.hzxspringmvc.context.HzxApplicationContext;
import com.hspedu.hzxspringmvc.parser.XMLParser;
import org.junit.Test;

/**
 * @author Zexi He.
 * @date 2023/4/17 14:18
 * @description:
 */
public class HzxSpringTest {

    @Test
    public void testGetPackages() {
        String scanPackages = XMLParser.getScanPackages("config.xml");
        System.out.println("待扫描的包:" + scanPackages);
    }

    @Test
    public void testScanPackage() {
        HzxApplicationContext hzxApplicationContext = new HzxApplicationContext();
        hzxApplicationContext.init("config.xml");
    }
}
