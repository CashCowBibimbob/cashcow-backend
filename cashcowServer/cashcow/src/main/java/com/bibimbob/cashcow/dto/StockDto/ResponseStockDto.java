package com.bibimbob.cashcow.dto.StockDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ResponseStockDto {
    @ApiModelProperty(value = "주식 코드")
    @JsonProperty("stock_code")
    private String stockCode;
}
