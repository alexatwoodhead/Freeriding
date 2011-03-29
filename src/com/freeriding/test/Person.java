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
package com.freeriding.test;

import java.util.Date;
import java.util.List;

import com.freeriding.data.Entity;
import com.freeriding.data.Global;
import com.freeriding.data.InstanceNotFoundException;
import com.freeriding.data.Subscript;

public class Person extends Entity {

	///@Override
	protected void setGlobal()
	{
		global=new Global("Test",new Subscript("People"));
	}
	/*
	protected OpenListener openListener=new OpenListener(){
		public void OnOpen(Entity entity) {
			//this.surname=entity.
		}
	}; */
	
	public Person()
	{
		super();
	}
	public Person(long identity) throws InstanceNotFoundException
	{
		super(identity);
		this.surname=GetStringProperty("Surname");
		this.forename=GetStringProperty("Forename");
		this.dateOfBirth=GetDateProperty("DateOfBirth");
	}
	///@Override
	protected void OnSave()
	{
		SetStringProperty("Surname",this.surname);
		SetStringProperty("Forename",this.forename);
		SetDateProperty("DateOfBirth",this.dateOfBirth);
	}
	///@Override
	protected boolean OnValidate(List<String> validationErrors) {return true;}
	
	private String surname;
	private String forename;
	private Date dateOfBirth;
	
	public String getSurname() { return this.surname;}
	public void setSurname(String name) { this.surname=name;}
	
	public String getForename() { return this.forename;}
	public void setForename(String name) { this.forename=name;}
	
	public Date getDateOfBirth() { return this.dateOfBirth;}
	public void setDateOfBirth(Date date) { this.dateOfBirth=date;}

	
}
