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

package org.apache.fesod.excel.enums;

import java.nio.charset.Charset;
import java.util.Map;
import lombok.Getter;
import org.apache.commons.io.ByteOrderMark;
import org.apache.fesod.excel.util.MapUtils;

/**
 * byte order mark
 *
 *
 */
@Getter
public enum ByteOrderMarkEnum {

    /**
     * UTF_8
     */
    UTF_8(ByteOrderMark.UTF_8),
    /**
     * UTF_16BE
     */
    UTF_16BE(ByteOrderMark.UTF_16BE),
    /**
     * UTF_16LE
     */
    UTF_16LE(ByteOrderMark.UTF_16LE),
    /**
     * UTF_32BE
     */
    UTF_32BE(ByteOrderMark.UTF_32BE),
    /**
     * UTF_32LE
     */
    UTF_32LE(ByteOrderMark.UTF_32LE);

    final ByteOrderMark byteOrderMark;
    final String stringPrefix;

    ByteOrderMarkEnum(ByteOrderMark byteOrderMark) {
        this.byteOrderMark = byteOrderMark;
        Charset charset = Charset.forName(byteOrderMark.getCharsetName());
        this.stringPrefix = new String(byteOrderMark.getBytes(), charset);
    }

    /**
     * store character aliases corresponding to `ByteOrderMark` prefix
     */
    private static final Map<String, ByteOrderMarkEnum> CHARSET_BYTE_ORDER_MARK_MAP = MapUtils.newHashMap();

    static {
        for (ByteOrderMarkEnum value : ByteOrderMarkEnum.values()) {
            CHARSET_BYTE_ORDER_MARK_MAP.put(value.getByteOrderMark().getCharsetName(), value);
        }
    }

    public static ByteOrderMarkEnum valueOfByCharsetName(String charsetName) {
        return CHARSET_BYTE_ORDER_MARK_MAP.get(charsetName);
    }
}
