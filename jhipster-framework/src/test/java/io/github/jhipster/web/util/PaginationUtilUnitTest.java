/*
 * Copyright 2016-2019 the original author or authors from the JHipster project.
 *
 * This file is part of the JHipster project, see https://www.jhipster.tech/
 * for more information.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jhipster.web.util;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests based on parsing algorithm in app/components/util/pagination-util.service.js
 *
 * @see PaginationUtil
 */
public class PaginationUtilUnitTest {

    private static final String BASE_URL = "/api/_search/example";

    private MockHttpServletRequest mockRequest;

    @Before
    public void init() {
        mockRequest = new MockHttpServletRequest();
        mockRequest.setRequestURI(BASE_URL);
    }

    @Test
    public void generatePaginationHttpHeadersTest() {
        String baseUrl = "/api/_search/example";
        List<String> content = new ArrayList<>();
        Page<String> page = new PageImpl<>(content, PageRequest.of(6, 50), 400L);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        List<String> strHeaders = headers.get(HttpHeaders.LINK);
        assertNotNull(strHeaders);
        assertTrue(strHeaders.size() == 1);
        String headerData = strHeaders.get(0);
        assertTrue(headerData.split(",").length == 4);
        String expectedData = "</api/_search/example?page=7&size=50>; rel=\"next\","
            + "</api/_search/example?page=5&size=50>; rel=\"prev\","
            + "</api/_search/example?page=7&size=50>; rel=\"last\","
            + "</api/_search/example?page=0&size=50>; rel=\"first\"";
        assertEquals(expectedData, headerData);
        List<String> xTotalCountHeaders = headers.get("X-Total-Count");
        assertTrue(xTotalCountHeaders.size() == 1);
        assertTrue(Long.valueOf(xTotalCountHeaders.get(0)).equals(400L));
    }

    @Test
    public void commaTest() {
        mockRequest.setParameter("query", "Test1, test2");
        String baseUrl = "/api/_search/example";
        List<String> content = new ArrayList<>();
        Page<String> page = new PageImpl<>(content);
        String query = "Test1, test2";
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        List<String> strHeaders = headers.get(HttpHeaders.LINK);
        assertNotNull(strHeaders);
        assertTrue(strHeaders.size() == 1);
        String headerData = strHeaders.get(0);
        assertTrue(headerData.split(",").length == 2);
        String expectedData = "</api/_search/example?page=0&size=0&query=Test1%2C+test2>; rel=\"last\","
            + "</api/_search/example?page=0&size=0&query=Test1%2C+test2>; rel=\"first\"";
        assertEquals(expectedData, headerData);
        List<String> xTotalCountHeaders = headers.get("X-Total-Count");
        assertTrue(xTotalCountHeaders.size() == 1);
        assertTrue(Long.valueOf(xTotalCountHeaders.get(0)).equals(0L));
    }

    @Test
    public void multiplePagesTest() {
        mockRequest.setParameter("query", "Test1, test2");
        String baseUrl = "/api/_search/example";
        List<String> content = new ArrayList<>();

        // Page 0
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 50), 400L);
        String query = "Test1, test2";
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        List<String> strHeaders = headers.get(HttpHeaders.LINK);
        assertNotNull(strHeaders);
        assertTrue(strHeaders.size() == 1);
        String headerData = strHeaders.get(0);
        assertTrue(headerData.split(",").length == 3);
        String expectedData = "</api/_search/example?page=1&size=50&query=Test1%2C+test2>; rel=\"next\","
            + "</api/_search/example?page=7&size=50&query=Test1%2C+test2>; rel=\"last\","
            + "</api/_search/example?page=0&size=50&query=Test1%2C+test2>; rel=\"first\"";
        assertEquals(expectedData, headerData);
        List<String> xTotalCountHeaders = headers.get("X-Total-Count");
        assertTrue(xTotalCountHeaders.size() == 1);
        assertTrue(Long.valueOf(xTotalCountHeaders.get(0)).equals(400L));

        // Page 1
        mockRequest.setParameter("page", "1");
        page = new PageImpl<>(content, PageRequest.of(1, 50), 400L);
        query = "Test1, test2";
        headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        strHeaders = headers.get(HttpHeaders.LINK);
        assertNotNull(strHeaders);
        assertTrue(strHeaders.size() == 1);
        headerData = strHeaders.get(0);
        assertTrue(headerData.split(",").length == 4);
        expectedData = "</api/_search/example?page=2&size=50&query=Test1%2C+test2>; rel=\"next\","
            + "</api/_search/example?page=0&size=50&query=Test1%2C+test2>; rel=\"prev\","
            + "</api/_search/example?page=7&size=50&query=Test1%2C+test2>; rel=\"last\","
            + "</api/_search/example?page=0&size=50&query=Test1%2C+test2>; rel=\"first\"";
        assertEquals(expectedData, headerData);
        xTotalCountHeaders = headers.get("X-Total-Count");
        assertTrue(xTotalCountHeaders.size() == 1);
        assertTrue(Long.valueOf(xTotalCountHeaders.get(0)).equals(400L));

        // Page 6
        mockRequest.setParameter("page", "6");
        page = new PageImpl<>(content, PageRequest.of(6, 50), 400L);
        headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        strHeaders = headers.get(HttpHeaders.LINK);
        assertNotNull(strHeaders);
        assertTrue(strHeaders.size() == 1);
        headerData = strHeaders.get(0);
        assertTrue(headerData.split(",").length == 4);
        expectedData = "</api/_search/example?page=7&size=50&query=Test1%2C+test2>; rel=\"next\","
            + "</api/_search/example?page=5&size=50&query=Test1%2C+test2>; rel=\"prev\","
            + "</api/_search/example?page=7&size=50&query=Test1%2C+test2>; rel=\"last\","
            + "</api/_search/example?page=0&size=50&query=Test1%2C+test2>; rel=\"first\"";
        assertEquals(expectedData, headerData);
        xTotalCountHeaders = headers.get("X-Total-Count");
        assertTrue(xTotalCountHeaders.size() == 1);
        assertTrue(Long.valueOf(xTotalCountHeaders.get(0)).equals(400L));

        // Page 7
        mockRequest.setParameter("page", "7");
        page = new PageImpl<>(content, PageRequest.of(7, 50), 400L);
        headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        strHeaders = headers.get(HttpHeaders.LINK);
        assertNotNull(strHeaders);
        assertTrue(strHeaders.size() == 1);
        headerData = strHeaders.get(0);
        assertTrue(headerData.split(",").length == 3);
        expectedData = "</api/_search/example?page=6&size=50&query=Test1%2C+test2>; rel=\"prev\","
            + "</api/_search/example?page=7&size=50&query=Test1%2C+test2>; rel=\"last\","
            + "</api/_search/example?page=0&size=50&query=Test1%2C+test2>; rel=\"first\"";
        assertEquals(expectedData, headerData);
    }

    @Test
    public void greaterSemicolonTest() {
        mockRequest.setParameter("query", "Test>;test");
        String baseUrl = "/api/_search/example";
        List<String> content = new ArrayList<>();
        Page<String> page = new PageImpl<>(content);
        String query = "Test>;test";
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        List<String> strHeaders = headers.get(HttpHeaders.LINK);
        assertNotNull(strHeaders);
        assertTrue(strHeaders.size() == 1);
        String headerData = strHeaders.get(0);
        assertTrue(headerData.split(",").length == 2);
        String[] linksData = headerData.split(",");
        assertTrue(linksData.length == 2);
        assertTrue(linksData[0].split(">;").length == 2);
        assertTrue(linksData[1].split(">;").length == 2);
        String expectedData = "</api/_search/example?page=0&size=0&query=Test%3E%3Btest>; rel=\"last\","
            + "</api/_search/example?page=0&size=0&query=Test%3E%3Btest>; rel=\"first\"";
        assertEquals(expectedData, headerData);
        List<String> xTotalCountHeaders = headers.get("X-Total-Count");
        assertTrue(xTotalCountHeaders.size() == 1);
        assertTrue(Long.valueOf(xTotalCountHeaders.get(0)).equals(0L));
    }

    @Test
    public void sortAndCriteriaTest() {
        mockRequest.setRequestURI("/api/users");
        mockRequest.addParameter("login.contains", "adm");
        mockRequest.addParameter("name", "name,asc");
        List<String> content = new ArrayList<>();
        Page<String> page = new PageImpl<>(content, PageRequest.of(0, 50), 400L);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(mockRequest, page);
        List<String> strHeaders = headers.get(HttpHeaders.LINK);
        String expectedData = "</api/users?page=1&size=50&login.contains=adm&name=name%2Casc>; rel=\"next\"," +
            "</api/users?page=7&size=50&login.contains=adm&name=name%2Casc>; rel=\"last\"," +
            "</api/users?page=0&size=50&login.contains=adm&name=name%2Casc>; rel=\"first\"";
        assertNotNull(strHeaders);
        assertEquals(1, strHeaders.size());
        String headerData = strHeaders.get(0);
        assertEquals(expectedData, headerData);
    }
}
