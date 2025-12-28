/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.   You may obtain a copy of the License at
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
package org.apache.fesod.cli.config;

/**
 * CLI configuration
 */
public class CliConfig {
    
    private DefaultsConfig defaults = new DefaultsConfig();
    private ReadConfig read = new ReadConfig();
    private WriteConfig write = new WriteConfig();
    
    public DefaultsConfig getDefaults() {
        return defaults;
    }
    
    public void setDefaults(DefaultsConfig defaults) {
        this.defaults = defaults;
    }
    
    public ReadConfig getRead() {
        return read;
    }
    
    public void setRead(ReadConfig read) {
        this.read = read;
    }
    
    public WriteConfig getWrite() {
        return write;
    }
    
    public void setWrite(WriteConfig write) {
        this.write = write;
    }
    
    public static class DefaultsConfig {
        private String outputFormat = "json";
        private String encoding = "UTF-8";
        
        public String getOutputFormat() {
            return outputFormat;
        }
        
        public void setOutputFormat(String outputFormat) {
            this.outputFormat = outputFormat;
        }
        
        public String getEncoding() {
            return encoding;
        }
        
        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }
    }
    
    public static class ReadConfig {
        private boolean autoTrim = true;
        private boolean ignoreEmptyRows = true;
        
        public boolean isAutoTrim() {
            return autoTrim;
        }
        
        public void setAutoTrim(boolean autoTrim) {
            this.autoTrim = autoTrim;
        }
        
        public boolean isIgnoreEmptyRows() {
            return ignoreEmptyRows;
        }
        
        public void setIgnoreEmptyRows(boolean ignoreEmptyRows) {
            this.ignoreEmptyRows = ignoreEmptyRows;
        }
    }
    
    public static class WriteConfig {
        private boolean autoCreateDirectories = true;
        
        public boolean isAutoCreateDirectories() {
            return autoCreateDirectories;
        }
        
        public void setAutoCreateDirectories(boolean autoCreateDirectories) {
            this.autoCreateDirectories = autoCreateDirectories;
        }
    }
}

