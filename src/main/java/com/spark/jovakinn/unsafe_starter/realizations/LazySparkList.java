package com.spark.jovakinn.unsafe_starter.realizations;

import lombok.Data;
import lombok.experimental.Delegate;
import java.util.*;

@Data
public class LazySparkList implements List {

    @Delegate
    private List content;

    private long ownerId;

    private Class<?> modelClass;

    private String foreignKeyName;

    private String pathToSource;

    public boolean initialized() {
        return content != null && !content.isEmpty();
    }
}
