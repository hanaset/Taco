package com.hanaset.taco.api.binance.domain;

/**
 * Order execution type.
 */
public enum ExecutionType {
  NEW,
  CANCELED,
  REPLACED,
  REJECTED,
  TRADE,
  EXPIRED
}