package aaron.paper.service.test;

import aaron.paper.biz.service.PaperService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-05
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {
    @Autowired
    PaperService paperService;

    @Test
    public void t(){
    }
}
