/**
 * 
 */
package com.consumption.rest.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import com.consumption.rest.exceptions.ResponseError;
import com.consumption.rest.pojos.Data;
import com.consumption.rest.pojos.MeterReadingData;
import com.consumption.rest.pojos.Profile;
import com.consumption.rest.utils.BaseClass;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationControllerTest extends BaseClass {

    //    @Autowired
    //    private ProfileController controller;

    //    @Test
    //    public void contexLoads() throws Exception {
    //        assertNotNull(controller);
    //    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String SERVICE_PATH_RATIO;
    private String SERVICE_PATH_METER;
    final static String filePathToUseRatio = "src/test/resources/ratios.csv";
    final static String filePathToUseMeter = "src/test/resources/meter.csv";

    static Set<Profile> readRatioData;

    /**
     * Initialize data before starting tests.
     */
    @Before
    public void beforeTest() {
        SERVICE_PATH_RATIO = "http://localhost:" + port + "/api/v0/ProfileService/profiles";
        SERVICE_PATH_METER = "http://localhost:" + port + "/api/v0/MeterService/meter";

        String content = this.restTemplate.postForObject(SERVICE_PATH_RATIO + "/load", filePathToUseRatio,
                String.class);
        getLogger().debug("Recieved content: " + content);

        content = this.restTemplate.postForObject(SERVICE_PATH_METER + "/load", filePathToUseMeter, String.class);
        getLogger().debug("Recieved content: " + content);
    }

    /**
     * Find all Profiles.
     */
    @Test
    public void getListOfAll() throws Exception {
        getLogger().info("Get list of all profiles and their data.");
        
        Profile[] profileData = this.restTemplate.getForObject(SERVICE_PATH_RATIO + "/", Profile[].class);
        List<Profile> asList = Arrays.asList(profileData);
        assertThat(asList).isNotNull();

        getLogger().info("Print data to console");
        asList.stream().forEach(p -> {
            getLogger().info("Profile Data " + p);
        });
    }

    /**
     * Find Profile by ID.
     */
    @Test
    public void findProfileById() {
        getLogger().info("Pind profile by ID");
        Profile profileData = this.restTemplate.getForObject(SERVICE_PATH_RATIO + "/A", Profile.class);
        assertThat(profileData).isNotNull();
        
        getLogger().info("Profile Data " + profileData);
    }

    /**
     * Find Profile by ID.
     * 
     * @throws URISyntaxException
     * @throws RestClientException
     */
    @Test
    public void creatProfile() throws RestClientException, URISyntaxException {
        getLogger().info("Create dummy data for one profile");
        final String tempId = "A" + RandomStringUtils.randomAlphabetic(1);
        Profile profileData = this.restTemplate.getForObject(SERVICE_PATH_RATIO + "/A", Profile.class);
        assertThat(profileData).isNotNull();
        profileData.setProfileId(tempId);

        String response = this.restTemplate.postForObject(SERVICE_PATH_RATIO + "/", profileData, String.class);
        assertThat(response).isNull();

        this.restTemplate.delete(new URI(SERVICE_PATH_RATIO + "/" + tempId));

        ResponseError responseError = this.restTemplate.getForObject(SERVICE_PATH_RATIO + "/" + tempId,
                ResponseError.class);
        assertTrue(responseError.getHttpStatus().equals(HttpStatus.NOT_FOUND));
    }
    
    /**
     * Find Meter by ID.
     * 
     * @throws URISyntaxException
     * @throws RestClientException
     */
    @Test
    public void createMeter() throws RestClientException, URISyntaxException {
        getLogger().info("Create dummy meter reading entry");
        Random ran = new Random();
        int x = ran.nextInt(6) + 10009;
        final String tempId = Integer.toString(x); 
        MeterReadingData profileData = this.restTemplate.getForObject(SERVICE_PATH_METER + "/10001",
                MeterReadingData.class);
        assertThat(profileData).isNotNull();
        profileData.setConnectionId(tempId);

        String response = this.restTemplate.postForObject(SERVICE_PATH_METER + "/", profileData, String.class);
        assertThat(response).isNull();

        this.restTemplate.delete(new URI(SERVICE_PATH_METER + "/" + tempId));

        ResponseError responseError = this.restTemplate.getForObject(SERVICE_PATH_METER + "/" + tempId,
                ResponseError.class);
        assertTrue(responseError.getHttpStatus().value() == (HttpStatus.NOT_FOUND.value()));
    }

    /**
     * Find Consumption by ID.
     * 
     * @throws URISyntaxException
     * @throws RestClientException
     */
    @Test
    public void checkConsumption() throws RestClientException, URISyntaxException {
        getLogger().info("Find consumption data for one connection ID and month");
        final String tempId = "10001";
        final String tempMonth = "Feb";
        MeterReadingData profileData = this.restTemplate.getForObject(SERVICE_PATH_METER + "/" + tempId,
                MeterReadingData.class);
        assertThat(profileData).isNotNull();

        MeterReadingData response = this.restTemplate
                .getForObject(SERVICE_PATH_METER + "/consumption/" + tempId + "/" + tempMonth, MeterReadingData.class);
        assertThat(response).isNotNull();

        response.getProfileList().stream().forEach(p -> {
            getLogger().info("Consumption for profile " + p.getProfileId());
            Data data = p.getData().stream().findFirst().get();
            getLogger().info("Month: " + data.getMonthName() + " consumption: " + data.getDataValue());
        });
    }
}
