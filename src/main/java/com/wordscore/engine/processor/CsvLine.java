package com.wordscore.engine.processor;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CsvLine {

    @CsvBindByPosition(position = 0)
    private BigDecimal score;

}
