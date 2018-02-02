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
import com.typeqast.job.rest.interfaces.IProfileService;
import com.typeqast.job.rest.pojos.Data;
import com.typeqast.job.rest.pojos.Profile;
import com.typeqast.job.rest.utils.BaseClass;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */

@Api(name = "Shopping Basket Profile service",
        description = "Methods for managing profiles",
        group = "Profiles",
        visibility = ApiVisibility.PUBLIC,
        stage = ApiStage.RC)
@ApiVersion(since = "1", until = "2")
@ApiAuthNone
@SuppressWarnings({ "unchecked", "rawtypes" })
@EnableJSONDoc
@RestController
@RequestMapping("/api/v0/ProfileService/profiles")
public class ProfileController extends BaseClass {

    @Autowired
    private IProfileService profileService;
    private static String SERVICE_PATH = "/api/v0/ProfileService/profiles";

    @ApiMethod(path = "/",
            verb = ApiVerb.GET,
            description = "Get all profiles and ratio data from database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get profiles and ratio data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public @ApiResponseObject ResponseEntity<List<Profile>> getAllProfileData() {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/");
        
        getLogger().info("Fetching all profile and ratio data");
        List<Profile> dataList = profileService.findAll();
        
        if (dataList.isEmpty()) {
            getLogger().error("Requested data not found.");
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Profile>>(dataList, HttpStatus.OK);
    }

    @ApiMethod(path = "/{profileId}",
            verb = ApiVerb.GET,
            description = "Get single profile and its all ratio data from database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get single profile and all ratio data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{profileId}", method = RequestMethod.GET)
    public ResponseEntity<Profile> getProfileData(@PathVariable("profileId") String profileId) {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/{profileId}");
        
        getLogger().info("Fetching profile with id: " + profileId);
        Profile profile = profileService.findById(profileId);
        
        if (profile == null) {
            getLogger().error("Profile with id " + profileId + " not found.");
            return new ResponseEntity(new ResponseError("Profile with id " + profileId + " not found", 
                    HttpStatus.NOT_FOUND),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Profile>(profile, HttpStatus.OK);
    }

    @ApiMethod(path = "/{profileId}/{month}",
            verb = ApiVerb.GET,
            description = "Get single profile and month ratio data from database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Get single profile and ratio data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{profileId}/{month}", method = RequestMethod.GET)
    public ResponseEntity<?> getProfileData(@PathVariable("profileId") String profileId,
            @PathVariable("month") String month) {
        getLogger().debug("REST " + RequestMethod.GET + " call to: " + SERVICE_PATH + "/{profileId}/{month}");
        
        getLogger().info("Fetching profile with id: " + profileId + " and month: " + month);
        
        try {
            Data data  = profileService.findByMonth(profileId, month);
            getLogger().info("Requested data found.");
            return new ResponseEntity<Data>(data, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Requested data not found.", e.getMessage());
            return new ResponseEntity(new ResponseError(e.getMessage(), HttpStatus.NOT_FOUND),
                    HttpStatus.NOT_FOUND);
        }
    }

    @ApiMethod(path = "/{profileId}",
            verb = ApiVerb.POST,
            description = "Create single profile and its yearly ratio data to database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE },
            summary = "Create single profile and its yearly ratio data",
            responsestatuscode = "201 - CREATED")
    @RequestMapping(value = "/",
            method = RequestMethod.POST,
            produces = "application/json",
            consumes = "application/json")
    public ResponseEntity<String> createProfile(@RequestBody Profile profile, UriComponentsBuilder ucBuilder) {
        getLogger().debug("REST " + RequestMethod.POST + " call to: " + SERVICE_PATH + "/{profileId}");
        
        if (profile != null && StringUtils.isEmpty(profile.getProfileId())) {
            getLogger().error("Provided data not valid.");
            return new ResponseEntity(new ResponseError("Unable to create. Profile data invalid.", HttpStatus.BAD_REQUEST), 
                    HttpStatus.BAD_REQUEST);
        }
        getLogger().info("Creating profile with id: " + profile.getProfileId());
        if (profileService.isDataExist(profile)) {
            getLogger().error("Unable to create. Profile with id " + profile.getProfileId() + " already exist");
            return new ResponseEntity(new ResponseError("Unable to create. Profile with id " +
                    profile.getProfileId() + " already exist."), HttpStatus.BAD_REQUEST);
        }
        
        if (profileService.saveData(profile)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path(SERVICE_PATH + "/{id}")
                    .buildAndExpand(profile.getProfileId()).toUri());
            getLogger().info("Requested data saved.");
            return new ResponseEntity<String>(headers, HttpStatus.CREATED);
        } else {
            getLogger().error("Unable to create. Profile with id " + profile.getProfileId() + " is not valid");
            return new ResponseEntity(new ResponseError("Unable to create. Profile with id " +
                    profile.getProfileId() + " is not valid."), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiMethod(path = "/{profileId}",
            verb = ApiVerb.PUT,
            description = "Update single profile and its yearly ratio data in database.",
            produces = {MediaType.APPLICATION_JSON_VALUE },
            consumes = {MediaType.APPLICATION_JSON_VALUE },
            summary = "Update single profile and its yearly ratio data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{profileId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateProfile(@PathVariable("profileId") String profileId, @RequestBody Profile profile, UriComponentsBuilder ucBuilder) {
        getLogger().debug("REST " + RequestMethod.PUT + " call to: " + SERVICE_PATH + "/{profileId}");

        getLogger().info("Updating profile with id: " + profileId);
        Profile currentProfile = profileService.findById(profileId);

        if (currentProfile == null) {
            getLogger().error("Unable to update. Profile with id " + profileId + " not found");
            return new ResponseEntity(
                    new ResponseError("Unable to update. Profile with id " + profileId + " not found.", 
                            HttpStatus.NOT_FOUND),
                            HttpStatus.NOT_FOUND);
        }
        
        if (profileService.updateData(profile)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path(SERVICE_PATH + "/{id}")
                    .buildAndExpand(profile.getProfileId()).toUri());
            getLogger().info("Requested data updated.");
            return new ResponseEntity<Profile>(headers, HttpStatus.OK);
        } else {
            getLogger().error("Unable to update. Profile with id " + profile.getProfileId() + " is not valid");
            return new ResponseEntity(new ResponseError("Unable to update. Profile with id " +
                    profile.getProfileId() + " is not valid."), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiMethod(path = "/{profileId}",
            verb = ApiVerb.DELETE,
            description = "Delete single profile and its yearly ratio data from database.",
            summary = "Delete single profile and its yearly ratio data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/{profileId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteProfile(@PathVariable("profileId") String profileId) {
        getLogger().debug("REST " + RequestMethod.DELETE + " call to: " + SERVICE_PATH + "/{profileId}");
        
        getLogger().info("Fetching & Deleting profile with id " + profileId);
        Profile currentProfile = profileService.findById(profileId);
        
        if (currentProfile == null) {
            getLogger().error("Unable to delete. Profile with id " + profileId + " not found.");
            return new ResponseEntity(
                    new ResponseError("Unable to delete. Profile with id " + profileId + " not found.", 
                            HttpStatus.NOT_FOUND),
                            HttpStatus.NOT_FOUND);
        }
        if (profileService.deleteDataById(profileId)) {
            getLogger().info("Requested data deleted.");
            return new ResponseEntity<Profile>(HttpStatus.NO_CONTENT);
        } else {
            getLogger().error("Unable to delete. Profile with id " + profileId + " not deleted");
            return new ResponseEntity(new ResponseError("Unable to delete. Profile with id " 
                    + profileId + " not deleted", HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiMethod(path = "/",
            verb = ApiVerb.DELETE,
            description = "Delete all profiles and their yearly ratio data from database.",
            summary = "Delete all profiles and their yearly ratio data",
            responsestatuscode = "200 - OK")
    @RequestMapping(value = "/", method = RequestMethod.DELETE)
    public ResponseEntity<Profile> deleteAllProfiles() {
        getLogger().debug("REST " + RequestMethod.DELETE + " call to: " + SERVICE_PATH + "/");

        getLogger().info("Deleting All profile and ratio Data");
        if (profileService.deleteAllDatas()) {
            getLogger().info("Requested data deleted.");
            return new ResponseEntity<Profile>(HttpStatus.NO_CONTENT);
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
        profileService.loadData(filePath);
        return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
    }
}
