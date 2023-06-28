package com.hw.cloudstorage.model.entity;

import com.hw.cloudstorage.model.enums.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.assertj.core.util.DateUtil.now;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BaseEntityTest {

    private final Date created = now();
    private final Date updated = now();
    private final Long id = 1L;
    BaseEntity sut;


    @BeforeAll
    public static void startClassTest() {
        System.out.println("---Start BaseEntity Class Test---");
    }

    @AfterAll
    public static void endClassTest() {
        System.out.println("---BaseEntity Class Test Completed---");
    }

    @BeforeEach
    void initOneTest(){
        sut = new BaseEntity(id, created, updated, Status.ACTIVE);
    }

    @Test
    void getId() {
        Long expected = id;
        Long actual = sut.getId();
        assertEquals(expected,actual);
    }

    @Test
    void getCreated() {
        Date expected = created;
        Date actual = sut.getCreated();
        assertEquals(expected,actual);
    }

    @Test
    void getUpdated() {
        Date expected = updated;
        Date actual = sut.getUpdated();
        assertEquals(expected,actual);
    }

    @Test
    void getStatus() {
        Status expected = Status.ACTIVE;
        Status actual = sut.getStatus();
        assertEquals(expected,actual);
    }

    @Test
    void setId() {
        Long expected = 2L;
        sut.setId(expected);
        Long actual = sut.getId();
        assertEquals(expected,actual);
    }

    @Test
    void setCreated() {
        Date expected = now();
        sut.setCreated(expected);
        Date actual = sut.getCreated();
        assertEquals(expected,actual);
    }

    @Test
    void setUpdated() {
        Date expected = now();
        sut.setUpdated(expected);
        Date actual = sut.getUpdated();
        assertEquals(expected,actual);
    }

    @Test
    void setStatus() {
        Status expected = Status.DELETED;
        sut.setStatus(expected);
        Status actual = sut.getStatus();
        assertEquals(expected,actual);
    }
}