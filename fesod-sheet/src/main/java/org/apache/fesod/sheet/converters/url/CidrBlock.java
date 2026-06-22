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

package org.apache.fesod.sheet.converters.url;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import lombok.EqualsAndHashCode;

/**
 * CIDR block matcher for URL image fetch allowlists.
 */
@EqualsAndHashCode
public final class CidrBlock {

    private final String value;
    private final byte[] networkAddress;
    private final int prefixLength;

    private CidrBlock(String value, byte[] networkAddress, int prefixLength) {
        this.value = value;
        this.networkAddress = Arrays.copyOf(networkAddress, networkAddress.length);
        this.prefixLength = prefixLength;
    }

    public String getValue() {
        return value;
    }

    public byte[] getNetworkAddress() {
        return Arrays.copyOf(networkAddress, networkAddress.length);
    }

    public int getPrefixLength() {
        return prefixLength;
    }

    public static CidrBlock parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("CIDR block can not be null");
        }
        String[] parts = value.trim().split("/", -1);
        if (parts.length != 2) {
            throw new IllegalArgumentException("CIDR block must use address/prefix format");
        }

        try {
            InetAddress address = InetAddress.getByName(parts[0]);
            byte[] addressBytes = address.getAddress();
            int maxPrefixLength = addressBytes.length * Byte.SIZE;
            int prefixLength = Integer.parseInt(parts[1]);
            if (prefixLength < 0 || prefixLength > maxPrefixLength) {
                throw new IllegalArgumentException("CIDR prefix length is out of range");
            }
            return new CidrBlock(value.trim(), mask(addressBytes, prefixLength), prefixLength);
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("CIDR address is invalid", e);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("CIDR prefix length is invalid", e);
        }
    }

    public boolean contains(InetAddress address) {
        byte[] addressBytes = address.getAddress();
        if (addressBytes.length != networkAddress.length) {
            return false;
        }
        return Arrays.equals(mask(addressBytes, prefixLength), networkAddress);
    }

    private static byte[] mask(byte[] addressBytes, int prefixLength) {
        int bitLength = addressBytes.length * Byte.SIZE;
        BigInteger address = new BigInteger(1, addressBytes);
        BigInteger mask = BigInteger.ONE
                .shiftLeft(bitLength)
                .subtract(BigInteger.ONE)
                .shiftRight(bitLength - prefixLength)
                .shiftLeft(bitLength - prefixLength);
        byte[] maskedBytes = address.and(mask).toByteArray();
        return toFixedLength(maskedBytes, addressBytes.length);
    }

    private static byte[] toFixedLength(byte[] bytes, int length) {
        byte[] result = new byte[length];
        int copyLength = Math.min(bytes.length, length);
        System.arraycopy(bytes, bytes.length - copyLength, result, length - copyLength, copyLength);
        return result;
    }
}
