package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

public class ECWrapper {

    private String kty;
    private String use;
    private String crv;
    private String kid;
    private String x;
    private String y;

    public ECWrapper(String kty, String use, String crv, String kid, String x, String y) {
        this.kty = kty;
        this.use = use;
        this.crv = crv;
        this.kid = kid;
        this.x = x;
        this.y = y;
    }

    public String getKty() {
        return kty;
    }

    public void setKty(String kty) {
        this.kty = kty;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public String getCrv() {
        return crv;
    }

    public void setCrv(String crv) {
        this.crv = crv;
    }

    public String getKid() {
        return kid;
    }

    public void setKid(String kid) {
        this.kid = kid;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "ECWrapper{" +
                "kty='" + kty + '\'' +
                ", use='" + use + '\'' +
                ", crv='" + crv + '\'' +
                ", kid='" + kid + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ECWrapper ecWrapper = (ECWrapper) o;
        return Objects.equals(kty, ecWrapper.kty) && Objects.equals(use, ecWrapper.use) && Objects.equals(crv, ecWrapper.crv) && Objects.equals(kid, ecWrapper.kid) && Objects.equals(x, ecWrapper.x) && Objects.equals(y, ecWrapper.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kty, use, crv, kid, x, y);
    }
}
