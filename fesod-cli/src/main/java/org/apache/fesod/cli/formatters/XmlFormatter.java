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
package org.apache.fesod.cli.formatters;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * XML formatter implementation
 */
public class XmlFormatter implements OutputFormatter {

    @Override
    public String format(Map<String, Object> data) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.newDocument();

            // Create root element
            org.w3c.dom.Element rootElement = doc.createElement("fesod-result");
            doc.appendChild(rootElement);

            // Convert data to XML
            convertToXml(doc, rootElement, data);

            // Transform to string
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));

            return writer.toString();

        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException("Failed to format as XML: " + e.getMessage(), e);
        }
    }

    private void convertToXml(org.w3c.dom.Document doc, org.w3c.dom.Element parent, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            org.w3c.dom.Element element = doc.createElement(key.replaceAll("[^a-zA-Z0-9]", "_"));

            if (value instanceof Map) {
                convertToXml(doc, element, (Map<String, Object>) value);
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (int i = 0; i < array.size(); i++) {
                    Object item = array.get(i);
                    org.w3c.dom.Element itemElement = doc.createElement("item");

                    if (item instanceof JSONObject) {
                        convertToXml(doc, itemElement, (JSONObject) item);
                    } else {
                        itemElement.setTextContent(item != null ? item.toString() : "");
                    }

                    element.appendChild(itemElement);
                }
            } else {
                element.setTextContent(value != null ? value.toString() : "");
            }

            parent.appendChild(element);
        }
    }

    private void convertToXml(org.w3c.dom.Document doc, org.w3c.dom.Element parent, JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            org.w3c.dom.Element element = doc.createElement(key.replaceAll("[^a-zA-Z0-9]", "_"));
            element.setTextContent(value != null ? value.toString() : "");
            parent.appendChild(element);
        }
    }

    @Override
    public void writeToFile(String content, Path outputPath) {
        try {
            Files.write(outputPath, content.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write XML to file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFormatType() {
        return "xml";
    }
}

