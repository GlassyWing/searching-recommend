package org.manlier.srapp.segment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class SegmentServiceTest {

    @Autowired
    SegmentService service;

    @Test
    public void testSentenceProcess() {
        List<String> strings = service.sentenceProcess("「台中」正确应该不会被切开", false);
        System.out.println(String.join("/", strings));
        System.out.println(service.tuneFreq("台", "中"));
        strings = service.sentenceProcess("「台中」正确应该不会被切开", false);
        System.out.println(String.join("/", strings));
        System.out.println(service.tuneFreq("台中"));
        strings = service.sentenceProcess("「台中」正确应该不会被切开", false);
        System.out.println(String.join("/", strings));
    }
}
