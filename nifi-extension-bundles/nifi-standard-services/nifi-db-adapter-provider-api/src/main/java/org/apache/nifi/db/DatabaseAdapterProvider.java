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

import org.apache.nifi.annotation.documentation.CapabilityDescription;
import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.controller.ControllerService;

/**
 * Definition for Database Adapter Provider Service.
 */
@Tags({"sql", "database", "syntax", "dialect", "adapter", "provider"})
@CapabilityDescription("Provides an adapter for a specific SQL dialect.")
public interface DatabaseAdapterProvider extends ControllerService {

    /**
     * Returns a specific adapter for the SQL dialect implemented by this service.
     *
     * @return An adapter instance.
     */
    DatabaseAdapter getAdapter();
}
