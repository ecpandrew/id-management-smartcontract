/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.assettransfer;

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



    public String getId() { return id; }

    public String getContext() { return context; }

    public String getControlledBy() { return controlledBy; }

    public Identity(@JsonProperty("@context") final String context, @JsonProperty("id") final String id, @JsonProperty("controlledBy") final String controlledBy) {
        this.context = context;
        this.id = id;
        this.controlledBy = controlledBy;
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [context= " + context + " assetID= " + id + " controlledBy= "+ controlledBy + "]";
    }
}
