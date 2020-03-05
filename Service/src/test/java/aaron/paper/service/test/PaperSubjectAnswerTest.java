package aaron.paper.service.test;

import aaron.paper.biz.service.PaperSubjectAnswerService;
import aaron.paper.pojo.model.PaperSubjectAnswer;
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
 * @since 2020-03-05
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class PaperSubjectAnswerTest {
    @Autowired
    PaperSubjectAnswerService service;

    @Test
    public void test(){
        List<Long> list = new ArrayList<>();
        list.add(660540243451711488L);
        list.add(660540243451711493L);
        list.add(660540243451711498L);
        List<PaperSubjectAnswer> res = service.listAnswerBySubjectIdList(list);
    }
}
