package aaron.paper.service.test;

import aaron.paper.biz.service.PaperSubjectService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaoyouming
 * @version 1.0
 * @since 2020-03-20
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PaperSubjectTest {

    @Autowired
    PaperSubjectService service;

    @Test
    public void testQuerySubject(){
        List<Long> list = new ArrayList<>();
        list.add(660540242306666496L);
        System.out.println(service.listSubjectByPaperIdList(list));
    }
}
