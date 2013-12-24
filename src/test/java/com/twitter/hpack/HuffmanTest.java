/*
 * Copyright 2013 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.hpack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

public class HuffmanTest {

  private HuffmanEncoder requestEncoder;

  private HuffmanDecoder requestDecoder;

  private HuffmanEncoder responseEncoder;

  private HuffmanDecoder responseDecoder;

  @Before
  public void setUp() {
    requestEncoder = HpackUtil.REQUEST_ENCODER;
    requestDecoder = HpackUtil.REQUEST_DECODER;
    responseEncoder = HpackUtil.RESPONSE_ENCODER;
    responseDecoder = HpackUtil.RESPONSE_DECODER;
  }

  @Test
  public void testHuffman() throws IOException {

    String s = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    for (int i = 0; i < s.length(); i++) {
      roundTrip(s.substring(0, i));
    }

    Random random = new Random(123456789L);
    byte[] buf = new byte[4096];
    random.nextBytes(buf);
    roundTrip(buf);
  }

  private void roundTrip(String s) throws IOException {
    roundTrip(requestEncoder, requestDecoder, s);
    roundTrip(responseEncoder, responseDecoder, s);
  }

  private static void roundTrip(HuffmanEncoder encoder, HuffmanDecoder decoder, String s) throws IOException {
    roundTrip(encoder, decoder, s.getBytes());
  }

  private void roundTrip(byte[] buf) throws IOException {
    roundTrip(requestEncoder, requestDecoder, buf);
    roundTrip(responseEncoder, responseDecoder, buf);
  }

  private static void roundTrip(HuffmanEncoder encoder, HuffmanDecoder decoder, byte[] buf) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);

    encoder.encode(buf, dos);

    byte[] actualBytes = decoder.decode(baos.toByteArray());

    Assert.assertTrue(Arrays.equals(buf, actualBytes));
  }
}