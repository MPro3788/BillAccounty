package com.bill.accounty.model;

import java.math.BigDecimal;
import java.util.List;

public class BilanzAntwort {

    private BigDecimal summeAktiva;
    private BigDecimal summePassiva;
    private boolean bilanziert;
    private List<BilanzPosition> aktiva;
    private List<BilanzPosition> passiva;

    public BigDecimal getSummeAktiva() {
        return summeAktiva;
    }

    public void setSummeAktiva(BigDecimal summeAktiva) {
        this.summeAktiva = summeAktiva;
    }

    public BigDecimal getSummePassiva() {
        return summePassiva;
    }

    public void setSummePassiva(BigDecimal summePassiva) {
        this.summePassiva = summePassiva;
    }

    public boolean isBilanziert() {
        return bilanziert;
    }

    public void setBilanziert(boolean bilanziert) {
        this.bilanziert = bilanziert;
    }

    public List<BilanzPosition> getAktiva() {
        return aktiva;
    }

    public void setAktiva(List<BilanzPosition> aktiva) {
        this.aktiva = aktiva;
    }

    public List<BilanzPosition> getPassiva() {
        return passiva;
    }

    public void setPassiva(List<BilanzPosition> passiva) {
        this.passiva = passiva;
    }
}

