package com.hanaset.taco.api.binance.domain.account;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents an executed trade.
 */
public class Trade {

    /**
     * Trade id.
     */
    private Long id;

    /**
     * Price.
     */
    private String price;

    /**
     * Quantity.
     */
    private String qty;

    /**
     * Commission.
     */
    private String commission;

    /**
     * Asset on which commission is taken
     */
    private String commissionAsset;

    /**
     * Trade execution time.
     */
    private long time;

    private boolean isBuyer;

    private boolean isMaker;

    private boolean isBestMatch;

    private Long orderId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getCommissionAsset() {
        return commissionAsset;
    }

    public void setCommissionAsset(String commissionAsset) {
        this.commissionAsset = commissionAsset;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isBuyer() {
        return isBuyer;
    }

    public void setBuyer(boolean buyer) {
        this.isBuyer = buyer;
    }

    public boolean isMaker() {
        return isMaker;
    }

    public void setMaker(boolean maker) {
        this.isMaker = maker;
    }

    public boolean isBestMatch() {
        return isBestMatch;
    }

    public void setBestMatch(boolean bestMatch) {
        this.isBestMatch = bestMatch;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("price", price)
                .append("qty", qty)
                .append("commission", commission)
                .append("commissionAsset", commissionAsset)
                .append("time", time)
                .append("buyer", isBuyer)
                .append("maker", isMaker)
                .append("bestMatch", isBestMatch)
                .append("orderId", orderId)
                .toString();
    }
}
