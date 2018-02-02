/**
 * 
 */
package com.typeqast.job.rest.controllers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiResponseObject;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVerb;
import org.jsondoc.core.pojo.ApiVisibility;
import org.jsondoc.spring.boot.starter.EnableJSONDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.typeqast.job.rest.exceptions.ResponseError;
import com.typeqast.job.rest.interfaces.IMeterService;
import com.typeqast.job.rest.pojos.Data;
import com.typeqast.job.rest.pojos.MeterReadingData;
import com.typeqast.job.rest.utils.BaseClass;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */

@Api(name = "Shopping Basket Meter Service",
        description = "Methods for managing meter",
        group = "Meters",
        visibility = ApiVisibility.PUBLIC,
        stage = ApiStage.RC)
@ApiVersion(since = "1", until = "2")
@ApiAuthNone
@SuppressWarnings({ "unchecked", "rawtypes" })
@EnableJSONDoc
@RestController
@RequestMapping("/api/v0/MeterService/meter")
public class MeterController extends BaseClass {

    @Autowired
    private IMeterService meterService;
    private static String SERVICE_PATH = "/api/v0/MeterService/meter";

    @ApiMethod(path = "/",
            verb = ApiVerb.GET,
            description = "Get all connection data and meter reading data from database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get all connection data and meter reading data.",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ApiResponseObject ResponseEntity<List<MeterReadingData>> listAllMeters() {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/");
        
        getLogger().info("Fetching all meter reading data");
        List<MeterReadingData> dataList = meterService.findAll();
        if (dataList.isEmpty()) {
            getLogger().error("Requested data not found.");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<MeterReadingData>>(dataList, HttpStatus.OK);
    }

    @ApiMethod(path = "/{connectionId}",
            verb = ApiVerb.GET,
            description = "Get single connection and its all meter reading data from database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get single connection and its all meter reading data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{connectionId}", method = RequestMethod.GET)
    public ResponseEntity<MeterReadingData> getConnectionData(@PathVariable("connectionId") String connectionId) {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/{connectionId}");
       
        getLogger().info("Fetching connection with id: " + connectionId);
        MeterReadingData meterReading = meterService.findById(connectionId);
        if (meterReading == null) {
            getLogger().error("Connection with id " + connectionId + " not found.");
            return new ResponseEntity(new ResponseError("Connection with id " + connectionId + " not found", 
                    HttpStatus.NOT_FOUND),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<MeterReadingData>(meterReading, HttpStatus.OK);
    }

    @ApiMethod(path = "/{connectionId}/{profileId}/{month}",
            verb = ApiVerb.GET,
            description = "Get single connection with profile and its all meter reading data for single month from database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get single connection with profile and its all meter reading data for single month",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{connectionId}/{profileId}/{month}", method = RequestMethod.GET)
    public ResponseEntity<?> getConnectionData(@PathVariable("connectionId") String connectionId,
            @PathVariable("profileId") String profileId, @PathVariable("month") String month) {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/{connectionId}/{profileId}/{month}");
        
        getLogger().info("Fetching connection with id: " + connectionId + " profile: " + profileId + " and month: " + month);
        Data data = meterService.findByProfileMonth(connectionId, profileId, month);
        if (data == null) {
            getLogger().error("Requested data not found.");
            return new ResponseEntity(new ResponseError("Data not found", HttpStatus.NOT_FOUND),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Data>(data, HttpStatus.OK);
    }
    
    @ApiMethod(path = "/{connectionId}/{month}",
            verb = ApiVerb.GET,
            description = "Get single connection and its meter reading data for single month and all profiles from database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get single connection and its meter reading data for single month and all profiles",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{connectionId}/{month}", method = RequestMethod.GET)
    public ResponseEntity<?> getConnectionDataPerMonth(@PathVariable("connectionId") String connectionId, @PathVariable("month") String month) {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/{connectionId}/{month}");
        
        getLogger().info("Fetching connection with id: " + connectionId + " and month: " + month);
        MeterReadingData meterData = meterService.findByMonth(connectionId, month);
        if (meterData == null) {
            getLogger().error("Requested data not found.");
            return new ResponseEntity(new ResponseError("Data not found", HttpStatus.NOT_FOUND),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<MeterReadingData>(meterData, HttpStatus.OK);
    }

    @ApiMethod(path = "/consumption/{connectionId}/{month}",
            verb = ApiVerb.GET,
            description = "Get Consumption data for single month.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get Consumption data for single month.",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/consumption/{connectionId}/{month}", method = RequestMethod.GET)
    public ResponseEntity<?> getConsumption(@PathVariable("connectionId") String connectionId,
            @PathVariable("month") String month) {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/{connectionId}/{month}");
        
        getLogger().info("Fetching connection with id: " + connectionId + " and month: " + month);
        try {
            MeterReadingData metaReadingData = meterService.findConsumptionByMonth(connectionId, month);
            getLogger().info("Requested data found.");
            return new ResponseEntity<MeterReadingData>(metaReadingData, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Requested data not found.", e.getMessage());
            return new ResponseEntity(new ResponseError(e.getMessage(), HttpStatus.NOT_FOUND),
                    HttpStatus.NOT_FOUND);
        }
    }

    @ApiMethod(path = "/",
            verb = ApiVerb.POST,
            description = "Create single meter reading data in database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            consumes = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Create single meter reading data",
            responsestatuscode = "201 - CREATED")
    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = "application/json",
            consumes = "application/json")
    public ResponseEntity<String> createConnection(@RequestBody MeterReadingData meterReadingData,
            UriComponentsBuilder ucBuilder) {
        getLogger().debug("REST " + RequestMethod.POST + " call to: " + SERVICE_PATH + "/");
        
        if (meterReadingData != null && StringUtils.isEmpty(meterReadingData.getConnectionId())) {
            getLogger().error("Provided data not valid.");
            return new ResponseEntity(new ResponseError("Unable to create. Connection data invalid.", HttpStatus.BAD_REQUEST), 
                    HttpStatus.BAD_REQUEST);
        }
        getLogger().info("Creating connection with id: " + meterReadingData.getConnectionId());
        if (meterService.isDataExist(meterReadingData)) {
            getLogger().error("Unable to create. Connection with connection id " + meterReadingData.getConnectionId() + " already exist");
            return new ResponseEntity(new ResponseError("Unable to create. A MeterReadingData with name " +
                    meterReadingData.getConnectionId() + " already exist."), HttpStatus.BAD_REQUEST);
        }

        if (meterService.saveData(meterReadingData)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path(SERVICE_PATH + "/{id}")
                    .buildAndExpand(meterReadingData.getConnectionId()).toUri());
            getLogger().info("Requested data saved.");
            return new ResponseEntity<String>(headers, HttpStatus.CREATED);
        } else {
            getLogger().error("Unable to create. Connection with connection id " + meterReadingData.getConnectionId() + " is not valid");
            return new ResponseEntity(new ResponseError("Unable to create. A Connection with id " +
                    meterReadingData.getConnectionId() + " is not valid."), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiMethod(path = "/profiles/{connectionId}",
            verb = ApiVerb.PUT,
            description = "Update single meter data in database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            consumes = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Update single meter data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{connectionId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateConnection(@PathVariable("connectionId") String connectionId,
            @RequestBody MeterReadingData meterReading, UriComponentsBuilder ucBuilder) {
        getLogger().debug("REST " + RequestMethod.PUT + " call to: " + SERVICE_PATH + "/{connectionId}");
        
        getLogger().info("Updating connection with id: " + connectionId);
        MeterReadingData currentMeterReading = meterService.findById(connectionId);

        if (currentMeterReading == null) {
            getLogger().error("Unable to update. Connection with connection id " + connectionId + " not found");
            return new ResponseEntity(
                    new ResponseError("Unable to update. Connection with id " + connectionId + " not found.", 
                            HttpStatus.NOT_FOUND),
                            HttpStatus.NOT_FOUND);
        }

        if (meterService.updateData(meterReading)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path(SERVICE_PATH + "/{id}")
                    .buildAndExpand(meterReading.getConnectionId()).toUri());
            getLogger().info("Requested data updated.");
            return new ResponseEntity<String>(headers, HttpStatus.OK);
        } else {
            getLogger().error("Unable to update. Connection with connection id " + meterReading.getConnectionId() + " is not valid");
            return new ResponseEntity(new ResponseError("Unable to update. A Connection with id " +
                    meterReading.getConnectionId() + " is not valid."), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiMethod(path = "/{connectionId}",
            verb = ApiVerb.DELETE,
            description = "Delete single meter reading data from database.",
            summary = "Delete single meter reading data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{connectionId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteConnection(@PathVariable("connectionId") String connectionId) {
        getLogger().debug("REST " + RequestMethod.DELETE + " call to: " + SERVICE_PATH + "/{connectionId}");
        
        getLogger().info("Fetching & Deleting connection with id " + connectionId);
        MeterReadingData currentMediaReading = meterService.findById(connectionId);
        
        if (currentMediaReading == null) {
            getLogger().error("Unable to delete. connection with id " + connectionId + " not found.");
            return new ResponseEntity(
                    new ResponseError("Unable to delete. connection with id " + connectionId + " not found.", 
                            HttpStatus.NOT_FOUND),
                            HttpStatus.NOT_FOUND);
        }
        if (meterService.deleteDataById(connectionId)) {
            getLogger().info("Requested data deleted.");
            return new ResponseEntity<MeterReadingData>(HttpStatus.NO_CONTENT);
        } else {
            getLogger().error("Unable to delete. Connection with connection id " + connectionId + " not deleted");
            return new ResponseEntity(new ResponseError("Unable to delete. Connection with connection id " 
                    + connectionId + " not deleted", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiMethod(path = "/",
            verb = ApiVerb.DELETE,
            description = "Delete all meter reading data from database.",
            summary = "Delete all meter reading data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<MeterReadingData> deleteAllConnections() {
        getLogger().debug("REST " + RequestMethod.DELETE + " call to: " + SERVICE_PATH + "/");
        
        getLogger().info("Deleting All Meter Reading Data");
        if (meterService.deleteAllDatas()) {
            getLogger().info("Requested data deleted.");
            return new ResponseEntity<MeterReadingData>(HttpStatus.NO_CONTENT);
        } else {
            getLogger().error("Unable to delete all entries");
            return new ResponseEntity(new ResponseError("Unable to delete all entries", 
                    HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @ApiMethod(path = "/load",
            verb = ApiVerb.POST,
            description = "Load data from location specified in body.",
            summary = "Load data from location specified in body.",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/load", method = RequestMethod.POST)
    public ResponseEntity<String> loadData(@RequestBody String filePath,
            UriComponentsBuilder ucBuilder) {
        getLogger().debug("REST " + RequestMethod.POST + " call to: " + SERVICE_PATH + "/load");
        
        getLogger().info("Load data from path");
        meterService.loadData(filePath);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
}
