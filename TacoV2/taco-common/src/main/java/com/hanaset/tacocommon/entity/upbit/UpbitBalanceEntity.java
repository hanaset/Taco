package com.hanaset.tacocommon.entity.upbit;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@Table(name = "TB_BALANCE")
public class UpbitBalanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal amount;

    @Column(name = "reg_dtime")
    private Timestamp regDtime;
}
