package com.spark.jovakinn.unsafe_starter.realizations;

import com.spark.jovakinn.unsafe_starter.contracts.Finalizer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.stereotype.Component;

@Component("count")
public class CountFinalizer implements Finalizer {
    @Override
    public Object doAction(Dataset<Row> dataset, Class<?> model) {
        return dataset.count();
    }
}
