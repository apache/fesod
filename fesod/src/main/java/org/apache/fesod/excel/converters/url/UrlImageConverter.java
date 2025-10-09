/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.fesod.excel.converters.url;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.fesod.excel.converters.Converter;
import org.apache.fesod.excel.metadata.GlobalConfiguration;
import org.apache.fesod.excel.metadata.data.WriteCellData;
import org.apache.fesod.excel.metadata.property.ExcelContentProperty;
import org.apache.fesod.excel.util.IoUtils;

/**
 * Url and image converter
 *
 *
 * @since 2.1.1
 */
public class UrlImageConverter implements Converter<URL> {
    public static int urlConnectTimeout = 1000;
    public static int urlReadTimeout = 5000;

    @Override
    public Class<?> supportJavaTypeKey() {
        return URL.class;
    }

    @Override
    public WriteCellData<?> convertToExcelData(
            URL value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration)
            throws IOException {
        InputStream inputStream = null;
        try {
            URLConnection urlConnection = value.openConnection();
            urlConnection.setConnectTimeout(urlConnectTimeout);
            urlConnection.setReadTimeout(urlReadTimeout);
            inputStream = urlConnection.getInputStream();
            byte[] bytes = IoUtils.toByteArray(inputStream);
            return new WriteCellData<>(bytes);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
