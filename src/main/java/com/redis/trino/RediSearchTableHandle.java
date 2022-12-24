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

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.trino.spi.connector.ColumnHandle;
import io.trino.spi.connector.ConnectorTableHandle;
import io.trino.spi.connector.SchemaTableName;
import io.trino.spi.predicate.TupleDomain;

public class RediSearchTableHandle implements ConnectorTableHandle {

	public enum Type {
		SEARCH, AGGREGATE
	}

	private final Type type;
	private final SchemaTableName schemaTableName;
	private final TupleDomain<ColumnHandle> constraint;
	private final OptionalLong limit;
	// for group by fields
	private final List<RediSearchAggregationTerm> aggregationTerms;
	private final List<RediSearchAggregation> aggregations;
	private final Map<String, String> wildcards;

	public RediSearchTableHandle(Type type, SchemaTableName schemaTableName) {
		this(type, schemaTableName, TupleDomain.all(), OptionalLong.empty(), Collections.emptyList(),
				Collections.emptyList(), Map.of());
	}

	@JsonCreator
	public RediSearchTableHandle(@JsonProperty("type") Type type,
			@JsonProperty("schemaTableName") SchemaTableName schemaTableName,
			@JsonProperty("constraint") TupleDomain<ColumnHandle> constraint, @JsonProperty("limit") OptionalLong limit,
			@JsonProperty("aggTerms") List<RediSearchAggregationTerm> aggregationTerms,
			@JsonProperty("aggregates") List<RediSearchAggregation> aggregates,
			@JsonProperty("wildcards") Map<String, String> wildcards) {
		this.type = requireNonNull(type, "type is null");
		this.schemaTableName = requireNonNull(schemaTableName, "schemaTableName is null");
		this.constraint = requireNonNull(constraint, "constraint is null");
		this.limit = requireNonNull(limit, "limit is null");
		this.aggregationTerms = requireNonNull(aggregationTerms, "aggTerms is null");
		this.aggregations = requireNonNull(aggregates, "aggregates is null");
		this.wildcards = requireNonNull(wildcards, "wildcards is null");
	}

	@JsonProperty
	public Type getType() {
		return type;
	}

	@JsonProperty
	public SchemaTableName getSchemaTableName() {
		return schemaTableName;
	}

	@JsonProperty
	public TupleDomain<ColumnHandle> getConstraint() {
		return constraint;
	}

	@JsonProperty
	public OptionalLong getLimit() {
		return limit;
	}

	@JsonProperty
	public List<RediSearchAggregationTerm> getAggregationTerms() {
		return aggregationTerms;
	}

	@JsonProperty
	public List<RediSearchAggregation> getAggregations() {
		return aggregations;
	}

	@JsonProperty
	public Map<String, String> getWildcards() {
		return wildcards;
	}

	@Override
	public int hashCode() {
		return Objects.hash(schemaTableName, constraint, limit);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		RediSearchTableHandle other = (RediSearchTableHandle) obj;
		return Objects.equals(this.schemaTableName, other.schemaTableName)
				&& Objects.equals(this.constraint, other.constraint) && Objects.equals(this.limit, other.limit);
	}

	@Override
	public String toString() {
		return schemaTableName.toString();
	}
}
