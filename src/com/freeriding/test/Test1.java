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

import com.freeriding.data.Entity;
import com.freeriding.test.Person;

public class Test1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		Entity.Initialise();
		/*Person person=new Person();
		person.setSurname("Smith");
		person.setForename("John");
		person.Save();
		System.out.println(String.format("Person id is '%1$s'",person.getIdentity()));
		*/
		try
		{
	      Person person=new Person(4);
	      System.out.println(String.format("Person id is '%1$s'",person.getIdentity()));
	      System.out.println(String.format("  Surname: '%1$s'",person.getSurname()));
	      System.out.println(String.format("  Forename: '%1$s'",person.getForename()));
	      
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		
		
		Entity.Stop();
	}

}
