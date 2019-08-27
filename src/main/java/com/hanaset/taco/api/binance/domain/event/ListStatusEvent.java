package com.hanaset.taco.api.binance.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.hanaset.taco.api.binance.domain.account.Order;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ListStatusEvent {

    /**
     * {
     *   "e": "listStatus",                //Event Type
     *   "E": 1564035303637,               //Event Time
     *   "s": "ETHBTC",                    //Symbol
     *   "g": 2,                           //OrderListId
     *   "c": "OCO",                       //Contingency Type
     *   "l": "EXEC_STARTED",              //List Status Type
     *   "L": "EXECUTING",                 //List Order Status
     *   "r": "NONE",                      //List Reject Reason
     *   "C": "F4QN4G8DlFATFlIUQ0cjdD",    //List Client Order ID
     *   "T": 1564035303625,               //Transaction Time
     *   "O": [                            //An array of objects
     *     {
     *       "s": "ETHBTC",                //Symbol
     *       "i": 17,                      // orderId
     *       "c": "AJYsMjErWJesZvqlJCTUgL" //ClientOrderId
     *     },
     *     {
     *       "s": "ETHBTC",
     *       "i": 18,
     *       "c": "bfYPSQdLoqAJeNrOr9adzq"
     *     }
     *   ]
     * }
     */

    @JsonProperty("e")
    private String eventType;

    @JsonProperty("E")
    private long eventTime;

    @JsonProperty("s")
    private String symbol;

    @JsonProperty("g")
    private long orderListId;

    @JsonProperty("c")
    private String contingencyType;

    @JsonProperty("l")
    private String listStatusType;

    @JsonProperty("L")
    private String listOrderStatus;

    @JsonProperty("r")
    private String listRejectReason;

    @JsonProperty("C")
    private String listClientOrderId;

    @JsonProperty("T")
    private long transactionTime;

    @JsonProperty("O")
    @JsonDeserialize(contentUsing = OrderDeserializer.class)
    private List<Order> orders;
}
