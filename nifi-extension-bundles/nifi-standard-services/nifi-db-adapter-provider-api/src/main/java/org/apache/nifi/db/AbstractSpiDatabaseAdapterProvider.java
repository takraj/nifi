/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.db;

import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.AllowableValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.controller.ConfigurationContext;

/**
 * Abstract implementation of a DatabaseAdapterProvider, that discovers available adapters through Java SPI.
 */
public abstract class AbstractSpiDatabaseAdapterProvider extends AbstractControllerService implements DatabaseAdapterProvider {

    private final static Map<String, DatabaseAdapter> dbAdapters = new HashMap<>();

    private static PropertyDescriptor createDbTypePropertyDescriptor() {
        // Load the DatabaseAdapters
        ArrayList<AllowableValue> dbAdapterValues = new ArrayList<>();
        ServiceLoader<DatabaseAdapter> dbAdapterLoader = ServiceLoader.load(DatabaseAdapter.class);
        dbAdapterLoader.forEach(it -> {
            dbAdapters.put(it.getName(), it);
            dbAdapterValues.add(new AllowableValue(it.getName(), it.getName(), it.getDescription()));
        });

        return new PropertyDescriptor.Builder()
                .name("db-type")
                .displayName("Database Type")
                .description("The type/flavor of database, used for generating database-specific code.")
                .allowableValues(dbAdapterValues.toArray(new AllowableValue[0]))
                .required(true)
                .build();
    }

    private final PropertyDescriptor DB_TYPE = createDbTypePropertyDescriptor();
    private DatabaseAdapter selectedAdapter;

    @Override
    public List<PropertyDescriptor> getSupportedPropertyDescriptors() {
        return singletonList(DB_TYPE);
    }

    @OnEnabled
    public void onEnabled(final ConfigurationContext context) {
        selectedAdapter = dbAdapters.get(context.getProperty(DB_TYPE).getValue());
    }

    protected DatabaseAdapter getSelectedAdapter() {
        return selectedAdapter;
    }
}
