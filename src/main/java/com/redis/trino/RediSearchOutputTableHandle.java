/*
 * MIT License
 *
 * Copyright (c) 2022, Redis Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.redis.trino;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.trino.spi.connector.ConnectorOutputTableHandle;
import io.trino.spi.connector.SchemaTableName;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class RediSearchOutputTableHandle implements ConnectorOutputTableHandle {
	private final SchemaTableName schemaTableName;
	private final List<RediSearchColumnHandle> columns;

	@JsonCreator
	public RediSearchOutputTableHandle(@JsonProperty("schemaTableName") SchemaTableName schemaTableName,
			@JsonProperty("columns") List<RediSearchColumnHandle> columns) {
		this.schemaTableName = requireNonNull(schemaTableName, "schemaTableName is null");
		this.columns = ImmutableList.copyOf(requireNonNull(columns, "columns is null"));
	}

	@JsonProperty
	public SchemaTableName getSchemaTableName() {
		return schemaTableName;
	}

	@JsonProperty
	public List<RediSearchColumnHandle> getColumns() {
		return columns;
	}
}
