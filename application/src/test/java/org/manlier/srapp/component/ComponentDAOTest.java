package org.manlier.srapp.component;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.manlier.srapp.Application;
import org.manlier.srapp.dao.ComponentDAO;
import org.manlier.srapp.domain.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ComponentDAOTest {

    @Autowired
    ComponentDAO dao;

    @Test
    public void testSelect() throws IOException {
        System.out.println(dao.getComponentByName("A1"));
        ;
    }

    @Test
    public void testGetById() {
        System.out.println(dao.getComponentById(10));
    }

    @Test
    public void testAddComponent() throws IOException {
        Component component = new Component("A1", "None");
        dao.addComponent(component);
    }

    @Test
    public void testUpdateComponent() {
        Component component = new Component("A1", "Some");
        dao.updateComponent(component);
    }

}
