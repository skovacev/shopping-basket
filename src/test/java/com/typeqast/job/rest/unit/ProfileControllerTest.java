/**
 * 
 */
package com.typeqast.job.rest.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.typeqast.job.rest.controllers.ProfileController;
import com.typeqast.job.rest.pojos.Profile;
import com.typeqast.job.rest.services.ProfileService;
import com.typeqast.job.rest.utils.BaseClass;
import com.typeqast.job.rest.utils.DataImportUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc
public class ProfileControllerTest extends BaseClass {

    @Autowired
    private MockMvc mockMvc;
    @Autowired

    static String filePathToUse = "src/main/resources/ratios.csv";

    @MockBean
    private ProfileService profileService;
    private static String SERVICE_PATH = "/api/v0/ProfileService/profiles";
    static Set<Profile> readRatioData;

    @BeforeClass
    public static void prepareData() {
        readRatioData = DataImportUtils.readRatioData(filePathToUse);
    }

    @Test
    public void findAll() throws Exception {
        assertThat(readRatioData).isNotEmpty().isNotNull();
        readRatioData.forEach(p -> {
            given(this.profileService.findAll()).willReturn(new ArrayList<>(readRatioData));
            try {
                this.mockMvc.perform(get(SERVICE_PATH + "/")).andDo(print())
                        .andExpect(status().is2xxSuccessful());
            } catch (Exception e) {
                getLogger().error("Failed to test path");
                fail("Failed to test path");
            }
        });
    }

    @Test
    public void findById() throws Exception {
        assertThat(readRatioData).isNotEmpty().isNotNull();
        readRatioData.forEach(p -> {
            given(this.profileService.findById(p.getProfileId()))
                    .willReturn(new Profile(p.getProfileId(), p.getData()));
            try {
                this.mockMvc.perform(get(SERVICE_PATH + "/" + p.getProfileId())).andDo(print())
                        .andExpect(status().is2xxSuccessful());
            } catch (Exception e) {
                getLogger().error("Failed to test path");
                fail("Failed to test path");
            }
        });
    }

    @Test
    public void findByIdDFail() throws Exception {
        assertThat(readRatioData).isNotEmpty().isNotNull();
        Profile profile = readRatioData.stream().findAny().get();
    
        given(this.profileService.findById(profile.getProfileId()))
                .willReturn(new Profile(profile.getProfileId(), profile.getData()));
        try {
            this.mockMvc.perform(get(SERVICE_PATH + "/D")).andDo(print())
                    .andExpect(status().is4xxClientError());
        } catch (Exception e) {
            getLogger().error("Failed to test path");
            fail("Failed to test path");
        }
    }

   
    @Test
    public void deleteProfile() throws Exception {
        assertThat(readRatioData).isNotEmpty().isNotNull();
        readRatioData.forEach(p -> {
            given(this.profileService.findById(p.getProfileId()))
            .willReturn(new Profile(p.getProfileId(), p.getData()));
            try {
                this.mockMvc.perform(delete(SERVICE_PATH + "/" + p.getProfileId()))
                .andDo(print())
                .andExpect(status().is5xxServerError());
            } catch (Exception e) {
                getLogger().error("Failed to test path");
                fail("Failed to test path");
            }
        });
    }
}
