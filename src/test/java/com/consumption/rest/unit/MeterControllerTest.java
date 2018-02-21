/**
 * 
 */
package com.consumption.rest.unit;

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

import com.consumption.rest.controllers.MeterController;
import com.consumption.rest.interfaces.IMeterService;
import com.consumption.rest.pojos.MeterReadingData;
import com.consumption.rest.utils.BaseClass;
import com.consumption.rest.utils.DataImportUtils;

@RunWith(SpringRunner.class)
@WebMvcTest(MeterController.class)
@AutoConfigureMockMvc
public class MeterControllerTest extends BaseClass {

    @Autowired
    private MockMvc mockMvc;

    static String filePathToUse = "src/main/resources/meter.csv";

    @MockBean
    private IMeterService meterService;
    private static String SERVICE_PATH = "/api/v0/MeterService/meter";
    static Set<MeterReadingData> meterReadingData;

    @BeforeClass
    public static void prepareData() {
        meterReadingData = DataImportUtils.readMeterData(filePathToUse);
    }

    @Test
    public void findAll() throws Exception {
        assertThat(meterReadingData).isNotEmpty().isNotNull();
        meterReadingData.forEach(p -> {
            given(this.meterService.findAll()).willReturn(new ArrayList<>(meterReadingData));
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
        assertThat(meterReadingData).isNotEmpty().isNotNull();
        meterReadingData.forEach(p -> {
            given(this.meterService.findById(p.getConnectionId()))
                    .willReturn(p);
            try {
                this.mockMvc.perform(get(SERVICE_PATH + "/" + p.getConnectionId())).andDo(print())
                        .andExpect(status().is2xxSuccessful());
            } catch (Exception e) {
                getLogger().error("Failed to test path");
                fail("Failed to test path");
            }
        });
    }

    @Test
    public void findByIdDFail() throws Exception {
        assertThat(meterReadingData).isNotEmpty().isNotNull();
        MeterReadingData meterReading = meterReadingData.stream().findAny().get();
        
        given(this.meterService.findById(meterReading.getConnectionId() + "1"))
                .willReturn(meterReading);
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
        assertThat(meterReadingData).isNotEmpty().isNotNull();
        meterReadingData.forEach(p -> {
            given(this.meterService.findById(p.getConnectionId()))
            .willReturn(p);
            try {
                this.mockMvc.perform(delete(SERVICE_PATH + "/" + p.getConnectionId()))
                .andDo(print())
                .andExpect(status().is5xxServerError());
            } catch (Exception e) {
                getLogger().error("Failed to test path");
                fail("Failed to test path");
            }
            
        });
    }
}
