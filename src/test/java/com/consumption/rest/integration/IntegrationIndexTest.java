/**
 * 
 */
package com.consumption.rest.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import com.consumption.rest.exceptions.ResponseError;
import com.consumption.rest.pojos.IndexData;
import com.consumption.rest.utils.BaseClass;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationIndexTest extends BaseClass {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String HOSTNAME = "http://localhost:" + port;

    @Test
    public void getBasicApplicationData() throws Exception {
        HOSTNAME = "http://localhost:" + port;
        IndexData indexData = this.restTemplate.getForObject(HOSTNAME + "/api/v0/", IndexData.class);
        assertThat(indexData).isNotNull();
        getLogger().info("Index Data valid " + indexData);
    }

    @Test
    public void getRootErrorPage() throws Exception {
        HOSTNAME = "http://localhost:" + port;
        ResponseError responseError = this.restTemplate.getForObject(HOSTNAME + "/api/v0/error", ResponseError.class);
        assertThat(responseError).isNotNull();
        getLogger().info("Response error valid " + responseError);
    }
}
