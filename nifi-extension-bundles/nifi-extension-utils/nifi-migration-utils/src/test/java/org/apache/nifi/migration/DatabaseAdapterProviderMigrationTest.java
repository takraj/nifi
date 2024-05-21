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
package org.apache.nifi.migration;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.apache.nifi.migration.DatabaseAdapterProviderMigration.GENERIC_DATABASE_ADAPTER_PROVIDER_CLASSNAME;
import static org.apache.nifi.migration.DatabaseAdapterProviderMigration.MSSQL_DATABASE_ADAPTER_PROVIDER_CLASSNAME;
import static org.apache.nifi.migration.DatabaseAdapterProviderMigration.MYSQL_DATABASE_ADAPTER_PROVIDER_CLASSNAME;
import static org.apache.nifi.migration.DatabaseAdapterProviderMigration.ORACLE_DATABASE_ADAPTER_PROVIDER_CLASSNAME;
import static org.apache.nifi.migration.DatabaseAdapterProviderMigration.PHOENIX_DATABASE_ADAPTER_PROVIDER_CLASSNAME;
import static org.apache.nifi.migration.DatabaseAdapterProviderMigration.POSTGRESQL_DATABASE_ADAPTER_PROVIDER_CLASSNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.stream.Stream;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.util.MockPropertyConfiguration;
import org.apache.nifi.util.MockPropertyConfiguration.CreatedControllerService;
import org.apache.nifi.util.PropertyMigrationResult;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DatabaseAdapterProviderMigrationTest {

    private static final PropertyDescriptor DATABASE_ADAPTER_PROVIDER = new PropertyDescriptor.Builder()
            .name("db-adapter-provider")
            .build();

    private static final String DB_TYPE = "db-type";

    @ParameterizedTest
    @MethodSource("provideArgs")
    void testMigrateDatabaseTypeProperty(String serviceClassname, String dbType, boolean specifyDbType) {
        final Map<String, String> properties = Map.of(
                DB_TYPE, dbType
        );
        final MockPropertyConfiguration config = new MockPropertyConfiguration(properties);

        DatabaseAdapterProviderMigration.migrateProperties(config, DATABASE_ADAPTER_PROVIDER, DB_TYPE);

        assertFalse(config.hasProperty(DB_TYPE));
        assertTrue(config.isPropertySet(DATABASE_ADAPTER_PROVIDER));

        PropertyMigrationResult result = config.toPropertyMigrationResult();
        assertEquals(1, result.getCreatedControllerServices().size());

        final CreatedControllerService createdService = result.getCreatedControllerServices().iterator().next();

        assertEquals(config.getRawPropertyValue(DATABASE_ADAPTER_PROVIDER).get(), createdService.id());
        assertEquals(serviceClassname, createdService.implementationClassName());

        if (specifyDbType) {
            assertEquals(singletonMap(DB_TYPE, dbType), createdService.serviceProperties());
        } else {
            assertEquals(emptyMap(), createdService.serviceProperties());
        }
    }

    private static Stream<Arguments> provideArgs() {
        return Stream.of(
                Arguments.of(GENERIC_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "Generic", false),
                Arguments.of(MSSQL_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "MS SQL 2012+", true),
                Arguments.of(MSSQL_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "MS SQL 2008", true),
                Arguments.of(MYSQL_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "MySQL", false),
                Arguments.of(ORACLE_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "Oracle", true),
                Arguments.of(ORACLE_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "Oracle 12+", true),
                Arguments.of(PHOENIX_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "Phoenix", false),
                Arguments.of(POSTGRESQL_DATABASE_ADAPTER_PROVIDER_CLASSNAME, "PostgreSQL", false)
        );
    }
}
