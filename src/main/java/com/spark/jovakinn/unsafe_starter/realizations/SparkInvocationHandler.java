package com.spark.jovakinn.unsafe_starter.realizations;

import com.spark.jovakinn.unsafe_starter.contracts.DataExtractor;
import com.spark.jovakinn.unsafe_starter.contracts.Finalizer;
import com.spark.jovakinn.unsafe_starter.contracts.FinalizerPostFinalizer;
import com.spark.jovakinn.unsafe_starter.contracts.SparkTransformation;
import lombok.Builder;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.context.ConfigurableApplicationContext;
import scala.Tuple2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Builder
public class SparkInvocationHandler implements InvocationHandler {

    private Class<?> modelClass;
    private String pathToData;
    private DataExtractor dataExtractor;
    private Map<Method, List<Tuple2<SparkTransformation, List<String>>>> transformationChain;
    private Map<Method, Finalizer> finalizerMap;
    private ConfigurableApplicationContext context;
    private FinalizerPostFinalizer finalizerPostFinalizer;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Dataset<Row> dataset = dataExtractor.load(pathToData, context);
        List<Tuple2<SparkTransformation, List<String>>> tuple2List = transformationChain.get(method);

        for (Tuple2<SparkTransformation, List<String>> tuple : tuple2List) {
            SparkTransformation transformation = tuple._1();
            List<String> columnNames = tuple._2();
            dataset = transformation.transform(dataset, columnNames, new OrderedBag<>(args));
        }

        Finalizer finalizer = finalizerMap.get(method);
        Object retVal = finalizer.doAction(dataset, modelClass);
        return finalizerPostFinalizer.postFinalize(retVal);
    }
}
