package com.world_quant.test;

import lombok.Data;
/**
 * Self-describing
 */
@Data
public class CopyingArgs {

    private final String   fromDb;
    private final String[] fromTables;
    private final String   toDb;
    private final String   toSchema;

}
