package com.hanaset.tacocommon.entity.mld;

import com.hanaset.tacocommon.common.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Data
@Builder
@Table(name = "TB_MLD_BALANCE_HISTORY")
public class MldBalanceHistoryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "asset")
    private String asset;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "reg_dtime", updatable = false)
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime regDateTime;

    @Column(name = "upd_dtime")
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime updDateTime;

}
