/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Identity {

    @Property()
    private final String context;

    @Property()
    private final String id;

    @Property()
    private final String controlledBy;

    @Property()
    private final Map<String, String> publicKeyJwk;

    @Property()
    private final Map<String, String> subjectInfo;

    @Property()
    private final String issuedAt;

    @Property()
    private final String validTo;

    @Property()
    private final String status;

    public String getId() {
        return id;
    }

    public String getContext() {
        return context;
    }

    public String getControlledBy() {
        return controlledBy;
    }

    public Map<String, String> getPublicKeyJwk() {
        return publicKeyJwk;
    }

    public String getIssuedAt() {
        return issuedAt;
    }


    public String getValidTo() {
        return validTo;
    }


    public String getStatus() {
        return status;
    }

    public Map<String, String> getSubjectInfo() {
        return subjectInfo;
    }

    public Identity(@JsonProperty("context") final String context,
                    @JsonProperty("id") final String id,
                    @JsonProperty("controlledBy") final String controlledBy,
                    @JsonProperty("publicKeyJwk") final Map<String, String> publicKeyJwk,
                    @JsonProperty("subjectInfo") final Map<String, String> subjectInfo,
                    @JsonProperty("status") final String status,
                    @JsonProperty("issuedAt") final String issuedAt,
                    @JsonProperty("validTo") final String validTo) {
        this.context = context;
        this.id = id;
        this.controlledBy = controlledBy;
        this.publicKeyJwk = publicKeyJwk;
        this.subjectInfo = subjectInfo;
        this.status = status;
        this.issuedAt = issuedAt;
        this.validTo = validTo;
    }








    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Identity other = (Identity) obj;

        return Objects.deepEquals(
                new String[] {getContext(),getId(),getControlledBy()},
                new String[] {other.getContext(),other.getId(),other.getControlledBy()});

    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }


}
