/**
 * 
 */
package com.typeqast.job.rest.pojos;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author Sinisa Kovčević 29-01-2018. -- Initial implementation
 */
@JsonPropertyOrder({
        "connectionID",
        "profileList"
})
@ApiObject(name="MeterReadingData", description="Meter Reading data to keep connection ID and meter reading")
public class MeterReadingData {

    @JsonProperty("connectionId")
    @NotNull
    @ApiObjectField(name="connectionId", description = "Meter connection ID", required= true)
    private String connectionId;
    
    @JsonProperty("profileList")
    @ApiObjectField(name="profileList", description = "Set of Profiles", required= true)
    private Set<Profile> profileList = new HashSet<Profile>();;

    /**
     * Default Constructor
     */
    public MeterReadingData() {
    }
    
    /**
     * Default Constructor
     */
    public MeterReadingData(String connectionId, Set<Profile> profileList) {
        this.connectionId = connectionId;
        setProfileList(profileList);
    }

    
    /**
     * Get connection ID.
     *
     * @return the connection ID
     */
    public final String getConnectionId() {
        return connectionId;
    }

    
    /**
     * Set connection ID.
     *
     * @param connectionId the connectionID to set
     */
    public final void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    
    /**
     * Get profileList.
     *
     * @return the profileList
     */
    public final Set<Profile> getProfileList() {
        return profileList;
    }

    
    /**
     * Set profileList.
     *
     * @param profileList the profileList to set
     */
    public final void setProfileList(Set<Profile> profileList) {
        this.profileList = profileList;
    }
    
    /**
     * addProfileList
     * @param profile
     */
    public void addProfileList(Profile profile) {
        if (profile != null) {
            this.profileList.add(profile);
        }
    }
    
    
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MeterReadingData)) {
            return false;
        }

        MeterReadingData meterReadingData = (MeterReadingData) obj;
        return this.connectionId.equals(meterReadingData.getConnectionId());
    }

    @Override
    public int hashCode() {
        int hashno = 7;
        hashno = 13 * hashno + (connectionId == null ? 0 : connectionId.hashCode());
        return hashno;
    }
}
