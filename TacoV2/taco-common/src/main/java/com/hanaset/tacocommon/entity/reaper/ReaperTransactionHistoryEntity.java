package com.hanaset.tacocommon.entity.reaper;

import com.hanaset.tacocommon.common.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Generated;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Builder
@Table(name = "TB_REAPER_TRANSACTION_HISTORY")
public class ReaperTransactionHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String exchange;

    private String asset;

    @Column(name = "base_currency")
    private String baseCurrency;

    private String side;

    private BigDecimal price;

    private BigDecimal volume;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    private BigDecimal fee;

    @Column(name = "reg_dtime", updatable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime regDateTime;

    @Column(name = "upd_dtime")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updDateTime;
}
