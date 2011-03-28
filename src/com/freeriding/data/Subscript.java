/*
 * Copyright Alex Woodhead 2011 - alexatwoodhead@gmail.com
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.freeriding.data;

import com.intersys.globals.NodeReference;
/// NodeReferenceWrapper to encapsulate calling correct
/// addSubscript overload
public class Subscript {
	
	private String nameS;
	private Double nameD;
	private Long nameL;
	private Integer nameI;
	private Mode mode;
	
	public Subscript(int value)
	{
		mode=Mode.INTEGER;
		nameI=new Integer(value);
	}
	public Subscript(long value)
	{
		mode=Mode.LONG;
		nameL=new Long(value);
	}
	public Subscript(Double value)
	{
		mode=Mode.DOUBLE;
		nameD=new Double(value);
	}
	public Subscript(String value)
	{
		mode=Mode.STRING;
		nameS=value;
	}
	public void AppendSubscript(NodeReference nodeReference)
	{
		switch (mode)
		{
			case DOUBLE:
				nodeReference.appendSubscript(nameD.doubleValue());
				break;
			case INTEGER:
				nodeReference.appendSubscript(nameI.intValue());
				break;
			case LONG:
				nodeReference.appendSubscript(nameL.longValue());
				break;
			case STRING:
				nodeReference.appendSubscript(nameS);
				break;
		}
	}
	
	/// Overloaded for use by NodeReferenceBase
	public String toString()
	{
		switch (mode)
		{
			case DOUBLE:
				return nameD.toString();
			case INTEGER:
				return nameI.toString();
			case LONG:
				return nameL.toString();
			default:
				return ("\""+nameS+"\"");
		}
		
	}
	
	enum Mode
	{
		STRING,
		DOUBLE,
		LONG,
		INTEGER
	}
}
