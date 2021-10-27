/*******************************************************************************
 * (c) Copyright 2020 Micro Focus or one of its affiliates
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including without 
 * limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to 
 * whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included 
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY 
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE 
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN 
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 ******************************************************************************/
package com.fortify.cli.common.output;

import java.util.function.Function;

import com.fortify.cli.common.json.transformer.FieldBasedTransformer.FieldNameFormatter;
import com.fortify.cli.common.output.csv.CsvOutputWriterFactory;
import com.fortify.cli.common.output.json.JsonOutputWriterFactory;
import com.fortify.cli.common.output.table.TableOutputWriterFactory;
import com.fortify.cli.common.output.tree.TreeOutputWriterFactory;
import com.fortify.cli.common.output.xml.XmlOutputWriterFactory;
import com.fortify.cli.common.output.yaml.YamlOutputWriterFactory;

import lombok.Getter;

public enum OutputFormat {
	json (OutputType.TECHNICAL,    new JsonOutputWriterFactory(),  FieldNameFormatter::camelCase), 
	yaml (OutputType.TECHNICAL,    new YamlOutputWriterFactory(),  FieldNameFormatter::snakeCase), 
	table(OutputType.TEXT_COLUMNS, new TableOutputWriterFactory(), FieldNameFormatter::humanReadable), 
	tree (OutputType.TEXT_ROWS,    new TreeOutputWriterFactory(),  FieldNameFormatter::humanReadable), 
	xml  (OutputType.TECHNICAL,    new XmlOutputWriterFactory(),   FieldNameFormatter::camelCase), 
	csv  (OutputType.TEXT_COLUMNS, new CsvOutputWriterFactory(),   FieldNameFormatter::humanReadable);
	
	@Getter private final OutputType               outputType; 
	@Getter private final IOutputWriterFactory     outputWriterFactory;
	@Getter private final Function<String, String> fieldNameFormatter;
	private OutputFormat(OutputType outputType, IOutputWriterFactory outputWriterFactory, Function<String, String> fieldNameformatter) {
		this.outputType = outputType;
		this.outputWriterFactory = outputWriterFactory;
		this.fieldNameFormatter = fieldNameformatter;
	}
	
	public static enum OutputType { TEXT_ROWS, TEXT_COLUMNS, TECHNICAL }
}
